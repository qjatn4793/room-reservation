import { useEffect, useState } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { startPayment, waitPaymentResult } from '@/api/pays'
import { PaymentResultData } from '@/types/pay'

export default function PaymentWaitPage() {
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const reservationId = params.get('reservationId')

  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!reservationId) {
      setError('예약 번호가 없습니다.')
      setLoading(false)
      return
    }

    let cancelled = false

    ;(async () => {
      try {
        // 1) 결제 시작 (HOLD → HOLD_PENDING + 이벤트 발행)
        await startPayment(reservationId)

        // 2) 결제 결과 대기 (최대 30초, MVC waitPaymentResult)
        const result: PaymentResultData = await waitPaymentResult(reservationId)

        if (cancelled) return

        // 3) 결과 페이지로 이동
        // status 는 ReservationStatus 그대로 넘기고,
        // timedOut 이 true 면 front 에서도 "조금 있다 새로고침" 같은 메시지 보여줄 수 있음
        navigate(
          `/payment/result?reservationId=${reservationId}` +
            `&status=${result.status}` +
            `&timedOut=${result.timedOut ? 'true' : 'false'}`
        )
      } catch (e: any) {
        if (cancelled) return
        console.error(e)
        const message =
          e?.response?.data?.message ||
          e?.message ||
          '결제 처리 중 오류가 발생했습니다.'
        setError(message)
        setLoading(false)
      }
    })()

    return () => {
      cancelled = true
    }
  }, [reservationId, navigate])

  if (error) {
    return (
      <section className="max-w-lg mx-auto text-center space-y-3">
        <h1 className="text-xl font-bold">결제 처리 중 오류</h1>
        <p className="text-sm text-red-500 whitespace-pre-line">{error}</p>
      </section>
    )
  }

  return (
    <section className="max-w-lg mx-auto text-center space-y-4">
      <h1 className="text-xl font-bold">결제 처리 중입니다…</h1>
      <p className="text-sm text-gray-500">
        결제 결과를 확인하고 있어요.
        <br />
        최대 30초 정도 걸릴 수 있습니다.
      </p>

      {loading && (
        <div className="mt-4 flex justify-center">
          {/* 간단한 로딩 스피너 예시 */}
          <div className="w-8 h-8 border-4 border-gray-300 border-t-transparent rounded-full animate-spin" />
        </div>
      )}
    </section>
  )
}
