package com.rr.user.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class AuthService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun saveToken(username: String, token: String) {
        redisTemplate.opsForValue().set(username, token, Duration.ofMinutes(15))
    }

    fun getToken(username: String): String? {
        return redisTemplate.opsForValue()[username]
    }

    fun deleteToken(username: String) {
        redisTemplate.delete(username)
    }

    fun saveRefreshToken(username: String, refreshToken: String) {
        redisTemplate.opsForValue().set(username, refreshToken, Duration.ofDays(7))
    }

    fun getRefreshToken(username: String): String? = redisTemplate.opsForValue()[username]

    fun deleteRefreshToken(username: String) = redisTemplate.delete(username)
}