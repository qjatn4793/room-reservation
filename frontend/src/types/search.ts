export type SuggestionType = 'region' | 'subway' | 'landmark'

export type Suggestion = {
  id: string
  name: string
  type: SuggestionType
}

export type SuggestResponse = {
  query: string
  items: Suggestion[]
}
