package com.rr.room.controller

import com.rr.room.dto.SuggestResponse
import com.rr.room.dto.Suggestion
import com.rr.room.dto.SuggestionType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SuggestController {
    @GetMapping("/suggest")
    fun suggest(@RequestParam q: String): SuggestResponse {
        if (q.isBlank()) return SuggestResponse(q, emptyList())
        // 예시: 매칭된 상위 N개 반환 (실전은 Redis/ES/Meili)
        val samples = listOf(
            Suggestion("r-seoul", "서울", SuggestionType.region),
            Suggestion("r-busan", "부산", SuggestionType.region),
            Suggestion("s-line2", "2호선 강남역", SuggestionType.subway),
            Suggestion("l-namsan", "남산타워", SuggestionType.landmark)
        )
        val filtered = samples.filter { it.name.contains(q, ignoreCase = true) }.take(10)
        return SuggestResponse(q, filtered)
    }
}