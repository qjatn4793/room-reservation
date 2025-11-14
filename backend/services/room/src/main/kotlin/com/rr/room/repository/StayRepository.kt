package com.rr.room.repository

import com.rr.room.domain.Stay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StayRepository : JpaRepository<Stay, Long> {
    // 숙소 이름으로 서제스트 (상위 5개)
    fun findTop5ByNameContainingIgnoreCase(name: String): List<Stay>

    // 지역(location) 서제스트: distinct location
    @Query(
        """
        select distinct s.location
        from Stay s
        where lower(s.location) like lower(concat('%', :keyword, '%'))
        """
    )
    fun findLocationsByKeyword(@Param("keyword") keyword: String): List<String>
}
