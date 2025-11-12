import { Link } from 'react-router-dom'

export default function Header() {
  return (
    <header className="border-b">
      <div className="max-w-6xl mx-auto flex items-center justify-between p-4">
        <Link to="/" className="text-xl font-bold">GoodStay</Link>
        <nav className="flex gap-4">
          <Link to="/stays" className="hover:underline">숙소</Link>
          <Link to="/mypage" className="hover:underline">마이</Link>
        </nav>
      </div>
    </header>
  )
}
