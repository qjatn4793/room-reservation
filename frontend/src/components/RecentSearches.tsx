type Props = {
  items: string[]
  onClick: (v: string) => void
  onRemove: (v: string) => void
  onClear: () => void
}

export default function RecentSearches({ items, onClick, onRemove, onClear }: Props) {
  if (!items?.length) return null
  return (
    <div className="space-y-2">
      <div className="flex items-center justify-between">
        <div className="text-sm font-semibold">최근 검색</div>
        <button className="text-xs text-gray-500 hover:underline" type="button" onClick={onClear}>전체삭제</button>
      </div>
      <div className="flex flex-wrap gap-2">
        {items.map(v => (
          <div key={v} className="flex items-center gap-1 border rounded-full pl-3 pr-2 py-1">
            <button className="text-sm" type="button" onClick={() => onClick(v)}>{v}</button>
            <button
              className="text-gray-400 hover:text-gray-600"
              type="button"
              aria-label="remove"
              onClick={() => onRemove(v)}
            >
              ×
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}
