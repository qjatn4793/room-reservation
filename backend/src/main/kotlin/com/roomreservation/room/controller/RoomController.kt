package com.roomreservation.room.controller

import com.roomreservation.room.entity.Room
import com.roomreservation.room.repository.RoomRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/public/rooms")
class RoomController(
    private val roomRepository: RoomRepository
) {
    @GetMapping
    fun list(): List<Room> = roomRepository.findAll()
}