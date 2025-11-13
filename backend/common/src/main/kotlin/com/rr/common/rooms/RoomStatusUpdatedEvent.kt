package com.rr.common.rooms

import java.time.Instant
import java.util.UUID

data class RoomStatusUpdatedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val roomId: Long,
    val status: String,
    val occurredAt: Instant = Instant.now()
)