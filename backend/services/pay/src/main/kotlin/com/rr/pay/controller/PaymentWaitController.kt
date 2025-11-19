package com.rr.pay.controller

import com.rr.pay.dto.PaymentResultResponse
import com.rr.pay.service.PaymentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/pay")
class PaymentWaitController(
    private val paymentService: PaymentService
) {

    @GetMapping("/wait-result/{reservationId}")
    fun waitResult(
        @PathVariable reservationId: UUID,
        @RequestParam(defaultValue = "10") timeoutSeconds: Long
    ): Mono<PaymentResultResponse> {
        return paymentService.waitPaymentResult(reservationId, timeoutSeconds)
    }
}