package com.rr.user.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

/**
 * JWT 설정
 */
@Component
class JwtUtil {
    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val accessTokenValidity = 1000 * 60 * 15L // 15분
    private val refreshTokenValidity = 1000 * 60 * 60 * 24 * 7L // 7일

    fun generateAccessToken(username: String): String {
        val now = Date()
        val expiry = Date(now.time + accessTokenValidity)
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey)
            .compact()
    }

    fun generateRefreshToken(username: String): String {
        val now = Date()
        val expiry = Date(now.time + refreshTokenValidity)
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean = try {
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
        true
    } catch (e: Exception) { false }

    fun getUsername(token: String): String =
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).body.subject
}