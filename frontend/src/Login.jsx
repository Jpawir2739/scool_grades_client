import { useState } from 'react'
import { login } from './auth'
import './App.css'

export default function Login({ onSuccess }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  function onSubmit(event) {
    event.preventDefault()
    if (login(username.trim(), password)) {
      setError('')
      onSuccess()
      return
    }
    setError('Неверный логин или пароль')
  }

  return (
    <form className="login" onSubmit={onSubmit}>
      <h1>Вход</h1>
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
          autoComplete="current-password"
        />
      </label>
      <button type="submit">Войти</button>
    </form>
  )
}
