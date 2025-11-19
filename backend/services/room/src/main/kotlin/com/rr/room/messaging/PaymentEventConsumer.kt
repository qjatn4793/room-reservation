package com.rr.room.messaging

import com.rr.common.events.PaymentAuthorizedEvent
import com.rr.common.events.PaymentFailedEvent
import com.rr.room.service.RoomAvailabilityService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PaymentEventConsumer(
    private val roomAvailabilityService: RoomAvailabilityService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${app.topics.paymentAuthorized}"],
        groupId = "room-service"
    )
    @Transactional
    fun onPaymentAuthorized(event: PaymentAuthorizedEvent) {
        log.info(
            "[PaymentEventConsumer] Received PaymentAuthorizedEvent. reservationId={}, amount={}",
            event.reservationId, event.amount
        )
        roomAvailabilityService.confirmByReservation(event.reservationId)
    }

    @KafkaListener(
        topics = ["\${app.topics.paymentFailed}"],
        groupId = "room-service"
    )
    @Transactional
    fun onPaymentFailed(event: PaymentFailedEvent) {
        log.info(
            "[PaymentEventConsumer] Received PaymentFailedEvent. reservationId={}, reason={}",
            event.reservationId, event.reason
        )
        roomAvailabilityService.releaseHold(event.reservationId)
    }
}