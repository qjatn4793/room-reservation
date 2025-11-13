package com.rr.pay.messaging

import com.rr.common.booking.BookingRequestedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class BookingConsumers {

    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${app.topics.bookingRequested}"],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun onBookingRequested(event: BookingRequestedEvent) {
        log.info("Consumed BookingRequestedEvent: {}", event)
        // TODO: 결제 프로세스 트리거, 재고 확인 등
    }
}
