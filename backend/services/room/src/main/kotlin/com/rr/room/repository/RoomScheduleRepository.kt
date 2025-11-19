package com.rr.room.repository

import com.rr.room.domain.RoomSchedule
import com.rr.room.domain.ScheduleStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Repository
interface RoomScheduleRepository : JpaRepository<RoomSchedule, Long> {

    @Query(
        """
        select count(rs)
        from RoomSchedule rs
        where rs.roomId = :roomId
          and rs.status in :statuses
          and rs.checkIn < :checkOut
          and rs.checkOut > :checkIn
        """
    )
    fun countOverlapping(
        roomId: Long,
        checkIn: LocalDate,
        checkOut: LocalDate,
        statuses: List<ScheduleStatus>
    ): Long

    fun findByReservationIdAndStatus(
        reservationId: UUID,
        status: ScheduleStatus
    ): List<RoomSchedule>

    fun findByStatusAndHoldExpiresAtBefore(
        status: ScheduleStatus,
        holdExpiresAtBefore: Instant
    ): List<RoomSchedule>
}