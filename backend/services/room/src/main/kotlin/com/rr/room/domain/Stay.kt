package com.rr.room.domain

import jakarta.persistence.*

@Entity
@Table(name = "stays")
open class Stay(

    @Column(nullable = false)
    open var name: String,

    @Column(nullable = false)
    open var location: String,

    // 평점: 처음엔 0.0 으로 시작
    @Column(nullable = false)
    open var rating: Double = 0.0,

    // 리뷰 수: 처음엔 0으로 시작
    @Column(nullable = false)
    open var reviewCount: Int = 0,

    // 상세 설명: 선택값
    @Column(columnDefinition = "TEXT")
    open var description: String? = null,

    @Column(name = "thumbnail_url", length = 1000)
    open var thumbnailUrl: String? = null,

    // 편의시설: 나중에 필요하면 채우기
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "stay_amenities",
        joinColumns = [JoinColumn(name = "stay_id")]
    )
    @Column(name = "amenity")
    open var amenities: MutableList<String> = mutableListOf(),

    // 이미지 URL 리스트
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "stay_images",
        joinColumns = [JoinColumn(name = "stay_id")]
    )
    @Column(name = "image_url")
    open var images: MutableList<String> = mutableListOf()
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
}
