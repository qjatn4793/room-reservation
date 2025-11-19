export type BookingPayload = {
  roomId: number
  userId: string
  checkIn: string  // "YYYY-MM-DD"
  checkOut: string
  amount: number
  // 추후 확장용 (백엔드는 지금 무시함)
  people?: number
  customer?: {
    name: string
    phone: string
    email?: string
  }
}

export type BookingResult = {
  reservationId: string
  status: 'HOLD' | 'CONFIRMED' | 'CANCELLED' | 'EXPIRED' | 'DENIED'
  roomId: number
  userId: string
  checkIn: string
  checkOut: string
  amount: number
}

export type PaymentStatus =
  | 'HOLD_PENDING'
  | 'HOLD'
  | 'CONFIRMED'
  | 'CANCELLED'
  | 'EXPIRED'
  | 'DENIED'

// 서버에서 /pay/result 같은 API 아직 없으니, 나중용
export interface PaymentResultData {
  reservationId: string
  status: PaymentStatus
  message: string
  canRetry: boolean
  timedOut: boolean
}