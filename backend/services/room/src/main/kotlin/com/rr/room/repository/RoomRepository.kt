package com.rr.room.repository

import com.rr.room.domain.Room
import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository : JpaRepository<Room, Long> {

    // 객실 이름으로 서제스트 (상위 5개)
    fun findTop5ByNameContainingIgnoreCase(name: String): List<Room>

    // stay id 로 조회
    fun findByStayId(name: Long) : List<Room>
}