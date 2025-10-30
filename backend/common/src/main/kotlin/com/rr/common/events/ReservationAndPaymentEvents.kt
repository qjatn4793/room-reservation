package com.rr.common.events

import java.time.Instant
import java.util.*

data class HoldRequestedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val reservationId: UUID,
    val roomId: Long,
    val userId: String,
    val startAt: Instant,
    val endAt: Instant,
    val requestedAt: Instant = Instant.now()
)
data class HoldApprovedEvent(val eventId: UUID = UUID.randomUUID(), val reservationId: UUID, val roomId: Long, val holdExpiresAt: Instant, val approvedAt: Instant = Instant.now())
data class HoldDeniedEvent(val eventId: UUID = UUID.randomUUID(), val reservationId: UUID, val roomId: Long, val reason: String, val deniedAt: Instant = Instant.now())
data class ReservationConfirmedEvent(val eventId: UUID = UUID.randomUUID(), val reservationId: UUID, val roomId: Long, val confirmedAt: Instant = Instant.now())
data class ReservationCancelledEvent(val eventId: UUID = UUID.randomUUID(), val reservationId: UUID, val roomId: Long, val cancelledAt: Instant = Instant.now())

data class PaymentRequestedEvent(val eventId: UUID = UUID.randomUUID(), val reservationId: UUID, val roomId: Long, val amount: Long, val currency: String = "KRW", val requestedAt: Instant = Instant.now())
data class PaymentAuthorizedEvent(val eventId: UUID = UUID.randomUUID(), val reservationId: UUID, val roomId: Long, val authId: String, val authorizedAt: Instant = Instant.now())
data class PaymentFailedEvent(val eventId: UUID = UUID.randomUUID(), val reservationId: UUID, val roomId: Long, val reason: String, val failedAt: Instant = Instant.now())
