import { api } from './client'
import {
  BookingPayload,
  BookingResult,
  PaymentResultData,
} from '@/types/pay'

// 공통으로 써도 되는 sleep 유틸
const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

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
 * 결제 시작
 * POST /api/pay/{reservationId}/start
 */
export async function startPayment(reservationId: string) {
  await api.post(`/api/pay/${reservationId}/start`);
}

/**
 * 결제 요청
 * POST /api/pay/{bookingId} -> pay-service: /pay/{bookingId}
 */
export async function createPayment(bookingId: string) {
  // 5초 딜레이
  await sleep(5000)

  const res = await api.post<{ redirectUrl: string }>(`/api/pay/${bookingId}`)
  return res.data
}

/**
 * 결제 결과 조회 & 확정 처리
 * GET /api/pay/result?status=...&reservationId=...&authId=...
 */
export async function fetchPaymentResult(params: {
  status: string       // "SUCCESS" | "FAIL" | "CANCEL"
  reservationId: string
  authId?: string | null
}) {
  const res = await api.get<PaymentResultData>('/api/pay/result', {
    params: {
      status: params.status,
      reservationId: params.reservationId,
      authId: params.authId ?? undefined,
    },
  })
  return res.data
}

/**
 * 결제 결과 대기 조회 (폴링)
 * GET /api/pay/wait-result/{reservationId}?timeoutSeconds=...
 */
export async function waitPaymentResult(reservationId: string, timeoutSeconds = 10) {
  const res = await api.get<PaymentResultData>(`/api/pay/wait-result/${reservationId}`, {
    params: { timeoutSeconds },
  });
  return res.data;
}