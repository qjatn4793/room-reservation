package com.rr.room.domain

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(
    name = "room_schedule",
    indexes = [
        Index(name = "idx_room_schedule_room_dates", columnList = "room_id, check_in, check_out")
    ]
)
class RoomSchedule(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "room_id", nullable = false)
    val roomId: Long,

    @Column(name = "reservation_id", columnDefinition = "uuid", nullable = false)
    val reservationId: UUID,

    @Column(name = "check_in", nullable = false)
    val checkIn: LocalDate,

    @Column(name = "check_out", nullable = false)
    val checkOut: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: ScheduleStatus = ScheduleStatus.HOLD,

    @Column(name = "hold_expires_at")
    var holdExpiresAt: Instant? = null,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
) {

    private fun touch() {
        updatedAt = Instant.now()
    }

    fun markConfirmed() {
        status = ScheduleStatus.CONFIRMED
        holdExpiresAt = null
        touch()
    }

    fun markExpired() {
        // 필요하면 EXPIRED 같은 status 를 별도 enum 값으로 추가
        status = ScheduleStatus.HOLD     // or 삭제 전략
        touch()
    }
}