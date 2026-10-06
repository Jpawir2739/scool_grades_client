const SESSION_KEY = 'grades-mock-session'
const USERS_KEY = 'grades-mock-users'
const NOTICE_KEY = 'grades-mock-notice'

export const MOCK_CREDENTIALS = {
  username: 'teacher',
  password: 'teacher',
}

function readUsers() {
  try {
    const parsed = JSON.parse(localStorage.getItem(USERS_KEY) || '[]')
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

export function isLoggedIn() {
  return sessionStorage.getItem(SESSION_KEY) === '1'
}

export function login(username, password) {
  const name = username.trim()
  const builtin = name === MOCK_CREDENTIALS.username && password === MOCK_CREDENTIALS.password
  const registered = readUsers().some((user) => user.username === name && user.password === password)
  if (builtin || registered) {
    sessionStorage.setItem(SESSION_KEY, '1')
    return true
  }
  return false
}

export function register(username, password) {
  const name = username.trim()
  if (!name || !password) {
    return 'Заполните логин и пароль'
  }
  if (name === MOCK_CREDENTIALS.username || readUsers().some((user) => user.username === name)) {
    return 'Такой логин уже есть'
  }
  const users = readUsers()
  users.push({ username: name, password })
  localStorage.setItem(USERS_KEY, JSON.stringify(users))
  sessionStorage.setItem(NOTICE_KEY, 'Аккаунт создан, теперь можно войти')
  return ''
}

export function takeNotice() {
  const text = sessionStorage.getItem(NOTICE_KEY) || ''
  sessionStorage.removeItem(NOTICE_KEY)
  return text
}

export function logout() {
  sessionStorage.removeItem(SESSION_KEY)
}
