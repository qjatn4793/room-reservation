package com.roomreservation.room.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "rooms")
class Room(
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var city: String,
    @Column(nullable = true)
    var pricePerNight: Int? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}