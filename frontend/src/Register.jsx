import { useState } from 'react'
import { register } from './auth'
import { Link } from './router.jsx'
import './App.css'

export default function Register({ onSuccess }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  function onSubmit(event) {
    event.preventDefault()
    const message = register(username, password)
    if (message) {
      setError(message)
      return
    }
    onSuccess()
  }

  return (
    <form className="login" onSubmit={onSubmit}>
      <h1>Регистрация</h1>
      {error ? <p className="error">{error}</p> : null}
      <label>
        Логин
        <input
          name="username"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          autoComplete="username"
        />
      </label>
      <label>
        Пароль
        <input
          name="password"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          autoComplete="new-password"
        />
      </label>
      <button type="submit">Создать аккаунт</button>
      <p className="hint">
        Уже есть аккаунт? <Link to="/login">Вход</Link>
      </p>
    </form>
  )
}
