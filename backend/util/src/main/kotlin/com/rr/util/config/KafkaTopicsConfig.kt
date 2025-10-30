package com.rr.util.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaTopicsConfig(
    @Value("\${app.topics.holdRequested}") private val t1: String,
    @Value("\${app.topics.holdApproved}")  private val t2: String,
    @Value("\${app.topics.holdDenied}")    private val t3: String,
    @Value("\${app.topics.confirmed}")     private val t4: String,
    @Value("\${app.topics.cancelled}")     private val t5: String,
    @Value("\${app.topics.paymentRequested}")  private val t6: String,
    @Value("\${app.topics.paymentAuthorized}") private val t7: String,
    @Value("\${app.topics.paymentFailed}")     private val t8: String
) {
    @Bean fun holdRequested()  = NewTopic(t1, 3, 1)
    @Bean fun holdApproved()   = NewTopic(t2, 3, 1)
    @Bean fun holdDenied()     = NewTopic(t3, 3, 1)
    @Bean fun confirmed()      = NewTopic(t4, 3, 1)
    @Bean fun cancelled()      = NewTopic(t5, 3, 1)
    @Bean fun payRequested()   = NewTopic(t6, 3, 1)
    @Bean fun payAuthorized()  = NewTopic(t7, 3, 1)
    @Bean fun payFailed()      = NewTopic(t8, 3, 1)
}
