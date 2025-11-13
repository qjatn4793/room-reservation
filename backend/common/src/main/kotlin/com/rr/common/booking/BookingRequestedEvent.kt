package com.rr.common.booking

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class BookingRequestedEvent(
    val reservationId: UUID,
    val roomId: Long,
    val userId: String,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val amount: Long,
    val createdAt: Instant = Instant.now()
)