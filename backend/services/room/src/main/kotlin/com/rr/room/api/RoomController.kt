package com.rr.room.api

import com.rr.common.rooms.RoomCreateRequest
import com.rr.common.rooms.RoomResponse
import com.rr.room.service.RoomService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rooms")
class RoomController(
    private val roomService: RoomService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody req: RoomCreateRequest): RoomResponse =
        roomService.create(req.name, req.capacity)

    @PatchMapping("/{id}/status/{status}")
    fun updateStatus(@PathVariable id: Long, @PathVariable status: String): RoomResponse =
        roomService.updateStatus(id, status)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): RoomResponse =
        roomService.get(id)
}
