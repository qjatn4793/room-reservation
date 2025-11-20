package com.rr.pay.controller

import com.rr.pay.dto.PaymentResultResponse
import com.rr.pay.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
    ): ResponseEntity<PaymentResultResponse> {

        val result = paymentService.waitPaymentResult(reservationId, timeoutSeconds)
        return ResponseEntity.ok(result)
    }
}