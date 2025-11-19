package com.rr.room.messaging

import com.rr.common.booking.BookingRequestedEvent
import com.rr.common.events.HoldApprovedEvent
import com.rr.common.events.HoldDeniedEvent
import com.rr.room.service.RoomAvailabilityService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class BookingConsumers(
    private val roomAvailabilityService: RoomAvailabilityService,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${app.topics.holdApproved}") private val holdApprovedTopic: String,
    @Value("\${app.topics.holdDenied}") private val holdDeniedTopic: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${app.topics.bookingRequested}"],
        groupId = "\${app.kafka.groups.room-booking}"
    )
    @Transactional
    fun onBookingRequested(event: BookingRequestedEvent) {
        log.info("room-service: Consumed BookingRequestedEvent: {}", event)

        val available = roomAvailabilityService.isAvailable(
            event.roomId,
            event.checkIn,
            event.checkOut
        )

        if (!available) {
            publishHoldDenied(event, "NO_STOCK")
            return
        }

        // 재고 hold (재고 수량 -1, RoomInventory 등 업데이트)
        roomAvailabilityService.hold(
            roomId = event.roomId,
            checkIn = event.checkIn,
            checkOut = event.checkOut,
            reservationId = event.reservationId
        )

        publishHoldApproved(event)
    }

    private fun publishHoldApproved(event: BookingRequestedEvent) {
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
                    log.info("room-service: Published HoldApprovedEvent for reservation {}", event.reservationId)
                }
            }
    }

    private fun publishHoldDenied(event: BookingRequestedEvent, reason: String) {
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
                    log.info("room-service: Published HoldDeniedEvent for reservation {} ({})", event.reservationId, reason)
                }
            }
    }
}