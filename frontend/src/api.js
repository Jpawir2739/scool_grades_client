async function request(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })

  if (response.status === 204) {
    return null
  }

  const text = await response.text()
  const data = text ? JSON.parse(text) : null

  if (!response.ok) {
    throw new Error(data?.message || response.statusText)
  }

  return data
}

export function fetchGrades() {
  return request('/api/grades')
}

export function fetchStudents() {
  return request('/api/students')
}

export function fetchSubjects() {
  return request('/api/subjects')
}

export function createGrade(body) {
  return request('/api/grades', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export function updateGrade(id, body) {
  return request(`/api/grades/${id}`, {
    method: 'PUT',
    body: JSON.stringify(body),
  })
}

export function deleteGrade(id) {
  return request(`/api/grades/${id}`, { method: 'DELETE' })
}
