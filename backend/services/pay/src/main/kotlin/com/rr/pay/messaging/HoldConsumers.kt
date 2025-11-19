package com.rr.pay.messaging

import com.rr.common.events.HoldApprovedEvent
import com.rr.common.events.HoldDeniedEvent
import com.rr.pay.domain.ReservationStatus
import com.rr.pay.repository.ReservationRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class HoldConsumers(
    private val reservationRepository: ReservationRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${app.topics.holdApproved}"],
        groupId = "\${app.kafka.groups.pay-hold}"
    )
    @Transactional
    fun onHoldApproved(event: HoldApprovedEvent) {
        log.info("[HoldConsumers] Consumed HoldApprovedEvent. reservationId={}, roomId={}",
            event.reservationId, event.roomId)

        val reservation = reservationRepository.findById(event.reservationId)
            .orElseThrow { IllegalStateException("Reservation ${event.reservationId} not found") }

        if (reservation.status != ReservationStatus.HOLD_PENDING &&
            reservation.status != ReservationStatus.HOLD) {
            log.warn(
                "[HoldConsumers] Reservation {} status is {}, but HoldApprovedEvent arrived.",
                reservation.id, reservation.status
            )
        }

        if (reservation.status == ReservationStatus.CANCELLED) {
            reservation.cancel()
            log.info("[HoldConsumers] Reservation {} status -> CANCELLED", reservation.id)
        } else {
            // 여기서는 그냥 CONFIRM 으로 만든다
            reservation.confirm()
            log.info("[HoldConsumers] Reservation {} status -> CONFIRM", reservation.id)
        }
    }

    @KafkaListener(
        topics = ["\${app.topics.holdDenied}"],
        groupId = "\${app.kafka.groups.pay-hold}"
    )
    @Transactional
    fun onHoldDenied(event: HoldDeniedEvent) {
        log.info("[HoldConsumers] Consumed HoldDeniedEvent. reservationId={}", event.reservationId)

        val reservation = reservationRepository.findById(event.reservationId)
            .orElseThrow { IllegalStateException("Reservation ${event.reservationId} not found") }

        reservation.deny()
        log.info("[HoldConsumers] Reservation {} status -> DENIED (reason={})",
            reservation.id, event.reason)
    }
}