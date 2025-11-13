package com.rr.room.domain

import jakarta.persistence.*

@Entity
@Table(name = "rooms")
class Room(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "stay_id")
    var stay: Stay,

    @Column(nullable = false) var name: String,
    @Column(nullable = false) var maxPeople: Int,
    @Column(nullable = false) var price: Long
)