# RR MSA — Gateway + Room Service (Kafka + Postgres)

본 문서는 현재 구현된 **Gateway → Room Service → Postgres + Kafka** 흐름을 기준으로
실행/테스트/검증 절차와 핵심 코드 앵커를 정리합니다.

---

## 1) 아키텍처 개요

```
Client ─▶ Gateway(8080) ─▶ Room Service(8081) ─▶ Postgres(room_reservation)
                                   └────────────▶ Kafka (rr.room.created / rr.room.status-updated)
```

- **Gateway**
  - `/api/rooms/**` → `http://localhost:8081` 라우팅 (`StripPrefix=1`)
  - `RequestIdFilter`: `X-Request-Id` 생성/전파
  - 공통 CORS 적용
- **Room Service**
  - REST API: 생성/상태변경/조회
  - JPA/Hibernate로 `rooms` 테이블 영속화
  - Kafka 이벤트 발행(`room.created`, `room.status-updated`)
  - (권장) 트랜잭션 커밋 이후에만 publish: `afterCommit` 훅
- **Infra**
  - Postgres(room): `localhost:5432` / DB `room_reservation` / user `room` / pw `roompw`
  - Kafka: 호스트 접속 `localhost:29092`, 컨테이너 내부 `kafka:9092`

---

## 2) 사전 준비 (Docker Compose)

필수 서비스 기동:
```bash
docker compose -p rr up -d postgres_room zookeeper kafka
docker compose -p rr ps
```

- Postgres(room) Healthy 후 진행
- Kafka 브로커는 `9092(컨테이너), 29092(호스트)`를 오픈

---

## 3) 애플리케이션 실행

터미널 두 개에서 각각 실행:
```bash
# Room Service
./gradlew :services:room:bootRun

# Gateway
./gradlew :gateway:bootRun
```

헬스체크 (옵션):
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8080/actuator/health
```

---

## 4) API — 방 생성/조회/상태변경

### 4.1 방 생성 (게이트웨이 경유)
```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -H "X-Request-Id: $(uuidgen 2>/dev/null || powershell -c [guid]::NewGuid().ToString())" \
  -d '{ "name": "Twin 301", "capacity": 2 }'
```
- **201 Created** 가 정상 응답
- 저장 위치: `room_reservation.public.rooms`

### 4.2 단건 조회
```bash
curl http://localhost:8080/api/rooms/1
```

### 4.3 상태 변경
```bash
curl -X PATCH http://localhost:8080/api/rooms/1/status/MAINTENANCE
```

> IntelliJ HTTP 클라이언트/포스트맨용 파일도 제공됨  
> - `rooms.http` (IntelliJ)  
> - `rr-room-gateway.postman_collection.json` (Postman)

---

## 5) DB 검증 (Postgres)

최근 데이터 확인:
```bash
docker exec -it rr-postgres-room psql -U room -d room_reservation \
  -c "SELECT id,name,capacity,status FROM public.rooms ORDER BY id DESC LIMIT 10;"
```

카운트:
```bash
docker exec -it rr-postgres-room psql -U room -d room_reservation \
  -c "SELECT COUNT(*) FROM public.rooms;"
```

---

## 6) Kafka 검증

토픽 목록/상세:
```bash
docker exec -it rr-kafka kafka-topics --bootstrap-server kafka:9092 --list
docker exec -it rr-kafka kafka-topics --bootstrap-server kafka:9092 --describe --topic rr.room.created
docker exec -it rr-kafka kafka-topics --bootstrap-server kafka:9092 --describe --topic rr.room.status-updated
```

이벤트 소비 (처음부터, 키/타임스탬프 출력):
```bash
# room.created
docker exec -it rr-kafka kafka-console-consumer \
  --bootstrap-server kafka:9092 \
  --topic rr.room.created \
  --group rr-inspect-created \
  --from-beginning \
  --timeout-ms 15000 \
  --property print.key=true \
  --property key.separator=" | " \
  --property print.timestamp=true

# room.status-updated
docker exec -it rr-kafka kafka-console-consumer \
  --bootstrap-server kafka:9092 \
  --topic rr.room.status-updated \
  --group rr-inspect-status \
  --from-beginning \
  --timeout-ms 15000 \
  --property print.key=true \
  --property key.separator=" | " \
  --property print.timestamp=true
```

---

## 7) 실행 흐름 (시퀀스)

### 7.1 방 생성
1. `Client → Gateway`: `POST /api/rooms {name,capacity}` (+ 선택 `X-Request-Id`)  
2. `Gateway → Room`: `POST /rooms ...` (StripPrefix=1)  
3. `RoomController.create()` → `RoomService.create()`  
4. `repo.save(Room)` → Postgres `INSERT`  
5. `KafkaTemplate.send(rr.room.created, key=roomId, value=RoomCreatedEvent)`  
6. `201 Created` + `RoomResponse` 반환

### 7.2 상태 변경
1. `PATCH /api/rooms/{id}/status/{status}`  
2. `findById → 수정 → save`  
3. `send(rr.room.status-updated, key=roomId, value=RoomStatusUpdatedEvent)`  
4. `200 OK`

---

## 8) 코드 앵커 (파일 경로)

- Gateway
  - `gateway/src/main/resources/application.yml` — 라우팅, 필터
  - `gateway/.../filter/RequestIdFilter.kt` — X-Request-Id 주입
  - `gateway/.../config/CorsConfig.kt` — CORS
- Common
  - `common/.../com/rr/common/rooms/RoomDtos.kt` — DTO & 이벤트
- Room Service
  - `services/room/.../api/RoomController.kt` — REST 엔드포인트
  - `services/room/.../service/RoomService.kt` — 트랜잭션, 이벤트 발행
  - `services/room/.../domain/Room.kt` `RoomRepository.kt` — JPA
  - `services/room/.../messaging/RoomProducer.kt` — Kafka 프로듀서
  - `services/room/.../messaging/RoomConsumers.kt` — Kafka 컨슈머(후속 훅)
  - `services/room/.../config/KafkaConfig.kt` — 토픽 생성
  - `services/room/src/main/resources/application.yml` — DB/Kafka/topic 설정

---

## 9) (권장) 커밋 이후 발행 보장

트랜잭션 커밋 후에만 Kafka로 publish:
```kotlin
TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
    override fun afterCommit() {
        producer.publishCreated(RoomCreatedEvent(roomId = saved.id!!, name = saved.name, capacity = saved.capacity))
    }
})
```

---

## 10) 요청 상관관계 추적 (X-Request-Id)

- Gateway가 `X-Request-Id` 헤더를 부여/전파
- Room 서비스에서 MDC 필터로 로깅 상관관계 추적:
```kotlin
@Component
class RequestIdMdcFilter : Filter {
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val r = req as HttpServletRequest
        val rid = r.getHeader("X-Request-Id") ?: "N/A"
        MDC.put("rid", rid)
        try { chain.doFilter(req, res) } finally { MDC.remove("rid") }
    }
}
```

---

## 11) 트러블슈팅 요약

- **400/415 (JSON 바인딩)** : `com.fasterxml.jackson.module:jackson-module-kotlin` 추가
- **DB 연결 실패** : `jdbc:postgresql://localhost:5432/room_reservation` / `room` / `roompw` 확인
- **Kafka 연결 실패** :
  - 호스트 실행 앱: `spring.kafka.bootstrap-servers=localhost:29092`
  - 컨테이너 내부 앱: `kafka:9092`
- **메인 시그니처 오류** : Kotlin `main`은 블록 본문으로
  ```kotlin
  fun main(args: Array<String>) { runApplication<RoomApplication>(*args) }
  ```
- **Gateway 404/502** : 라우팅 `/api/rooms/**` + `StripPrefix=1` 확인

---

## 12) 부록 — 테스트 파일

- IntelliJ HTTP: `rooms.http`
- Postman: `rr-room-gateway.postman_collection.json`

두 파일은 함께 제공되며, 아래 링크에서 다운로드 가능합니다.
```