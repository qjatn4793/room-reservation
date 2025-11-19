package com.rr.pay.domain

enum class ReservationStatus {
    HOLD_PENDING,   // room-service에서 재고 체크/홀드 결과 대기 중
    HOLD,           // 재고 홀드 완료 (결제 가능)
    CONFIRMED,      // 결제까지 완료
    CANCELLED,      // 사용자가 취소
    EXPIRED,        // 홀드/결제 타임아웃 만료
    DENIED          // 재고 부족 등으로 예약 불가
}