export type RoomSummary = {
  id: number
  stayId: number
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