package com.rr.room.dto

data class RoomSummary(
    val id: Long,
    val stayId: Long,
    val name: String,        // "숙소명 - 객실명" 등
    val location: String,
    val price: Long,
    val rating: Double,
    val reviewCount: Int,
    val thumbnailUrl: String
)