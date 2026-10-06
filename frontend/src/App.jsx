import { useEffect, useState } from 'react'
import { isLoggedIn, logout } from './auth'
import GradesPage from './GradesPage'
import Login from './Login'
import Register from './Register'
import { navigate, usePath } from './router.jsx'

export default function App() {
  const path = usePath()
  const [authorized, setAuthorized] = useState(() => isLoggedIn())

  useEffect(() => {
    if (!authorized && path !== '/login' && path !== '/register') {
      navigate('/login')
    }
    if (authorized && path !== '/grades') {
      navigate('/grades')
    }
  }, [authorized, path])

  if (path === '/register' && !authorized) {
    return <Register onSuccess={() => navigate('/login')} />
  }

  if (path === '/login' && !authorized) {
    return (
      <Login
        onSuccess={() => {
          setAuthorized(true)
          navigate('/grades')
        }}
      />
    )
  }

  if (path === '/grades' && authorized) {
    return (
      <GradesPage
        onLogout={() => {
          logout()
          setAuthorized(false)
          navigate('/login')
        }}
      />
    )
  }

  return null
}
