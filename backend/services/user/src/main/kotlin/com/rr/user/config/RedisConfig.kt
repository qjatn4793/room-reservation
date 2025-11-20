package com.rr.user.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

// Redis 설정
@Configuration
class RedisConfig {
    @Bean
    fun redisConnectionFactory() = LettuceConnectionFactory()

    @Bean
    fun redisTemplate(): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.setConnectionFactory(redisConnectionFactory())
        return template
    }
}
