package com.rr.pay.messaging

import com.rr.common.booking.BookingRequestedEvent
import com.rr.pay.domain.Reservation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class BookingProducer(
    // Any 로 보내고 JsonSerializer 사용
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${app.topics.bookingRequested}") private val bookingTopic: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun publishBookingRequested(reservation: Reservation) {
        val event = BookingRequestedEvent(
            reservationId = reservation.id,
            roomId = reservation.roomId,
            userId = reservation.userId,
            checkIn = reservation.checkIn,
            checkOut = reservation.checkOut,
            amount = reservation.amount
        )

        kafkaTemplate.send(bookingTopic, reservation.id.toString(), event)
            .whenComplete { _, ex ->
                if (ex != null) {
                    log.error("publishBookingRequested failed: {}", ex.message, ex)
                } else {
                    log.info("Published BookingRequestedEvent for reservation {}", reservation.id)
                }
            }
    }
}
