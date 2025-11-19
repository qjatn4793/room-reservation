package com.rr.pay.service

import com.rr.pay.domain.Reservation
import com.rr.pay.domain.ReservationStatus
import com.rr.pay.dto.BookingRequest
import com.rr.pay.dto.BookingResponse
import com.rr.pay.messaging.BookingProducer
import com.rr.pay.repository.ReservationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class BookingService(
    private val reservationRepository: ReservationRepository,
    private val bookingProducer: BookingProducer
) {

    @Transactional
    fun create(request: BookingRequest): BookingResponse {

        val checkIn = LocalDate.parse(request.checkIn)
        val checkOut = LocalDate.parse(request.checkOut)

        val reservation = Reservation(
            roomId = request.roomId,
            userId = request.userId,
            checkIn = checkIn,
            checkOut = checkOut,
            amount = request.amount,
            status = ReservationStatus.HOLD   // 재고 홀드 대기
        )

        val saved = reservationRepository.save(reservation)

        // 재고 체크/홀드를 room-service에게 위임
        bookingProducer.publishBookingRequested(saved)

        return BookingResponse(
            reservationId = saved.id,
            status = saved.status,
            roomId = saved.roomId,
            userId = saved.userId,
            checkIn = saved.checkIn,
            checkOut = saved.checkOut,
            amount = saved.amount
        )
    }
}