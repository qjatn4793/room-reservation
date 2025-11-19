package com.rr.pay.dto

import com.rr.pay.domain.ReservationStatus
import java.util.UUID

data class PaymentResultResponse(
    val reservationId: UUID,
    val status: ReservationStatus,
    val message: String,
    val canRetry: Boolean,
    val timedOut: Boolean,     // 10초 안에 확정 안 되면 true
)