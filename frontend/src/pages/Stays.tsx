import { useSearchParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { searchRooms } from '@/api/rooms'
import RoomCard from '@/components/RoomCard'

export default function Stays() {
  const [params] = useSearchParams()
  const q = params.get('q') ?? ''
  const checkIn = params.get('checkIn') ?? ''
  const checkOut = params.get('checkOut') ?? ''
  const people = params.get('people') ? Number(params.get('people')) : undefined

  const { data, isLoading, isError } = useQuery({
    queryKey: ['stays', q, checkIn, checkOut, people],
    queryFn: () => searchRooms({ q, checkIn, checkOut, people }),
  })

  if (isLoading) return <div>로딩 중…</div>
  if (isError) return <div>목록을 불러오지 못했습니다.</div>

  return (
    <section className="grid grid-cols-1 md:grid-cols-3 gap-4">
      {data?.map((r) => <RoomCard key={r.id} room={r} />)}
    </section>
  )
}
