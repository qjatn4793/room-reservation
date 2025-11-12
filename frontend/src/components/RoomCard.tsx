import { Link } from 'react-router-dom'
import { RoomSummary } from '@/types/room'

export default function RoomCard({ room }: { room: RoomSummary }) {
  return (
    <Link to={'/stays/' + room.id} className="block border rounded-xl overflow-hidden hover:shadow-md transition">
      <img src={room.thumbnailUrl} alt={room.name} className="w-full h-48 object-cover" />
      <div className="p-3">
        <div className="text-sm text-gray-500">{room.location}</div>
        <div className="font-semibold">{room.name}</div>
        <div className="text-sm">₩ {room.price.toLocaleString()} / 박</div>
        <div className="text-xs text-gray-500">★ {room.rating} · 리뷰 {room.reviewCount}</div>
      </div>
    </Link>
  )
}
