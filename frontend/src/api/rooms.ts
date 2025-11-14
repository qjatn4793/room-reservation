import { api } from './client'
import {
  RoomSummary,
  RoomDetail
} from '@/types/room'

/**
 * 숙소 검색
 * GET /api/rooms/search -> room-service: /rooms/search
 */
export async function searchRooms(params: {
  q?: string
  checkIn?: string
  checkOut?: string
  people?: number
  page?: number
  size?: number
}) {
  const res = await api.get<RoomSummary[]>('/api/rooms/search', {
    params: {
      q: params.q,
      checkIn: params.checkIn,
      checkOut: params.checkOut,
      people: params.people ?? 2,
      page: params.page ?? 0,
      size: params.size ?? 12,
    },
  })
  return res.data
}

/**
 * 숙소 상세
 * GET /api/rooms/{stayId} -> room-service: /rooms/{stayId}
 */
export async function getRoomDetail(stayId: string) {
  const res = await api.get<RoomDetail>(`/api/rooms/${stayId}`)
  return res.data
}