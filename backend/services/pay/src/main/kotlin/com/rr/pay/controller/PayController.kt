package com.rr.pay.controller

import com.rr.pay.dto.PayRedirectResponse
import com.rr.pay.repository.ReservationRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("/pay")
class PayController(
    private val reservationRepository: ReservationRepository,
) {
    @PostMapping("/{reservationId}")
    fun requestPay(@PathVariable reservationId: UUID): PayRedirectResponse {

        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Reservation $reservationId not found"
                )
            }

        val redirectUrl = "/payment/result?status=SUCCESS&reservationId=$reservationId"

        return PayRedirectResponse(redirectUrl = redirectUrl)
    }
}