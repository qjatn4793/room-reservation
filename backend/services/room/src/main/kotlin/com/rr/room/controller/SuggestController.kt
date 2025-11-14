package com.rr.room.controller

import com.rr.room.dto.SuggestResponse
import com.rr.room.dto.Suggestion
import com.rr.room.dto.SuggestionType
import com.rr.room.repository.RoomRepository
import com.rr.room.repository.StayRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rooms")
class SuggestController(
    private val stayRepository: StayRepository,
    private val roomRepository: RoomRepository
) {

    @GetMapping("/suggest")
    fun suggest(@RequestParam q: String): SuggestResponse {
        val trimmed = q.trim()
        if (trimmed.isEmpty()) {
            return SuggestResponse(q, emptyList())
        }

        // 1) 지역(location) 후보
        val locationSuggestions = stayRepository
            .findLocationsByKeyword(trimmed)
            .mapIndexed { idx, loc ->
                Suggestion(
                    id = "loc-$idx",
                    name = loc,
                    type = SuggestionType.region   // enum 에 맞춰서 사용
                )
            }

        // 2) 숙소 이름 후보
        val staySuggestions = stayRepository
            .findTop5ByNameContainingIgnoreCase(trimmed)
            .map { stay ->
                Suggestion(
                    id = "stay-${stay.id}",
                    name = stay.name,
                    type = SuggestionType.landmark  // 혹은 stay 타입이 따로 있으면 그걸 사용
                )
            }

        // 3) 객실 이름 후보
        val roomSuggestions = roomRepository
            .findTop5ByNameContainingIgnoreCase(trimmed)
            .map { room ->
                Suggestion(
                    id = "room-${room.id}",
                    name = room.name,
                    type = SuggestionType.landmark  // 객실도 랜드마크처럼 보여줄지, 별도 타입 쓸지 선택
                )
            }

        // 4) 합치고 상위 N개만
        val items = (locationSuggestions + staySuggestions + roomSuggestions)
            .distinctBy { it.id }   // 중복 제거
            .take(10)

        return SuggestResponse(q, items)
    }
}