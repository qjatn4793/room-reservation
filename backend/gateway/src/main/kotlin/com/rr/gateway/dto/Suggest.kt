package com.rr.gateway.dto

enum class SuggestionType { region, subway, landmark }
data class Suggestion(val id: String, val name: String, val type: SuggestionType)
data class SuggestResponse(val query: String, val items: List<Suggestion>)