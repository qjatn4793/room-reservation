package com.rr.gateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@Component
class RequestIdFilter : GlobalFilter, Ordered {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val req = exchange.request
        val headers = req.headers
        val requestId = headers.getFirst("X-Request-Id") ?: UUID.randomUUID().toString()
        val mutated = exchange.mutate().request(
            req.mutate().header("X-Request-Id", requestId).build()
        ).build()
        return chain.filter(mutated)
    }
    override fun getOrder(): Int = -1000
}
