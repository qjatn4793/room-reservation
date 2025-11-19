package com.rr.room.messaging

import com.rr.common.booking.BookingRequestedEvent
import com.rr.common.events.HoldApprovedEvent
import com.rr.common.events.HoldDeniedEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class RoomHoldProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${app.topics.holdApproved}") private val holdApprovedTopic: String,
    @Value("\${app.topics.holdDenied}") private val holdDeniedTopic: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun publishHoldApproved(event: BookingRequestedEvent) {
        val holdEvent = HoldApprovedEvent(
            reservationId = event.reservationId,
            roomId = event.roomId,
            userId = event.userId,
            checkIn = event.checkIn,
            checkOut = event.checkOut,
            holdExpiresAt = Instant.now().plus(10, ChronoUnit.MINUTES)
        )

        kafkaTemplate.send(holdApprovedTopic, event.reservationId.toString(), holdEvent)
            .whenComplete { _, ex ->
                if (ex != null) {
                    log.error("publishHoldApproved failed: {}", ex.message, ex)
                } else {
                    log.info("Published HoldApprovedEvent for reservation {}", event.reservationId)
                }
            }
    }

    fun publishHoldDenied(event: BookingRequestedEvent, reason: String) {
        val holdEvent = HoldDeniedEvent(
            reservationId = event.reservationId,
            roomId = event.roomId,
            userId = event.userId,
            checkIn = event.checkIn,
            checkOut = event.checkOut,
            reason = reason
        )

        kafkaTemplate.send(holdDeniedTopic, event.reservationId.toString(), holdEvent)
            .whenComplete { _, ex ->
                if (ex != null) {
                    log.error("publishHoldDenied failed: {}", ex.message, ex)
                } else {
                    log.info("Published HoldDeniedEvent for reservation {} ({})", event.reservationId, reason)
                }
            }
    }
}