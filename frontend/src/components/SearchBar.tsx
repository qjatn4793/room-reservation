import { useEffect, useMemo, useRef, useState } from 'react'
import dayjs from 'dayjs'
import { useSearchStore } from '@/stores/searchStore'
import { fetchSuggestions } from '@/api/search'
import type { Suggestion } from '@/types/search'
import { useDebounce } from '@/hooks/useDebounce'
import SuggestionList from './SuggestionList'
import TagChips from './TagChips'
import RecentSearches from './RecentSearches'
import { useQuery } from '@tanstack/react-query'
import { searchRooms } from '@/api/rooms'
import RoomCard from './RoomCard'

export default function SearchBar() {
  const {
    keyword, setKeyword, dates, setDates, people, setPeople,
    recent, addRecent, removeRecent, clearRecent, recommendedTags, setLastPicked
  } = useSearchStore()

  const [open, setOpen] = useState(false)
  const [suggests, setSuggests] = useState<Suggestion[]>([])
  const [activeIndex, setActiveIndex] = useState(0)
  const debounced = useDebounce(keyword, 200)
  const wrapRef = useRef<HTMLDivElement>(null)

  // 👉 인라인 검색용: 마지막으로 제출한 파라미터를 저장
  const [submitted, setSubmitted] = useState<{
    q: string; checkIn: string; checkOut: string; people: number
  } | null>(null)

  // 외부 클릭 닫기
  useEffect(() => {
    const onDocClick = (e: MouseEvent) => {
      if (!wrapRef.current) return
      if (!wrapRef.current.contains(e.target as Node)) setOpen(false)
    }
    document.addEventListener('mousedown', onDocClick)
    return () => document.removeEventListener('mousedown', onDocClick)
  }, [])

  // 자동완성 fetch
  useEffect(() => {
    let mounted = true
    ;(async () => {
      if (!debounced.trim()) { setSuggests([]); return }
      try {
        const data = await fetchSuggestions(debounced)
        if (mounted) setSuggests(data.items)
      } catch {
        if (mounted) setSuggests([])
      }
    })()
    return () => { mounted = false }
  }, [debounced])

  const minCheckIn = useMemo(() => dayjs().format('YYYY-MM-DD'), [])
  const minCheckOut = dates.checkIn || minCheckIn

  // ✅ 페이지 이동 대신 인라인 결과를 위한 제출 로직
  const submit = (e?: React.FormEvent) => {
    e?.preventDefault()
    const q = keyword.trim()
    if (q) addRecent(q)
    setOpen(false)
    setSubmitted({
      q,
      checkIn: dates.checkIn,
      checkOut: dates.checkOut,
      people,
    })
  }

  const pickSuggestion = (s: Suggestion) => {
    setKeyword(s.name)
    setLastPicked(s)
    setOpen(false)
    submit()
  }

  const onKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (!open || !suggests.length) return
    if (e.key === 'ArrowDown') {
      e.preventDefault()
      setActiveIndex((i) => (i + 1) % suggests.length)
    } else if (e.key === 'ArrowUp') {
      e.preventDefault()
      setActiveIndex((i) => (i - 1 + suggests.length) % suggests.length)
    } else if (e.key === 'Enter') {
      e.preventDefault()
      const target = suggests[activeIndex]
      if (target) return pickSuggestion(target)
      submit()
    } else if (e.key === 'Escape') {
      setOpen(false)
    }
  }

  const onFocusInput = () => setOpen(true)

  const applyTag = (tag: string) => {
    setKeyword(tag)
    setOpen(true)
  }

  const applyRecent = (kw: string) => {
    setKeyword(kw)
    setOpen(true)
  }

  // 🔍 인라인 검색 쿼리 (제출되었을 때만 실행)
  const { data, isFetching, isError } = useQuery({
    queryKey: ['inline-search', submitted],
    queryFn: () => searchRooms(submitted!),
    enabled: !!submitted,                // 제출 전에는 호출하지 않음
    staleTime: 30_000,
  })

  return (
    <form onSubmit={submit} className="space-y-2">
      <div className="grid grid-cols-1 md:grid-cols-4 gap-2 items-stretch" ref={wrapRef}>
        {/* 검색어 */}
        <div className="relative">
          <input
            className="form-control"
            placeholder="지역/지하철/랜드마크로 검색"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onKeyDown={onKeyDown}
            onFocus={onFocusInput}
          />
          {open && (
            <SuggestionList
              items={suggests}
              activeIndex={activeIndex}
              onPick={pickSuggestion}
            />
          )}
        </div>

        {/* 체크인/체크아웃 */}
        <input
          type="date"
          className="form-control"
          value={dates.checkIn}
          onChange={(e) => setDates({ ...dates, checkIn: e.target.value })}
          min={minCheckIn}
        />
        <input
          type="date"
          className="form-control"
          value={dates.checkOut}
          onChange={(e) => setDates({ ...dates, checkOut: e.target.value })}
          min={minCheckOut}
        />

        {/* 인원 + 검색 버튼 */}
        <div className="flex gap-2">
          <div className="form-group">
            <span className="form-prefix">인원</span>
            <input
              type="number"
              min={1}
              value={people}
              onChange={(e) => setPeople(Number(e.target.value))}
              className="form-input"
            />
          </div>
          <button className="btn-primary" type="submit">검색</button>
        </div>
      </div>

      {/* 최근검색 & 추천태그 패널 */}
      <div className="border rounded-xl p-3 space-y-4">
        <RecentSearches
          items={recent}
          onClick={applyRecent}
          onRemove={removeRecent}
          onClear={clearRecent}
        />
        <TagChips
          title="추천 태그"
          items={recommendedTags}
          onClick={applyTag}
        />
      </div>

      {/* ▼▼▼ 인라인 검색 결과 영역 ▼▼▼ */}
      <div className="mt-4">
        {!submitted && (
          <div className="text-sm text-gray-500">
            검색어를 입력하고 <b>검색</b>을 눌러 결과를 확인하세요.
          </div>
        )}

        {submitted && (
          <>
            {isFetching && <div>로딩 중…</div>}
            {isError && <div className="text-red-500">목록을 불러오지 못했습니다.</div>}

            {!isFetching && data && data.length === 0 && (
              <div className="text-gray-600">검색 결과가 없습니다.</div>
            )}

            {!isFetching && data && data.length > 0 && (
              <section className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {data.map((r) => (
                  <RoomCard key={r.id} room={r} />
                ))}
              </section>
            )}
          </>
        )}
      </div>
    </form>
  )
}
