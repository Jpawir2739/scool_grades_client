import { useEffect, useState } from 'react'

export function usePath() {
  const [path, setPath] = useState(() => window.location.pathname)

  useEffect(() => {
    function sync() {
      setPath(window.location.pathname)
    }

    window.addEventListener('popstate', sync)
    return () => window.removeEventListener('popstate', sync)
  }, [])

  return path
}

export function navigate(path) {
  if (window.location.pathname === path) {
    return
  }
  window.history.pushState({}, '', path)
  window.dispatchEvent(new PopStateEvent('popstate'))
}

export function Link({ to, children }) {
  return (
    <a
      href={to}
      onClick={(event) => {
        event.preventDefault()
        navigate(to)
      }}
    >
      {children}
    </a>
  )
}
