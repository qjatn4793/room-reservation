package com.rr.room.controller

import com.rr.common.rooms.RoomCreateRequest
import com.rr.common.rooms.RoomResponse
import com.rr.common.rooms.RoomUpdateRequest
import com.rr.room.service.RoomService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rooms")
class RoomController(
    private val roomService: RoomService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody req: RoomCreateRequest): RoomResponse =
        roomService.create(req.stayId, req.name, req.maxPeople, req.price)

    @PatchMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: RoomUpdateRequest): RoomResponse =
        roomService.update(id, req.name, req.maxPeople, req.price)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): RoomResponse =
        roomService.get(id)
}