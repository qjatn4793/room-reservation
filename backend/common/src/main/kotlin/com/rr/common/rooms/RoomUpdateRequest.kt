package com.rr.common.rooms

data class RoomUpdateRequest(
    val name: String? = null,
    val maxPeople: Int? = null,
    val price: Long? = null
)