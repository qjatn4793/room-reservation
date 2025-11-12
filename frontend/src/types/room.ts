export type RoomSummary = {
  id: string
  name: string
  location: string
  price: number
  rating: number
  reviewCount: number
  thumbnailUrl: string
}

export type RoomDetail = RoomSummary & {
  images: string[]
  description: string
  amenities: string[]
  policies: string[]
  rooms: Array<{
    roomId: string
    name: string
    maxPeople: number
    price: number
  }>
}

export type BookingPayload = {
  roomId: string
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
  bookingId: string
  amount: number
  currency: 'KRW'
}

export type PaymentResultData = {
  bookingId: string
  status: 'SUCCESS' | 'FAIL' | 'CANCEL'
  approvedAt?: string
  message?: string
}
