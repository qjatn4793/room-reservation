package com.rr.pay.controller

import com.rr.pay.dto.BookingRequest
import com.rr.pay.dto.BookingResponse
import com.rr.pay.service.BookingService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/booking")
class BookingController(
    private val bookingService: BookingService
) {

    @PostMapping
    fun create(@RequestBody request: BookingRequest): BookingResponse {
        return bookingService.create(request)
    }
}
