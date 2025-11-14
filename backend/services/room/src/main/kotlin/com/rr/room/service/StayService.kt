package com.rr.room.service

import com.rr.room.dto.StayDetailResponse
import com.rr.room.dto.StayRoomItem
import com.rr.room.repository.RoomRepository
import com.rr.room.repository.StayRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StayService(
    private val stayRepo: StayRepository,
    private val roomRepo: RoomRepository
) {

    @Transactional(readOnly = true)
    fun getDetail(stayId: Long): StayDetailResponse {
        val stay = stayRepo.findById(stayId)
            .orElseThrow { NoSuchElementException("Stay $stayId not found") }

        val rooms = roomRepo.findByStayId(stayId)

        return StayDetailResponse(
            id = stay.id!!,
            name = stay.name,
            location = stay.location,
            rating = stay.rating,
            reviewCount = stay.reviewCount,
            description = stay.description,
            amenities = stay.amenities, // 저장 방식에 맞게 처리
            images = stay.images,
            thumbnailUrl = stay.thumbnailUrl,
            rooms = rooms.map { r ->
                StayRoomItem(
                    roomId = r.id!!,
                    name = r.name,
                    maxPeople = r.maxPeople,
                    price = r.price
                )
            }
        )
    }
}