// 숙소 상세에서 내려오는 객실 아이템
export interface StayRoomItem {
  roomId: number
  name: string
  maxPeople: number
  price: number
}

// 목록/검색에서 쓸 수 있는 숙소 요약 타입
export interface StaySummary {
  id: number
  name: string
  location: string
  rating: number
  reviewCount: number
  thumbnailUrl?: string | null
  // 필요하면 최소 가격 같은 것도 나중에 추가 가능
  // minPrice?: number
}

// 숙소 상세 타입
export interface StayDetail extends StaySummary {
  description?: string | null
  amenities: string[]
  images: string[]
  rooms: StayRoomItem[]
}
