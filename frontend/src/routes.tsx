import { createBrowserRouter } from 'react-router-dom'
import App from './App'
import Home from './pages/Home'
import Stays from './pages/Stays'
import StayDetail from './pages/StayDetail'
import Booking from './pages/Booking'
import PaymentResult from './pages/PaymentResult'
import MyPage from './pages/MyPage'
import Login from './pages/Login'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      { index: true, element: <Home /> },
      { path: 'stays', element: <Stays /> },
      { path: 'stays/:stayId', element: <StayDetail /> },
      { path: 'booking/:roomId', element: <Booking /> },
      { path: 'payment/result', element: <PaymentResult /> },
      { path: 'mypage', element: <MyPage /> },
      { path: 'login', element: <Login /> },
    ]
  }
])
