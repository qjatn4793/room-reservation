import { useParams, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { createBooking, createPayment } from '@/api/pays'

export default function Booking() {
  const { roomId } = useParams()
  const navigate = useNavigate()
  const [name, setName] = useState('')
  const [phone, setPhone] = useState('')

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!roomId) return
    const booking = await createBooking({
      roomId,
      userId: "test", // TODO :::: userId 나중에 로그인된 유저 ID 로 변경필요
      checkIn: new Date().toISOString().slice(0,10),
      checkOut: new Date(Date.now() + 86400000).toISOString().slice(0,10),
      people: 2,
      customer: { name, phone }
    })
    const { redirectUrl } = await createPayment(booking.reservationId)
    // In real production you'd redirect to PG; for now, we emulate redirect result
    window.location.href = redirectUrl || '/payment/result?status=SUCCESS&bookingId=' + booking.reservationId
  }

  return (
    <section className="max-w-lg mx-auto space-y-4">
      <h1 className="text-xl font-bold">예약 정보 입력</h1>
      <form onSubmit={submit} className="space-y-2">
        <input className="border rounded-lg px-3 py-2 w-full" placeholder="이름" value={name} onChange={e=>setName(e.target.value)} />
        <input className="border rounded-lg px-3 py-2 w-full" placeholder="연락처" value={phone} onChange={e=>setPhone(e.target.value)} />
        <button className="px-4 py-2 rounded-lg bg-black text-white w-full">결제하기</button>
      </form>
    </section>
  )
}
