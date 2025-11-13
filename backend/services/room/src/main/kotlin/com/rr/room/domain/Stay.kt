package com.rr.room.domain

import jakarta.persistence.*

@Entity
@Table(name = "stays")
class Stay(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false) var name: String,
    @Column(nullable = false) var location: String,
    @Column(nullable = false) var rating: Double = 4.5,
    @Column(nullable = false) var reviewCount: Long = 0,
    @Column(nullable = false) var thumbnailUrl: String = ""
)