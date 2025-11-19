import { useSearchParams, Link } from 'react-router-dom'

export default function PaymentResult() {
  const [params] = useSearchParams()
  const status = (params.get('status') || '').toUpperCase()
  const reservationId = params.get('reservationId') || '-'
  const timedOut = params.get('timedOut') === 'true'

  const isSuccess = status === 'CONFIRMED'

  let title = '결제 결과'
  let description = ''

  if (timedOut) {
    title = '결제 결과 확인 지연'
    description = '결제 결과를 아직 확인하지 못했습니다. 잠시 후 다시 시도해 주세요.'
  } else if (isSuccess) {
    title = '결제 성공'
    description = '결제가 정상적으로 완료되었습니다.'
  } else if (status === 'EXPIRED') {
    title = '결제 시간 초과'
    description = '결제 또는 홀드 시간이 만료되었습니다. 다시 시도해 주세요.'
  } else if (status === 'DENIED') {
    title = '예약 거절'
    description = '재고 부족 등으로 예약이 거절되었습니다.'
  } else if (status === 'CANCELLED') {
    title = '결제 취소'
    description = '사용자가 결제를 취소했습니다.'
  } else {
    title = '결제 실패'
    description = '결제 처리 중 문제가 발생했습니다.'
  }

  return (
    <section className="max-w-lg mx-auto space-y-3 text-center">
      <h1 className="text-xl font-bold">{title}</h1>
      <div className="text-sm">예약번호: {reservationId}</div>
      {description && <p className="text-sm text-gray-600 whitespace-pre-line">{description}</p>}

      <Link to="/" className="inline-block mt-2 underline">
        홈으로
      </Link>
    </section>
  )
}
