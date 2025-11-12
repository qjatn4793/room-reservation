import type { Suggestion } from '@/types/search'

type Props = {
  items: Suggestion[]
  activeIndex: number
  onPick: (s: Suggestion) => void
}

const labelMap: Record<Suggestion['type'], string> = {
  region: '지역',
  subway: '지하철',
  landmark: '랜드마크'
}

export default function SuggestionList({ items, activeIndex, onPick }: Props) {
  if (!items?.length) return null

  return (
    <div className="absolute z-20 mt-1 w-full bg-white border rounded-xl shadow-lg overflow-hidden">
      <ul className="max-h-80 overflow-auto">
        {items.map((s, i) => (
          <li
            key={s.type + '-' + s.id}
            className={`px-3 py-2 cursor-pointer flex items-center gap-2 ${
              i === activeIndex ? 'bg-gray-100' : ''
            }`}
            onMouseDown={(e) => { e.preventDefault(); onPick(s) }}
          >
            <span className="text-xs px-2 py-0.5 rounded-full border text-gray-600">{labelMap[s.type]}</span>
            <span className="truncate">{s.name}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}
