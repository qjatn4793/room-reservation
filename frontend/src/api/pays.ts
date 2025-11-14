import { api } from './client'
import {
  BookingPayload,
  BookingResult,
  PaymentResultData,
} from '@/types/pay'

/**
 * 예약 생성
 * POST /api/booking -> pay-service: /booking
 * (BookingPayload 안에 stayId, roomId, dates, people 등 들어있다고 가정)
 */
export async function createBooking(payload: BookingPayload) {
  const res = await api.post<BookingResult>('/api/booking', payload)
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