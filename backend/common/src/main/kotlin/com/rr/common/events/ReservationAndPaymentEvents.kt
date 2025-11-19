package com.rr.common.events

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

// 1) 재고 홀드(선점) 관련 이벤트 -----------------------------

data class HoldRequestedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val reservationId: UUID,
    val roomId: Long,
    val userId: String,
    val startAt: Instant,
    val endAt: Instant,
    val requestedAt: Instant = Instant.now()
)

data class HoldApprovedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val reservationId: UUID,
    val roomId: Long,
    val userId: String,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val holdExpiresAt: Instant,       // 언제까지 홀드인지 (예: 10분)
    val approvedAt: Instant = Instant.now()
)

data class HoldDeniedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val reservationId: UUID,
    val roomId: Long,
    val userId: String,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val reason: String,
    val deniedAt: Instant = Instant.now()
)

// 2) 예약 상태 확정/취소 이벤트 -----------------------------

data class ReservationConfirmedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val reservationId: UUID,
    val roomId: Long,
    val confirmedAt: Instant = Instant.now()
)

data class ReservationCancelledEvent(
    val eventId: UUID = UUID.randomUUID(),
    val reservationId: UUID,
    val roomId: Long,
    val cancelledAt: Instant = Instant.now()
)

// 3) 결제 관련 이벤트 ---------------------------------------

data class PaymentRequestedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val reservationId: UUID,
    val userId: String = "test",
    val amount: Long,
    val currency: String = "KRW",
    val requestedAt: Instant = Instant.now()
)

data class PaymentAuthorizedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val reservationId: UUID,
    val userId: String = "test",
    val amount: Long,
    val currency: String = "KRW",
    val authId: String,
    val authorizedAt: Instant = Instant.now()
)

data class PaymentFailedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val reservationId: UUID,
    val userId: String = "test",
    val amount: Long,
    val currency: String = "KRW",
    val reason: String,
    val failedAt: Instant = Instant.now()
)