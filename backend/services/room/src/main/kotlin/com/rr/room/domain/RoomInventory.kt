package com.rr.room.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "room_inventory")
class RoomInventory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "room_id")
    var room: Room,

    @Column(nullable = false) var date: LocalDate,
    @Column(nullable = false) var available: Boolean = true
)