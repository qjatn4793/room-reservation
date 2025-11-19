// services/pay/src/main/kotlin/com/rr/pay/messaging/PaymentRequestConsumers.kt
package com.rr.pay.messaging

import com.rr.common.events.PaymentAuthorizedEvent
import com.rr.common.events.PaymentFailedEvent
import com.rr.common.events.PaymentRequestedEvent
import com.rr.pay.domain.ReservationStatus
import com.rr.pay.repository.ReservationRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID
import java.security.SecureRandom

@Component
class PaymentRequestConsumers(
    private val reservationRepository: ReservationRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${app.topics.paymentAuthorized}") private val paymentAuthorizedTopic: String,
    @Value("\${app.topics.paymentFailed}") private val paymentFailedTopic: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val random = SecureRandom()

    @KafkaListener(
        topics = ["\${app.topics.paymentRequested}"],
        groupId = "\${app.kafka.groups.pay-payment}"   // 결제 전용 그룹
    )
    @Transactional
    fun onPaymentRequested(event: PaymentRequestedEvent) {
        log.info("pay-service: Consumed PaymentRequestedEvent: {}", event)

        val reservation = reservationRepository.findById(event.reservationId)
            .orElseThrow {
                IllegalStateException("Reservation ${event.reservationId} not found")
            }

        // 상태 체크: HOLD_PENDING 상태에서만 결제 진행
        if (reservation.status != ReservationStatus.HOLD_PENDING) {
            log.warn(
                "Reservation {} status is {}, expected HOLD. Skip payment.",
                reservation.id, reservation.status
            )

            reservation.deny()
            publishPaymentFailed(event, "PG_DECLINED")
            return
        }

        // 가짜 PG 호출
        val success = callFakePg(event)

        if (success) {
            // 결제 성공 → 예약 확정
            reservation.confirm()
            log.info("Reservation {} status -> CONFIRMED", reservation.id)

            publishPaymentAuthorized(event)
        } else {
            // 결제 실패 → 예약 취소
            reservation.cancel()
            log.info("Reservation {} status -> CANCELLED (payment failed)", reservation.id)

            publishPaymentFailed(event, "PG_DECLINED")
        }
    }

    /**
     * 가짜 PG 호출 로직
     * 지금은 단순하게 80% 확률 성공으로 시뮬레이션
     */
    private fun callFakePg(event: PaymentRequestedEvent): Boolean {
        // TODO: 나중에 실제 PG 연동으로 교체
        val r = random.nextInt(10) // 0~9
        val success = r < 8
        log.info(
            "Fake PG result for reservation {}: {}",
            event.reservationId,
            if (success) "SUCCESS" else "FAIL"
        )
        return success
    }

    private fun publishPaymentAuthorized(event: PaymentRequestedEvent) {
        val authEvent = PaymentAuthorizedEvent(
            reservationId = event.reservationId,
            userId = event.userId,
            amount = event.amount,
            currency = event.currency,
            authId = "PG-" + UUID.randomUUID().toString(),
            authorizedAt = Instant.now()
        )

        kafkaTemplate.send(paymentAuthorizedTopic, authEvent.reservationId.toString(), authEvent)
            .whenComplete { _, ex ->
                if (ex != null) {
                    log.error(
                        "Failed to publish PaymentAuthorizedEvent for reservation {}: {}",
                        authEvent.reservationId, ex.message, ex
                    )
                } else {
                    log.info("222 Published PaymentAuthorizedEvent for reservation {}", authEvent.reservationId)
                }
            }
    }

    private fun publishPaymentFailed(event: PaymentRequestedEvent, reason: String) {
        val failEvent = PaymentFailedEvent(
            reservationId = event.reservationId,
            userId = event.userId,
            amount = event.amount,
            currency = event.currency,
            reason = reason,
            failedAt = Instant.now()
        )

        kafkaTemplate.send(paymentFailedTopic, failEvent.reservationId.toString(), failEvent)
            .whenComplete { _, ex ->
                if (ex != null) {
                    log.error(
                        "Failed to publish PaymentFailedEvent for reservation {}: {}",
                        failEvent.reservationId, ex.message, ex
                    )
                } else {
                    log.info(
                        "Published PaymentFailedEvent for reservation {} (reason={})",
                        failEvent.reservationId, reason
                    )
                }
            }
    }
}
