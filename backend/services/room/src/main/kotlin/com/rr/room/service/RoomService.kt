package com.rr.room.service

import com.rr.common.rooms.RoomCreatedEvent
import com.rr.common.rooms.RoomResponse
import com.rr.room.domain.Room
import com.rr.room.repository.RoomRepository
import com.rr.room.repository.StayRepository
import com.rr.room.messaging.RoomProducer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoomService(
    private val roomRepo: RoomRepository,
    private val stayRepo: StayRepository,
    private val producer: RoomProducer
) {
    /**
     * 방 생성: (기존 name/capacity → name/maxPeople/price + stayId 필요)
     */
    @Transactional
    fun create(stayId: Long, name: String, maxPeople: Int, price: Long): RoomResponse {
        val stay = stayRepo.findById(stayId).orElseThrow { NoSuchElementException("Stay $stayId not found") }
        val saved = roomRepo.save(
            Room(
                stay = stay,
                name = name,
                maxPeople = maxPeople,
                price = price
            )
        )
        // 이벤트 필드도 maxPeople/price 로 발행
        producer.publishCreated(
            RoomCreatedEvent(
                roomId = saved.id!!,
                stayId = stay.id!!,
                name = saved.name,
                maxPeople = saved.maxPeople,
                price = saved.price
            )
        )
        return RoomResponse(
            id = saved.id,
            stayId = stay.id!!,
            name = saved.name,
            maxPeople = saved.maxPeople,
            price = saved.price
        )
    }

    /**
     * 방 정보 부분 수정 (이전의 status 업데이트 제거)
     */
    @Transactional
    fun update(
        id: Long,
        name: String? = null,
        maxPeople: Int? = null,
        price: Long? = null
    ): RoomResponse {
        val room = roomRepo.findById(id).orElseThrow { NoSuchElementException("Room $id not found") }
        name?.let { room.name = it }
        maxPeople?.let { room.maxPeople = it }
        price?.let { room.price = it }
        val saved = roomRepo.save(room)

        // 필요하다면 변경 이벤트 발행 (예: 가격/정원 변경)
        // producer.publishUpdated(RoomUpdatedEvent(...))

        return RoomResponse(
            id = saved.id!!,
            stayId = saved.stay.id!!,
            name = saved.name,
            maxPeople = saved.maxPeople,
            price = saved.price
        )
    }

    @Transactional(readOnly = true)
    fun get(id: Long): RoomResponse {
        val r = roomRepo.findById(id).orElseThrow { NoSuchElementException("Room $id not found") }
        return RoomResponse(
            id = r.id!!,
            stayId = r.stay.id!!,
            name = r.name,
            maxPeople = r.maxPeople,
            price = r.price
        )
    }
}
