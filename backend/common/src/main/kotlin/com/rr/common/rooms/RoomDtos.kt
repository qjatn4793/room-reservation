package com.rr.common.rooms

import java.time.Instant
import java.util.*

data class RoomCreateRequest(
    val name: String,
    val capacity: Int
)

data class RoomResponse(
    val id: Long,
    val name: String,
    val capacity: Int,
    val status: String
)

data class RoomCreatedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val roomId: Long,
    val name: String,
    val capacity: Int,
    val occurredAt: Instant = Instant.now()
)

data class RoomStatusUpdatedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val roomId: Long,
    val status: String,
    val occurredAt: Instant = Instant.now()
)
