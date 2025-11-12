# GoodStay (여기어때 클론 프론트 뼈대)

React + TypeScript + Vite + React Query + Zustand + Tailwind

## 설치
```bash
pnpm i    # or npm i / yarn
cp .env.example .env
pnpm dev  # http://localhost:5173
```

`.env`의 `VITE_API_BASE`를 게이트웨이 엔드포인트로 설정하세요. 예) http://localhost:8080

### 라우트
- `/` 홈 + 검색바
- `/stays` 검색 결과 목록 (쿼리스트링: q, checkIn, checkOut, people)
- `/stays/:stayId` 상세/객실 선택
- `/booking/:roomId` 예약 폼 → 결제 요청
- `/payment/result` 결제 결과
- `/mypage` 마이 영역
