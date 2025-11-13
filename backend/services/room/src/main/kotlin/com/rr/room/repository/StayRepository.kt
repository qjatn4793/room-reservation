package com.rr.room.repository

import com.rr.room.domain.Stay
import org.springframework.data.jpa.repository.JpaRepository

interface StayRepository : JpaRepository<Stay, Long>
