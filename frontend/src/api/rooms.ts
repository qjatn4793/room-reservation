import { api } from './client'
import {
  RoomSummary,
  RoomDetail,
  BookingPayload,
  BookingResult,
  PaymentResultData,
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

/**
 * 예약 생성
 * POST /api/rooms/booking -> room-service: /rooms/booking
 * (BookingPayload 안에 stayId, roomId, dates, people 등 들어있다고 가정)
 */
export async function createBooking(payload: BookingPayload) {
  const res = await api.post<BookingResult>('/api/rooms/booking', payload)
  return res.data
}

/**
 * 결제 요청
 * POST /api/pay/{bookingId} -> pay-service: /pay/{bookingId}
 */
export async function createPayment(bookingId: string) {
  const res = await api.post<{ redirectUrl: string }>(`/api/pay/${bookingId}`)
  return res.data
}

/**
 * 결제 결과 조회
 * GET /api/pay/result?pgToken=... -> pay-service: /pay/result
 */
export async function fetchPaymentResult(pgToken: string) {
  const res = await api.get<PaymentResultData>('/api/pay/result', {
    params: { pgToken },
  })
  return res.data
}
