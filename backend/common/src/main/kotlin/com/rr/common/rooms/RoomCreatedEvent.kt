package com.rr.common.rooms

data class RoomCreatedEvent(
    val roomId: Long,
    val stayId: Long,
    val name: String,
    val maxPeople: Int,
    val price: Long
)