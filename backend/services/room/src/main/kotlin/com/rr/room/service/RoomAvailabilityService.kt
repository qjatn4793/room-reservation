package com.rr.room.service

import com.rr.room.domain.RoomSchedule
import com.rr.room.domain.ScheduleStatus
import com.rr.room.repository.RoomScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class RoomAvailabilityService(
    private val roomScheduleRepository: RoomScheduleRepository
) {

    fun isAvailable(roomId: Long, checkIn: LocalDate, checkOut: LocalDate): Boolean {
        val activeStatuses = listOf(ScheduleStatus.HOLD, ScheduleStatus.CONFIRMED)
        val conflicts = roomScheduleRepository.countOverlapping(roomId, checkIn, checkOut, activeStatuses)
        return conflicts == 0L
    }

    @Transactional
    fun hold(roomId: Long, checkIn: LocalDate, checkOut: LocalDate, reservationId: UUID) {
        val holdExpiresAt = Instant.now().plus(10, ChronoUnit.MINUTES)

        val schedule = RoomSchedule(
            roomId = roomId,
            reservationId = reservationId,
            checkIn = checkIn,
            checkOut = checkOut,
            status = ScheduleStatus.HOLD,
            holdExpiresAt = holdExpiresAt
        )

        roomScheduleRepository.save(schedule)
    }

    /**
     * 결제 성공 → HOLD → CONFIRMED
     */
    @Transactional
    fun confirmByReservation(reservationId: UUID) {
        val holds = roomScheduleRepository.findByReservationIdAndStatus(
            reservationId,
            ScheduleStatus.HOLD
        )

        // 없으면 그냥 아무 일도 안 하고 리턴 (중복 처리/타임아웃 등)
        if (holds.isEmpty()) return

        holds.forEach {
            it.status = ScheduleStatus.CONFIRMED
            it.holdExpiresAt = null
        }
    }

    /**
     * 결제 실패/취소/타임아웃 → HOLD 해제
     */
    @Transactional
    fun releaseHold(reservationId: UUID) {
        val holds = roomScheduleRepository.findByReservationIdAndStatus(
            reservationId,
            ScheduleStatus.HOLD
        )

        if (holds.isEmpty()) return

        // 1) 그냥 삭제해버리는 버전
        holds.forEach { roomScheduleRepository.delete(it) }

        // 2) 또는 상태를 EXPIRED 로 바꾸고 싶으면:
        // holds.forEach {
        //     it.status = ScheduleStatus.EXPIRED
        //     it.holdExpiresAt = null
        // }
    }

    /**
     * 홀드 만료 처리용 – 스케줄러에서 주기적으로 호출
     */
    @Transactional
    fun expireHolds() {
        val now = Instant.now()
        val expired = roomScheduleRepository.findByStatusAndHoldExpiresAtBefore(
            ScheduleStatus.HOLD,
            now
        )

        expired.forEach {
            it.status = ScheduleStatus.EXPIRED
        }
    }
}