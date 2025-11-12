type Props = {
  title: string
  items: string[]
  onClick: (v: string) => void
}

export default function TagChips({ title, items, onClick }: Props) {
  if (!items?.length) return null
  return (
    <div className="space-y-2">
      <div className="text-sm font-semibold">{title}</div>
      <div className="flex flex-wrap gap-2">
        {items.map(t => (
          <button
            key={t}
            className="px-3 py-1 rounded-full border hover:bg-gray-50"
            onClick={() => onClick(t)}
            type="button"
          >
            #{t}
          </button>
        ))}
      </div>
    </div>
  )
}
