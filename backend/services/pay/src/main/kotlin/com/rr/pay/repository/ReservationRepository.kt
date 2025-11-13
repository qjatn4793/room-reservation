package com.rr.pay.repository

import com.rr.pay.domain.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReservationRepository : JpaRepository<Reservation, UUID>
