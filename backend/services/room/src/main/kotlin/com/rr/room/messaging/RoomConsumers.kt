package com.rr.room.messaging

import com.rr.common.rooms.RoomCreatedEvent
import com.rr.common.rooms.RoomStatusUpdatedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class RoomConsumers {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["\${app.topics.roomCreated}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun onCreated(event: RoomCreatedEvent) {
        log.info("Consumed RoomCreatedEvent: {}", event)
        // TODO: 사후 처리(예: 캐시 warm-up) 필요 시
    }

    @KafkaListener(topics = ["\${app.topics.roomStatusUpdated}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun onStatus(event: RoomStatusUpdatedEvent) {
        log.info("Consumed RoomStatusUpdatedEvent: {}", event)
    }
}
