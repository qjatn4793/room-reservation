// src/api/stays.ts
import { api } from './client'
import { StayDetail, StaySummary } from '@/types/stay'

/**
 * 숙소(Stay) 검색
 * GET /api/stays/search -> room-service: /stays/search
 */
export async function searchStays(params: {
  q?: string
  page?: number
  size?: number
}) {
  const res = await api.get<StaySummary[]>('/api/stays/search', {
    params: {
      q: params.q,
      page: params.page ?? 0,
      size: params.size ?? 12,
    },
  })
  return res.data
}

/**
 * 숙소(Stay) 상세
 * GET /api/stays/{stayId} -> room-service: /stays/{stayId}
 */
export async function getStayDetail(stayId: string) {
  const res = await api.get<StayDetail>(`/api/stays/${stayId}`)
  return res.data
}
