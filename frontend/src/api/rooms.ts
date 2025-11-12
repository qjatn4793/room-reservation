import { api } from './client'
import { RoomSummary, RoomDetail, BookingPayload, BookingResult, PaymentResultData } from '@/types/room'

export async function searchRooms(params: { q?: string; checkIn?: string; checkOut?: string; people?: number }) {
  const res = await api.get<RoomSummary[]>('/gateway/rooms', { params })
  return res.data
}

export async function getRoomDetail(stayId: string) {
  const res = await api.get<RoomDetail>(`/gateway/rooms/${stayId}`)
  return res.data
}

export async function createBooking(payload: BookingPayload) {
  const res = await api.post<BookingResult>('/gateway/booking', payload)
  return res.data
}

export async function createPayment(bookingId: string) {
  const res = await api.post<{ redirectUrl: string }>(`/gateway/pay/${bookingId}`)
  return res.data
}

export async function fetchPaymentResult(pgToken: string) {
  const res = await api.get<PaymentResultData>('/gateway/pay/result', { params: { pgToken } })
  return res.data
}
