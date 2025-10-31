package com.rr.room.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfig(
    @Value("\${app.topics.roomCreated}") private val roomCreated: String,
    @Value("\${app.topics.roomStatusUpdated}") private val roomStatusUpdated: String
) {
    @Bean fun topicRoomCreated() = NewTopic(roomCreated, 3, 1)
    @Bean fun topicRoomStatusUpdated() = NewTopic(roomStatusUpdated, 3, 1)
}
