import { api } from './client'
import { SuggestResponse } from '@/types/search'

export async function fetchSuggestions(q: string) {
  if (!q?.trim()) return { query: q, items: [] } as SuggestResponse
  // 게이트웨이에서 통합 서제스트 제공한다고 가정
  const res = await api.get<SuggestResponse>('/gateway/search/suggest', { params: { q } })
  return res.data
}
