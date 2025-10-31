package com.rr.room.service

import com.rr.common.rooms.RoomCreatedEvent
import com.rr.common.rooms.RoomResponse
import com.rr.common.rooms.RoomStatusUpdatedEvent
import com.rr.room.domain.Room
import com.rr.room.domain.RoomRepository
import com.rr.room.messaging.RoomProducer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoomService(
    private val repo: RoomRepository,
    private val producer: RoomProducer
) {
    @Transactional
    fun create(name: String, capacity: Int): RoomResponse {
        val saved = repo.save(Room(name = name, capacity = capacity))
        producer.publishCreated(
            RoomCreatedEvent(
                roomId = saved.id!!,
                name = saved.name,
                capacity = saved.capacity
            )
        )
        return RoomResponse(saved.id!!, saved.name, saved.capacity, saved.status)
    }

    @Transactional
    fun updateStatus(id: Long, status: String): RoomResponse {
        val room = repo.findById(id).orElseThrow()
        room.status = status
        val saved = repo.save(room)
        producer.publishStatus(RoomStatusUpdatedEvent(roomId = saved.id!!, status = saved.status))
        return RoomResponse(saved.id!!, saved.name, saved.capacity, saved.status)
    }

    @Transactional(readOnly = true)
    fun get(id: Long): RoomResponse {
        val r = repo.findById(id).orElseThrow()
        return RoomResponse(r.id!!, r.name, r.capacity, r.status)
    }
}
