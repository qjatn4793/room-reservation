package com.rr.room.service

import com.rr.room.dto.RoomSummary
import com.rr.room.repository.RoomSearchRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RoomSearchService(
    private val repo: RoomSearchRepository
) {
    fun search(
        q: String?,
        checkIn: String,
        checkOut: String,
        people: Int,
        page: Int,
        size: Int
    ): List<RoomSummary> {
        val ci = LocalDate.parse(checkIn)
        val co = LocalDate.parse(checkOut)
        require(!ci.isAfter(co)) { "checkIn must be before checkOut" }
        return repo.search(q?.takeIf { it.isNotBlank() }, ci, co, people, page, size)
    }
}
