package com.rr.room.domain

import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository : JpaRepository<Room, Long>
