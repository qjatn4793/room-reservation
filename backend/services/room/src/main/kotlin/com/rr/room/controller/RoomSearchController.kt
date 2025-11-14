package com.rr.room.controller

import com.rr.room.dto.RoomSummary
import com.rr.room.service.RoomSearchService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rooms")
class RoomSearchController(
    private val service: RoomSearchService
) {
    @GetMapping("/search")
    fun search(
        @RequestParam(required = false) q: String?,
        @RequestParam checkIn: String,
        @RequestParam checkOut: String,
        @RequestParam(defaultValue = "2") people: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "12") size: Int
    ): List<RoomSummary> =
        service.search(q, checkIn, checkOut, people, page, size)
}