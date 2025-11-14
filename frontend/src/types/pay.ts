export type BookingPayload = {
  roomId: string
  userId: string
  checkIn: string
  checkOut: string
  people: number
  customer: {
    name: string
    phone: string
    email?: string
  }
}

export type BookingResult = {
  reservationId: string
  status: string
  roomId: number
  amount: number
  checkIn: string
  checkOut: string
  currency: 'KRW'
}

export type PaymentResultData = {
  bookingId: string
  status: 'SUCCESS' | 'FAIL' | 'CANCEL'
  approvedAt?: string
  message?: string
}
