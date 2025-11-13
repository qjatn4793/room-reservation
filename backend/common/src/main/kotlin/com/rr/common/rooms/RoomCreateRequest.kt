package com.rr.common.rooms

data class RoomCreateRequest(
    val stayId: Long,
    val name: String,
    val maxPeople: Int,
    val price: Long
)