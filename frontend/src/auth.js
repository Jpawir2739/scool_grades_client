const STORAGE_KEY = 'grades-mock-session'

export const MOCK_CREDENTIALS = {
  username: 'teacher',
  password: 'teacher',
}

export function isLoggedIn() {
  return sessionStorage.getItem(STORAGE_KEY) === '1'
}

export function login(username, password) {
  const accepted = username === MOCK_CREDENTIALS.username
    && password === MOCK_CREDENTIALS.password
  if (accepted) {
    sessionStorage.setItem(STORAGE_KEY, '1')
  }
  return accepted
}

export function logout() {
  sessionStorage.removeItem(STORAGE_KEY)
}
