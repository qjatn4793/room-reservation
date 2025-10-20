package com.roomreservation.room.repository

import com.roomreservation.room.entity.Room
import org.springframework.data.jpa.repository.JpaRepository

interface RoomRepository : JpaRepository<Room, Long>