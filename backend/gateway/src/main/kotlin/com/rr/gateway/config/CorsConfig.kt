package com.rr.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {
    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val cors = CorsConfiguration().apply {
            allowCredentials = true
            addAllowedOriginPattern("*")
            addAllowedHeader("*")
            addAllowedMethod("*")
            addExposedHeader("X-Request-Id")
        }
        val src = UrlBasedCorsConfigurationSource()
        src.registerCorsConfiguration("/**", cors)
        return CorsWebFilter(src)
    }
}