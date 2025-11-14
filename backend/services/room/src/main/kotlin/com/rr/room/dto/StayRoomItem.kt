package com.rr.room.dto

data class StayRoomItem(
    val roomId: Long,
    val name: String,
    val maxPeople: Int,
    val price: Long
)