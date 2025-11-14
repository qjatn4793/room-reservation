package com.rr.room.dto

data class StayDetailResponse(
    val id: Long,
    val name: String,
    val location: String,
    val rating: Double,
    val reviewCount: Int,
    val description: String?,
    val amenities: List<String>,
    val images: List<String>,
    val thumbnailUrl: String?,          // 썸네일까지 있으면
    val rooms: List<StayRoomItem>
)