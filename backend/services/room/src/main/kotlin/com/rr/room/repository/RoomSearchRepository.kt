package com.rr.room.repository

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.rr.room.dto.RoomSummary
import com.rr.room.domain.QRoom.room
import com.rr.room.domain.QRoomInventory.roomInventory
import com.rr.room.domain.QStay.stay
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class RoomSearchRepository(
    private val queryFactory: JPAQueryFactory
) {
    /**
     * q: 키워드(지역/숙소명/객실명)
     * checkIn ~ checkOut: [시작, 종료) (종료일 미포함)
     * people: 최소 수용 인원
     * page/size: 페이지네이션
     */
    fun search(
        q: String?,
        checkIn: LocalDate,
        checkOut: LocalDate,
        people: Int,
        page: Int,
        size: Int
    ): List<RoomSummary> {

        // 1) 가용성: 해당 기간 안에 "미가용(available=false)"이 하나도 없어야 함
        val notAvailableSubquery = JPAExpressions
            .selectOne()
            .from(roomInventory)
            .where(
                roomInventory.room.eq(room),
                roomInventory.date.goe(checkIn),
                roomInventory.date.lt(checkOut),
                roomInventory.available.isFalse
            )

        // 2) 키워드 필터
        val keyword = q?.takeIf { it.isNotBlank() }
        val keywordPredicate = keyword?.let {
            stay.location.containsIgnoreCase(it)
                .or(stay.name.containsIgnoreCase(it))
                .or(room.name.containsIgnoreCase(it))
        }

        // 3) 이름 합치기: "숙소명 - 객실명"
        val displayName = Expressions.stringTemplate(
            "concat({0}, ' - ', {1})", stay.name, room.name
        )

        return queryFactory
            .select(
                Projections.constructor(
                    RoomSummary::class.java,
                    room.id,           // Long
                    displayName,       // String
                    stay.location,     // String
                    room.price,        // Long
                    stay.rating,       // Double
                    stay.reviewCount,  // Long
                    stay.thumbnailUrl  // String
                )
            )
            .from(room)
            .join(room.stay, stay)
            .where(
                room.maxPeople.goe(people),
                keywordPredicate,
                notAvailableSubquery.notExists()
            )
            .orderBy(stay.rating.desc(), room.price.asc())
            .offset(page.toLong() * size)
            .limit(size.toLong())
            .fetch()
    }
}
