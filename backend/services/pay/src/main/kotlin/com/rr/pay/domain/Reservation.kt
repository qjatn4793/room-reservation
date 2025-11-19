package com.rr.pay.domain

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "reservations")
class Reservation(

    @Id
    @Column(columnDefinition = "uuid")
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val roomId: Long,

    @Column(nullable = false, length = 100)
    val userId: String,

    @Column(nullable = false)
    val checkIn: LocalDate,

    @Column(nullable = false)
    val checkOut: LocalDate,

    @Column(nullable = false)
    val amount: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: ReservationStatus = ReservationStatus.HOLD_PENDING,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
) {

    private fun touch() {
        updatedAt = Instant.now()
    }

    fun setHoldPending() {
        status = ReservationStatus.HOLD_PENDING
        touch()
    }

    fun hold() {
        status = ReservationStatus.HOLD
        touch()
    }

    fun confirm() {
        status = ReservationStatus.CONFIRMED
        touch()
    }

    fun cancel() {
        status = ReservationStatus.CANCELLED
        touch()
    }

    fun expire() {
        status = ReservationStatus.EXPIRED
        touch()
    }

    fun deny() {
        status = ReservationStatus.DENIED
        touch()
    }
}