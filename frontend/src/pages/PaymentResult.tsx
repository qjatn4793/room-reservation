import { useSearchParams, Link } from 'react-router-dom'

export default function PaymentResult() {
  const [params] = useSearchParams()
  const status = params.get('status') || 'SUCCESS'
  const reservationId = params.get('reservationId') || '-'
  const isSuccess = status === 'SUCCESS'

  return (
    <section className="max-w-lg mx-auto space-y-3 text-center">
      <h1 className="text-xl font-bold">결제 {isSuccess ? '성공' : status === 'CANCEL' ? '취소' : '실패'}</h1>
      <div className="text-sm">예약번호: {reservationId}</div>
      <Link to="/" className="inline-block mt-2 underline">홈으로</Link>
    </section>
  )
}
