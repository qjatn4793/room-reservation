package com.roomreservation.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/**").permitAll()
                    .anyRequest().authenticated()
            }
        return http.build()
    }
}
