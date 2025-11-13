package com.rr.pay.dto

import com.rr.pay.domain.ReservationStatus
import java.time.LocalDate
import java.util.UUID

data class BookingResponse(
    val reservationId: UUID,
    val status: ReservationStatus,
    val roomId: Long,
    val userId: String,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val amount: Long
)