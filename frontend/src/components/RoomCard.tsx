import { Link } from 'react-router-dom'
import type { RoomSummary } from '@/types/room'

export default function RoomCard({ room }: { room: RoomSummary }) {
  return (
    <Link
      to={`/stays/${room.stayId}`}
      className="block border rounded-xl overflow-hidden hover:shadow-md transition"
    >
      <img
        src={room.thumbnailUrl ?? '/placeholder.jpg'}
        alt={room.name}
        className="w-full h-40 object-cover"
      />
      <div className="p-3">
        <div className="text-sm text-gray-500">{room.location}</div>
        <div className="font-semibold">{room.name}</div>
        <div className="text-sm text-gray-600">
          ★ {room.rating} ({room.reviewCount})
        </div>
        <div className="font-bold">
          ₩ {room.price.toLocaleString()}
        </div>
      </div>
    </Link>
  )
}