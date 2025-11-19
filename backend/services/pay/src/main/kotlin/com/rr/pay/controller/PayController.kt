package com.rr.pay.controller

import com.rr.pay.dto.PayRedirectResponse
import com.rr.pay.dto.PaymentResultRequest
import com.rr.pay.dto.PaymentResultResponse
import com.rr.pay.service.PaymentService
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/pay")
class PayController(
    private val paymentService: PaymentService
) {
    /**
     * 예: POST /api/pay/ead97bb0-f6c4-448e-a5a9-fe74489d4115/start
     */
    @PostMapping("/{reservationId}/start")
    fun startPayment(@PathVariable reservationId: UUID) {
        paymentService.startPayment(reservationId)
    }

    @GetMapping("/result")
    fun paymentResult(
        @RequestParam status: String,
        @RequestParam reservationId: UUID,
        @RequestParam(required = false) authId: String?
    ): PaymentResultResponse {
        val request = PaymentResultRequest(
            status = status,
            reservationId = reservationId,
            authId = authId,
            reason = null
        )
        return paymentService.processPaymentResult(request)
    }
}