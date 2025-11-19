import { useParams, useNavigate, useLocation } from 'react-router-dom'
import { useState } from 'react'
import { createBooking } from '@/api/pays'
import { useSearchStore } from '@/stores/searchStore'

type BookingLocationState = {
  room?: {
    roomId: string
    name: string
    price: number
    maxPeople: number
  }
}

/** 날짜 차이(박수) 계산: 최소 1박 */
function calcNights(checkIn: string, checkOut: string): number {
  const start = new Date(checkIn)
  const end = new Date(checkOut)
  const diffMs = end.getTime() - start.getTime()
  const nights = diffMs / (1000 * 60 * 60 * 24)
  return nights > 0 ? nights : 1
}

export default function Booking() {
  const { roomId } = useParams<{ roomId: string }>()
  const navigate = useNavigate()
  const location = useLocation()
  const state = location.state as BookingLocationState | null
  const room = state?.room

  const { dates, people } = useSearchStore()

  const [name, setName] = useState('')
  const [phone, setPhone] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!roomId) return

    try {
      setSubmitting(true)

      const nights = calcNights(dates.checkIn, dates.checkOut)
      const price = room?.price ?? 0
      const amount = price * nights

      // 1) 예약 생성 (→ pay-service: Reservation 생성, status=HOLD)
      const booking = await createBooking({
        roomId: Number(roomId),
        userId: 'test', // TODO: 나중에 로그인 유저 ID로 교체
        checkIn: dates.checkIn,
        checkOut: dates.checkOut,
        amount,
        people,
        customer: { name, phone },
      })

      // 2) 결제 대기 페이지로 이동
      //    여기서부터는 PaymentWaitPage 가:
      //    - /api/pay/{reservationId}/start
      //    - /api/pay/{reservationId}/wait
      //    를 호출하면서 상태 변화(HOLD_PENDING → CONFIRMED/EXPIRED/...)를 기다림
      navigate(`/payment/wait?reservationId=${booking.reservationId}`, {
        state: {
          room,
          checkIn: dates.checkIn,
          checkOut: dates.checkOut,
          people,
          amount,
        },
      })
    } catch (err) {
      console.error(err)
      alert('예약/결제 요청 중 오류가 발생했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="max-w-lg mx-auto space-y-4">
      <h1 className="text-xl font-bold">예약 정보 입력</h1>

      {/* 선택한 객실 요약 박스 */}
      {room && (
        <div className="border rounded-lg px-3 py-2 text-sm bg-gray-50 flex justify-between">
          <div>
            <div className="font-semibold">{room.name}</div>
            <div className="text-gray-600">
              {dates.checkIn} ~ {dates.checkOut} · {people}명
            </div>
          </div>
          <div className="text-right">
            <div className="text-xs text-gray-500">
              {calcNights(dates.checkIn, dates.checkOut)}박
            </div>
            <div className="font-bold">
              ₩ {room.price.toLocaleString()}
            </div>
          </div>
        </div>
      )}

      <form onSubmit={submit} className="space-y-2">
        <input
          className="border rounded-lg px-3 py-2 w-full"
          placeholder="이름"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <input
          className="border rounded-lg px-3 py-2 w-full"
          placeholder="연락처"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
          required
        />
        <button
          className="px-4 py-2 rounded-lg bg-black text-white w-full disabled:opacity-60"
          disabled={submitting}
        >
          {submitting ? '예약 생성 중…' : '결제하기'}
        </button>
      </form>

      <button
        type="button"
        className="text-sm underline"
        onClick={() => navigate(-1)}
      >
        뒤로가기
      </button>
    </section>
  )
}