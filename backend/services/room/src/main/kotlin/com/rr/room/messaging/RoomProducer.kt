package com.rr.room.messaging

import com.rr.common.rooms.RoomCreatedEvent
import com.rr.common.rooms.RoomStatusUpdatedEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class RoomProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${app.topics.roomCreated}") private val roomCreated: String,
    @Value("\${app.topics.roomStatusUpdated}") private val roomStatusUpdated: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun publishCreated(event: RoomCreatedEvent) {
        kafkaTemplate.send(roomCreated, event.roomId.toString(), event).whenComplete { _, ex ->
            if (ex != null) log.error("publishCreated failed: {}", ex.message, ex)
        }
    }

    fun publishStatus(event: RoomStatusUpdatedEvent) {
        kafkaTemplate.send(roomStatusUpdated, event.roomId.toString(), event).whenComplete { _, ex ->
            if (ex != null) log.error("publishStatus failed: {}", ex.message, ex)
        }
    }
}
