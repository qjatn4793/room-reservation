package com.rr.room.dto

data class SuggestResponse(
    val query: String,
    val items: List<Suggestion>
)