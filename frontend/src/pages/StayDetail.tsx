import { useParams, Link, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { getRoomDetail } from '@/api/rooms'

export default function StayDetail() {
  const { stayId } = useParams()
  const navigate = useNavigate()
  const { data, isLoading } = useQuery({
    queryKey: ['stay', stayId],
    queryFn: () => getRoomDetail(stayId!),
    enabled: !!stayId
  })

  if (isLoading) return <div>불러오는 중…</div>
  if (!data) return <div>존재하지 않는 숙소입니다.</div>

  return (
    <section className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        <img src={data.images?.[0]} alt={data.name} className="rounded-xl w-full object-cover max-h-96" />
        <div className="space-y-2">
          <h1 className="text-2xl font-bold">{data.name}</h1>
          <div className="text-sm text-gray-600">{data.location} · ★ {data.rating} ({data.reviewCount})</div>
          <p className="text-sm">{data.description}</p>
          <ul className="text-sm list-disc list-inside">
            {data.amenities?.slice(0,6).map((a) => <li key={a}>{a}</li>)}
          </ul>
        </div>
      </div>

      <div className="space-y-2">
        <h2 className="font-semibold">예약 가능한 객실</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          {data.rooms.map((room) => (
            <div key={room.roomId} className="border rounded-xl p-3 flex items-center justify-between">
              <div>
                <div className="font-semibold">{room.name}</div>
                <div className="text-sm text-gray-600">최대 {room.maxPeople}인</div>
              </div>
              <div className="text-right">
                <div className="font-bold">₩ {room.price.toLocaleString()}</div>
                <button
                  className="mt-2 px-3 py-2 rounded-lg bg-black text-white"
                  onClick={() => navigate(`/booking/${room.roomId}`)}
                >
                  예약하기
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      <Link to="/stays" className="text-sm underline">목록으로</Link>
    </section>
  )
}
