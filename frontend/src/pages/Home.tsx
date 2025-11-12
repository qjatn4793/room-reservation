import SearchBar from '@/components/SearchBar'

export default function Home() {
  return (
    <section className="space-y-4">
      <h1 className="text-2xl font-bold">어디로 떠나시나요?</h1>
      <SearchBar />
      {/* <div className="text-sm text-gray-500">인기 목적지, 추천 특가 등 섹션을 배치하세요.</div> */}
    </section>
  )
}
