package com.rr.pay.service

import com.rr.pay.domain.Reservation
import com.rr.pay.domain.ReservationStatus
import com.rr.pay.dto.PayRedirectResponse
import com.rr.pay.dto.PaymentResultRequest
import com.rr.pay.dto.PaymentResultResponse
import com.rr.pay.messaging.PaymentProducer
import com.rr.pay.repository.ReservationRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeoutException

@Service
class PaymentService (
    private val reservationRepository: ReservationRepository,
    private val paymentProducer: PaymentProducer,
    @Value("\${app.payment.return-url-base:/payment/result}")
    private val returnUrlBase: String
){
    private val log = LoggerFactory.getLogger(PaymentService::class.java)

    /**
     * 결제 시작 API - "결제하기" 버튼에서 호출
     * 1) 예약이 HOLD 상태인지 확인
     * 2) 상태를 HOLD_PENDING(결제 진행 중) 으로 변경
     * 3) PaymentRequestedEvent 발행
     */
    @Transactional
    fun startPayment(reservationId: UUID) {
        log.info("[startPayment] START reservationId={}", reservationId)

        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reservation $reservationId not found"
                )
            }

        // 결제 시작 상태로
        reservation.status = ReservationStatus.HOLD_PENDING
        log.info("[startPayment] Reservation {} status -> HOLD_PENDING", reservationId)

        // 이제 결제 요청 이벤트 발행
        paymentProducer.publishPaymentRequested(reservation)
        log.info("[startPayment] PaymentRequestedEvent published. reservationId={}", reservationId)
    }

    /**
     * PG 콜백/웹훅에서 호출하는 성공 처리 메서드 예시
     */
    @Transactional
    fun handlePaymentSuccess(reservationId: UUID, authId: String) {
        log.info(
            "[handlePaymentSuccess] START reservationId={}, authId={}",
            reservationId, authId
        )

        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow {
                log.error(
                    "[handlePaymentSuccess] Reservation not found. reservationId={}",
                    reservationId
                )
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Reservation $reservationId not found"
                )
            }

        log.info(
            "[handlePaymentSuccess] Current status of reservationId={} is {}",
            reservationId, reservation.status
        )

        reservation.status = ReservationStatus.CONFIRMED
        log.info(
            "[handlePaymentSuccess] Reservation status changed to CONFIRMED. reservationId={}",
            reservationId
        )

        paymentProducer.publishPaymentAuthorized(reservation, authId)
        log.info(
            "[handlePaymentSuccess] Published PaymentAuthorizedEvent. reservationId={}",
            reservationId
        )
    }

    /**
     * PG 콜백/웹훅에서 호출하는 실패 처리 메서드 예시
     */
    @Transactional
    fun handlePaymentFailed(reservationId: UUID, reason: String) {
        log.info(
            "[handlePaymentFailed] START reservationId={}, reason={}",
            reservationId, reason
        )

        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow {
                log.error(
                    "[handlePaymentFailed] Reservation not found. reservationId={}",
                    reservationId
                )
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Reservation $reservationId not found"
                )
            }

        log.info(
            "[handlePaymentFailed] Current status of reservationId={} is {}",
            reservationId, reservation.status
        )

        if (reservation.status in listOf(
                ReservationStatus.CONFIRMED,
                ReservationStatus.CANCELLED,
                ReservationStatus.EXPIRED,
                ReservationStatus.DENIED
            )
        ) {
            log.info(
                "[handlePaymentFailed] Reservation already in final state ({}). Ignore failure. reservationId={}",
                reservation.status, reservationId
            )
            return
        }

        reservation.status = ReservationStatus.EXPIRED
        log.info(
            "[handlePaymentFailed] Reservation status changed to EXPIRED. reservationId={}",
            reservationId
        )

        paymentProducer.publishPaymentFailed(reservation, reason)
        log.info(
            "[handlePaymentFailed] Published PaymentFailedEvent. reservationId={}",
            reservationId
        )
    }

    @Transactional
    fun processPaymentResult(request: PaymentResultRequest): PaymentResultResponse {
        val reservationId = request.reservationId
        log.info(
            "[processPaymentResult] START reservationId={}, status={}, authId={}, reason={}",
            reservationId, request.status, request.authId, request.reason
        )

        when (request.status.uppercase()) {
            "SUCCESS" -> {
                log.info("[processPaymentResult] Handling SUCCESS. reservationId={}", reservationId)
                handlePaymentSuccess(reservationId, request.authId ?: "MOCK_AUTH_ID")
            }
            "FAIL", "CANCEL" -> {
                log.info(
                    "[processPaymentResult] Handling {}. reservationId={}",
                    request.status.uppercase(), reservationId
                )
                handlePaymentFailed(reservationId, request.reason ?: request.status.uppercase())
            }
            else -> {
                log.warn(
                    "[processPaymentResult] Unknown status '{}'. Only fetching result. reservationId={}",
                    request.status, reservationId
                )
            }
        }

        val response = getPaymentResult(reservationId)
        log.info(
            "[processPaymentResult] END reservationId={}, finalStatus={}, canRetry={}",
            response.reservationId, response.status, response.canRetry
        )
        return response
    }

    @Transactional(readOnly = true)
    fun getPaymentResult(reservationId: UUID): PaymentResultResponse {
        log.debug("[getPaymentResult] Fetching result. reservationId={}", reservationId)

        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow {
                log.error(
                    "[getPaymentResult] Reservation not found. reservationId={}",
                    reservationId
                )
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Reservation $reservationId not found"
                )
            }

        // 여기서는 "단순 조회" 이므로 timedOut = false 고정
        val response = toResultResponse(reservation, timedOut = false)

        log.info(
            "[getPaymentResult] reservationId={}, status={}, canRetry={}, message={}, timedOut={}",
            response.reservationId, response.status, response.canRetry, response.message, response.timedOut
        )

        return response
    }

    fun waitPaymentResult(
        reservationId: UUID,
        timeoutSeconds: Long = 10
    ): Mono<PaymentResultResponse> {

        val timeout = Duration.ofSeconds(timeoutSeconds)
        val interval = Duration.ofMillis(300)   // 0.3초마다 한번씩 조회

        return Flux.interval(interval)
            .flatMap {
                Mono.fromCallable {
                    reservationRepository.findById(reservationId)
                        .orElseThrow {
                            ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Reservation $reservationId not found"
                            )
                        }
                }
                    .subscribeOn(Schedulers.boundedElastic())
            }
            .filter { reservation ->
                // 더 이상 "결제 중"이 아닐 때 (즉, 결과가 난 상태)
                reservation.status != ReservationStatus.HOLD_PENDING
            }
            .next() // 첫 번째로 조건을 만족하는 값 1개만 가져옴
            .map { reservation ->
                toResultResponse(reservation, timedOut = false)
            }
            .timeout(timeout)
            .onErrorResume(TimeoutException::class.java) {
                // 10초 안에 상태가 안 바뀌면 타임아웃 응답
                Mono.fromCallable {
                    val reservation = reservationRepository.findById(reservationId)
                        .orElseThrow {
                            ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Reservation $reservationId not found on timeout"
                            )
                        }
                    toResultResponse(reservation, timedOut = true)
                }.subscribeOn(Schedulers.boundedElastic())
            }
    }

    private fun toResultResponse(
        reservation: Reservation,
        timedOut: Boolean
    ): PaymentResultResponse {
        val (message, canRetry) = when (reservation.status) {
            ReservationStatus.CONFIRMED ->
                "결제가 정상적으로 완료되었습니다." to false

            ReservationStatus.CANCELLED ->
                "사용자가 예약을 취소했습니다." to true

            ReservationStatus.EXPIRED ->
                "결제 또는 홀드 시간이 만료되었습니다." to true

            ReservationStatus.DENIED ->
                "재고 부족 등으로 예약이 거절되었습니다." to true

            ReservationStatus.HOLD ->
                "아직 결제를 시작하지 않았습니다." to true

            ReservationStatus.HOLD_PENDING ->
                "결제가 진행 중입니다." to false
        }

        return PaymentResultResponse(
            reservationId = reservation.id,
            status = reservation.status,
            message = if (timedOut) "결제 결과를 아직 확인하지 못했습니다." else message,
            canRetry = canRetry,
            timedOut = timedOut
        )
    }
}