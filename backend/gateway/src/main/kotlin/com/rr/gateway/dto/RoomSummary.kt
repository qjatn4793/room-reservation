package com.rr.gateway.dto

data class RoomSummary(
    val id: String,
    val name: String,
    val location: String,
    val price: Long,
    val rating: Double,
    val reviewCount: Long,
    val thumbnailUrl: String
)