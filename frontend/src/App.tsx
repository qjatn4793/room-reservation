import { useEffect, useState } from 'react'
import axios from 'axios'

type Room = { id: number; name: string; city: string; pricePerNight: number | null }

export default function App() {
    const [rooms, setRooms] = useState<Room[]>([])
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        (async () => {
            try {
                const res = await axios.get('/api/rooms/2', { validateStatus: () => true })
                const ct = res.headers['content-type'] ?? ''
                // 응답이 JSON 배열인지 점검
                if (res.status >= 400) {
                    setError(`API ${res.status} 오류`)
                } else if (ct.includes('application/json') && Array.isArray(res.data)) {
                    setRooms(res.data)
                } else {
                    setError('응답이 배열 JSON이 아님(프록시/URL/CORS 확인 필요)')
                    console.debug('Raw response:', res.data)
                }
            } catch (e: any) {
                setError(e?.message ?? '네트워크 오류')
            } finally {
                setLoading(false)
            }
        })()
    }, [])

    if (loading) return <div style={{ padding: 24 }}>로딩 중…</div>
    if (error)   return <div style={{ padding: 24, color: 'crimson' }}>에러: {error}</div>

    return (
        <div style={{ padding: 24 }}>
            <h1>RoomReservation</h1>
            {rooms.length === 0 ? (
                <p>등록된 숙소가 없습니다.</p>
            ) : (
                <ul>
                    {rooms.map(r => (
                        <li key={r.id}>
                            {r.name} · {r.city} · {r.pricePerNight?.toLocaleString() ?? '-'}원/박
                        </li>
                    ))}
                </ul>
            )}
        </div>
    )
}
