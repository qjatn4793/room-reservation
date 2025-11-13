package com.rr.pay.domain

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate
import java.util.*

enum class ReservationStatus {
    HOLD, CONFIRMED, CANCELLED, EXPIRED, DENIED
}

@Entity
@Table(name = "reservations")
class Reservation(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val roomId: Long,

    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false)
    val checkIn: LocalDate,

    @Column(nullable = false)
    val checkOut: LocalDate,

    @Column(nullable = false)
    val amount: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReservationStatus = ReservationStatus.HOLD,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    fun confirm() {
        status = ReservationStatus.CONFIRMED
        updatedAt = Instant.now()
    }

    fun cancel() {
        status = ReservationStatus.CANCELLED
        updatedAt = Instant.now()
    }

    fun deny() {
        status = ReservationStatus.DENIED
        updatedAt = Instant.now()
    }
}
