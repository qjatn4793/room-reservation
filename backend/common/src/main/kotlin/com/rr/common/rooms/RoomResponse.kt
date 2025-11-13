package com.rr.common.rooms

data class RoomResponse(
    val id: Long,
    val stayId: Long,
    val name: String,
    val maxPeople: Int,
    val price: Long
)