package com.rr.room.controller

import com.rr.common.stays.StayCreateRequest
import com.rr.common.stays.StayResponse
import com.rr.room.domain.Stay
import com.rr.room.repository.StayRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stays")
class StayController(
    private val stayRepo: StayRepository
) {
    @PostMapping
    fun create(@RequestBody req: StayCreateRequest): StayResponse {
        val saved = stayRepo.save(Stay(name = req.name, location = req.location))
        return StayResponse(saved.id!!, saved.name, saved.location)
    }
}