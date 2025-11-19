package com.rr.pay.messaging

import com.rr.common.events.PaymentAuthorizedEvent
import com.rr.common.events.PaymentFailedEvent
import com.rr.common.events.PaymentRequestedEvent
import com.rr.pay.domain.Reservation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PaymentProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${app.topics.paymentRequested}") private val paymentRequestedTopic: String,
    @Value("\${app.topics.paymentAuthorized}") private val paymentAuthorizedTopic: String,
    @Value("\${app.topics.paymentFailed}") private val paymentFailedTopic: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun publishPaymentRequested(reservation: Reservation) {
        val event = PaymentRequestedEvent(
            reservationId = reservation.id,
            userId = "test", // 추후 userId 로 변경필요
            amount = reservation.amount,
            // currency 기본값 "KRW" 사용
            requestedAt = Instant.now()
        )

        kafkaTemplate.send(paymentRequestedTopic, event.reservationId.toString(), event)
            .whenComplete { _, ex ->
                if (ex != null) {
                    log.error(
                        "Failed to publish PaymentRequestedEvent for reservation {}: {}",
                        event.reservationId, ex.message, ex
                    )
                } else {
                    log.info("Published PaymentRequestedEvent for reservation {}", event.reservationId)
                }
            }
    }

    fun publishPaymentAuthorized(reservation: Reservation, authId: String) {
        val event = PaymentAuthorizedEvent(
            reservationId = reservation.id,
            userId = "test", // 추후 userId 로 변경필요
            amount = reservation.amount,
            // currency 에 기본값이 있으면 생략 가능, 없으면 "KRW" 같은 값 넘겨주기
            currency = "KRW",
            authId = authId,
            authorizedAt = Instant.now()
        )

        kafkaTemplate.send(paymentAuthorizedTopic, event.reservationId.toString(), event)
            .whenComplete { _, ex ->
                if (ex != null) {
                    log.error(
                        "Failed to publish PaymentAuthorizedEvent for reservation {}: {}",
                        event.reservationId, ex.message, ex
                    )
                } else {
                    log.info("111 Published PaymentAuthorizedEvent for reservation {}", event.reservationId)
                }
            }
    }

    fun publishPaymentFailed(reservation: Reservation, reason: String) {
        val event = PaymentFailedEvent(
            reservationId = reservation.id,
            userId = "test", // 추후 userId 로 변경필요
            amount = reservation.amount,
            currency = "KRW",
            reason = reason,
            failedAt = Instant.now()
        )

        kafkaTemplate.send(paymentFailedTopic, event.reservationId.toString(), event)
            .whenComplete { _, ex ->
                if (ex != null) {
                    log.error(
                        "Failed to publish PaymentFailedEvent for reservation {}: {}",
                        event.reservationId, ex.message, ex
                    )
                } else {
                    log.info(
                        "Published PaymentFailedEvent for reservation {} (reason={})",
                        event.reservationId, reason
                    )
                }
            }
    }
}