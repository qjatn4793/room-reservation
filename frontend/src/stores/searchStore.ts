import { create } from 'zustand'
import dayjs from 'dayjs'
import type { Suggestion } from '@/types/search'

type Dates = { checkIn: string; checkOut: string }

interface SearchState {
  keyword: string
  dates: Dates
  people: number
  recent: string[]                 // 최근 검색어
  recommendedTags: string[]        // 추천 태그
  lastPicked?: Suggestion          // 마지막으로 선택된 자동완성 항목 (옵션)
  setKeyword: (v: string) => void
  setDates: (d: Dates) => void
  setPeople: (n: number) => void
  addRecent: (kw: string) => void
  removeRecent: (kw: string) => void
  clearRecent: () => void
  setLastPicked: (s?: Suggestion) => void
}

const today = dayjs().format('YYYY-MM-DD')
const tomorrow = dayjs().add(1, 'day').format('YYYY-MM-DD')

const RECENT_KEY = 'goodstay_recent_keywords'

function loadRecent(): string[] {
  try { return JSON.parse(localStorage.getItem(RECENT_KEY) || '[]') } catch { return [] }
}
function saveRecent(list: string[]) {
  try { localStorage.setItem(RECENT_KEY, JSON.stringify(list)) } catch {}
}

export const useSearchStore = create<SearchState>((set, get) => ({
  keyword: '',
  dates: { checkIn: today, checkOut: tomorrow },
  people: 2,
  recent: loadRecent(),
  recommendedTags: ['스파', '가성비', '노을맛집', '야경', '한옥', '키즈', '애견동반', '호캉스'],

  setKeyword: (v) => set({ keyword: v }),
  setDates: (d) => set({ dates: d }),
  setPeople: (n) => set({ people: n }),

  addRecent: (kw) => {
    const trimmed = kw.trim()
    if (!trimmed) return
    const uniq = [trimmed, ...get().recent.filter(x => x !== trimmed)].slice(0, 10)
    saveRecent(uniq)
    set({ recent: uniq })
  },
  removeRecent: (kw) => {
    const next = get().recent.filter(x => x !== kw)
    saveRecent(next)
    set({ recent: next })
  },
  clearRecent: () => {
    saveRecent([])
    set({ recent: [] })
  },
  setLastPicked: (s) => set({ lastPicked: s })
}))
