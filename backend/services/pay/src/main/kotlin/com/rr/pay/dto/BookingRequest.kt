package com.rr.pay.dto

data class BookingRequest(
    val roomId: Long,
    val userId: String,
    // "2025-11-20" 형식 (ISO-8601 날짜)
    val checkIn: String,
    val checkOut: String,
    // 총 결제 금액 (원) – 부하테스트용이라 프론트/스크립트에서 계산해서 보내는 걸로
    val amount: Long
)