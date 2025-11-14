package com.rr.room.controller

import com.rr.room.domain.Stay
import com.rr.common.stays.StayCreateRequest
import com.rr.common.stays.StayResponse
import com.rr.room.dto.StayDetailResponse
import com.rr.room.repository.StayRepository
import com.rr.room.service.RoomService
import com.rr.room.service.StayService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stays")
class StayController(
    private val stayRepo: StayRepository,
    private val stayService: StayService
) {
    @PostMapping
    fun create(@RequestBody req: StayCreateRequest): StayResponse {
        val saved = stayRepo.save(Stay(name = req.name, location = req.location))
        return StayResponse(saved.id!!, saved.name, saved.location)
    }

    @GetMapping("/{stayId}")
    fun getDetail(@PathVariable stayId: Long): StayDetailResponse {
        return stayService.getDetail(stayId)
    }
}