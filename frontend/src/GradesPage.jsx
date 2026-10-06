import { useEffect, useState } from 'react'
import {
  createGrade,
  deleteGrade,
  fetchGrades,
  fetchStudents,
  fetchSubjects,
  updateGrade,
} from './api'
import './App.css'

const emptyForm = () => ({
  studentId: '',
  subjectId: '',
  value: '5',
  gradeDate: new Date().toISOString().slice(0, 10),
  comment: '',
})

function toPayload(form) {
  return {
    studentId: Number(form.studentId),
    subjectId: Number(form.subjectId),
    value: Number(form.value),
    gradeDate: form.gradeDate,
    comment: form.comment.trim() ? form.comment.trim() : null,
  }
}

export default function GradesPage({ onLogout }) {
  const [grades, setGrades] = useState([])
  const [students, setStudents] = useState([])
  const [subjects, setSubjects] = useState([])
  const [selectedId, setSelectedId] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  async function load() {
    const [gradeRows, studentRows, subjectRows] = await Promise.all([
      fetchGrades(),
      fetchStudents(),
      fetchSubjects(),
    ])
    setGrades(gradeRows)
    setStudents(studentRows)
    setSubjects(subjectRows)
    setForm((current) => ({
      ...current,
      studentId: current.studentId || (studentRows[0] ? String(studentRows[0].id) : ''),
      subjectId: current.subjectId || (subjectRows[0] ? String(subjectRows[0].id) : ''),
    }))
  }

  useEffect(() => {
    load()
      .catch((err) => setError(err.message || 'Не удалось загрузить данные'))
      .finally(() => setLoading(false))
  }, [])

  function selectGrade(grade) {
    setSelectedId(grade.id)
    setForm({
      studentId: String(grade.studentId),
      subjectId: String(grade.subjectId),
      value: String(grade.value),
      gradeDate: grade.gradeDate,
      comment: grade.comment || '',
    })
    setError('')
  }

  function changeField(event) {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
  }

  async function runAction(action) {
    setError('')
    try {
      await action()
      await load()
    } catch (err) {
      setError(err.message || 'Ошибка запроса')
    }
  }

  function onAdd() {
    if (!form.studentId || !form.subjectId || !form.gradeDate) {
      setError('Заполните ученика, предмет и дату')
      return
    }
    runAction(async () => {
      const created = await createGrade(toPayload(form))
      setSelectedId(created.id)
    })
  }

  function onUpdate() {
    if (selectedId == null) {
      setError('Выберите строку для обновления')
      return
    }
    runAction(() => updateGrade(selectedId, toPayload(form)))
  }

  function onDelete() {
    if (selectedId == null) {
      setError('Выберите строку для удаления')
      return
    }
    runAction(async () => {
      await deleteGrade(selectedId)
      setSelectedId(null)
      setForm(emptyForm())
    })
  }

  function onShowAll() {
    setError('')
    setLoading(true)
    fetchGrades()
      .then((rows) => {
        setGrades(rows)
        setSelectedId(null)
      })
      .catch((err) => setError(err.message || 'Не удалось загрузить отметки'))
      .finally(() => setLoading(false))
  }

  return (
    <main className="page">
      <header className="header">
        <h1>Журнал оценок</h1>
        <button type="button" onClick={onLogout}>
          Выйти
        </button>
      </header>

      {error ? <p className="error">{error}</p> : null}

      <form className="form" onSubmit={(event) => event.preventDefault()}>
        <label>
          Ученик
          <select name="studentId" value={form.studentId} onChange={changeField}>
            {students.map((student) => (
              <option key={student.id} value={student.id}>
                {student.lastName} {student.firstName}
              </option>
            ))}
          </select>
        </label>
        <label>
          Предмет
          <select name="subjectId" value={form.subjectId} onChange={changeField}>
            {subjects.map((subject) => (
              <option key={subject.id} value={subject.id}>
                {subject.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          Оценка
          <select name="value" value={form.value} onChange={changeField}>
            {[1, 2, 3, 4, 5].map((value) => (
              <option key={value} value={value}>
                {value}
              </option>
            ))}
          </select>
        </label>
        <label>
          Дата
          <input type="date" name="gradeDate" value={form.gradeDate} onChange={changeField} />
        </label>
        <label className="comment">
          Комментарий
          <input name="comment" value={form.comment} onChange={changeField} />
        </label>
        <div className="actions">
          <button type="button" onClick={onAdd}>Добавить</button>
          <button type="button" onClick={onUpdate}>Обновить</button>
          <button type="button" onClick={onDelete}>Удалить</button>
          <button type="button" onClick={onShowAll}>Все отметки</button>
        </div>
      </form>

      {loading ? <p>Загрузка...</p> : null}

      <table>
        <thead>
          <tr>
            <th>Ученик</th>
            <th>Предмет</th>
            <th>Учитель</th>
            <th>Оценка</th>
            <th>Дата</th>
            <th>Комментарий</th>
          </tr>
        </thead>
        <tbody>
          {grades.map((grade) => (
            <tr
              key={grade.id}
              className={grade.id === selectedId ? 'selected' : undefined}
              onClick={() => selectGrade(grade)}
            >
              <td>{grade.studentFullName}</td>
              <td>{grade.subjectName}</td>
              <td>{grade.teacherFullName}</td>
              <td>{grade.value}</td>
              <td>{grade.gradeDate}</td>
              <td>{grade.comment || ''}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </main>
  )
}
