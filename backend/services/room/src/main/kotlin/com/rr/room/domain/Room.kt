package com.rr.room.domain

import jakarta.persistence.*

@Entity
@Table(name = "rooms")
class Room(
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var capacity: Int,
    @Column(nullable = false)
    var status: String = "AVAILABLE"
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
