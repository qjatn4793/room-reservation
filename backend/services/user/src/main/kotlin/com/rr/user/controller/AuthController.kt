package com.rr.user.controller

import com.rr.user.security.JwtUtil
import com.rr.user.service.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val email: String)

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtUtil: JwtUtil,
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): LoginResponse {
        println(request.email)
        println(request.password)
        if (request.email == "test@naver.com" && request.password == "123456") {
            val accessToken = jwtUtil.generateAccessToken(request.email)
            val refreshToken = jwtUtil.generateRefreshToken(request.email)
            authService.saveRefreshToken(request.email, refreshToken)

            val accessCookie = Cookie("accessToken", accessToken).apply {
                isHttpOnly = true
                path = "/"
                maxAge = 15 * 60
            }
            val refreshCookie = Cookie("refreshToken", refreshToken).apply {
                isHttpOnly = true
                path = "/"
                maxAge = 7 * 24 * 60 * 60
            }

            response.addCookie(accessCookie)
            response.addCookie(refreshCookie)

            return LoginResponse(email = request.email)
        } else {
            throw RuntimeException("Invalid credentials")
        }
    }

    @PostMapping("/refresh")
    fun refresh(@CookieValue("refreshToken") refreshToken: String?, response: HttpServletResponse) {
        if (refreshToken == null) throw RuntimeException("No refresh token")

        val email = jwtUtil.getUsername(refreshToken)
        val storedToken = authService.getRefreshToken(email)
        if (storedToken != refreshToken) throw RuntimeException("Invalid refresh token")

        val newAccessToken = jwtUtil.generateAccessToken(email)
        val accessCookie = Cookie("accessToken", newAccessToken).apply {
            isHttpOnly = true
            path = "/"
            maxAge = 15 * 60
        }
        response.addCookie(accessCookie)
    }

    @PostMapping("/logout")
    fun logout(@CookieValue("refreshToken") refreshToken: String?, response: HttpServletResponse) {
        refreshToken?.let {
            val email = jwtUtil.getUsername(it)
            authService.deleteRefreshToken(email)

            val accessCookie = Cookie("accessToken", null).apply { maxAge = 0; path = "/"; isHttpOnly = true }
            val refreshCookie = Cookie("refreshToken", null).apply { maxAge = 0; path = "/"; isHttpOnly = true }

            response.addCookie(accessCookie)
            response.addCookie(refreshCookie)
        }
    }
}