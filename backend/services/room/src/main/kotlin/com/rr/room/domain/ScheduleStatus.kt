package com.rr.room.domain

enum class ScheduleStatus {
    HOLD,       // 홀드 (결제 대기)
    CONFIRMED,  // 확정된 예약
    EXPIRED     // 결제 취소
}