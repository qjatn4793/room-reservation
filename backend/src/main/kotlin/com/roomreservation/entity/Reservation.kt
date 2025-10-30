package com.roomreservation.reservation.domain

import jakarta.persistence.*
import java.time.Instant
import java.util.*

enum class ReservationStatus {
    HOLD, CONFIRMED, CANCELLED, EXPIRED, DENIED
}

@Entity
@Table(
    name = "reservations",
    indexes = [
        Index(name = "idx_res_room_time", columnList = "roomId,startAt,endAt"),
        Index(name = "idx_res_status", columnList = "status")
    ]
)
class Reservation(
    @Id
    val id: UUID = UUID.randomUUID(),

    val roomId: Long,

    val userId: String,

    val startAt: Instant,

    val endAt: Instant,

    @Enumerated(EnumType.STRING)
    var status: ReservationStatus = ReservationStatus.HOLD,

    var holdExpiresAt: Instant? = null,

    val createdAt: Instant = Instant.now(),

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
