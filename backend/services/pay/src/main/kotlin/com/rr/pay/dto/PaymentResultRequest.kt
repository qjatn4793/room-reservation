package com.rr.pay.dto


import java.util.UUID

data class PaymentResultRequest(
    val status: String,          // "SUCCESS" / "FAIL"
    val reservationId: UUID,
    val authId: String? = null,  // 성공 시 PG에서 주는 승인번호 같은 것
    val reason: String? = null   // 실패 시 사유(Optional)
)