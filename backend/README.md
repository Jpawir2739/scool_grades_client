# 🎓 School Grades — REST API

Система учёта оценок школьников на **Spring Boot 3.2 / Java 17 / MySQL 8**.

---

## Быстрый старт (Docker)

### Требования
- Docker ≥ 20.10
- Docker Compose ≥ 2.x

### 1. Клонировать / распаковать проект
```bash
cd school-grades
```

### 2. Запустить
```bash
docker compose up --build -d
```
Первый запуск дольше: Maven скачивает зависимости и собирает JAR внутри контейнера.

### 3. Проверить, что всё работает
```bash
docker compose ps          # оба контейнера должны быть "healthy" / "running"
docker compose logs -f app # логи приложения
```
API доступен на **http://localhost:8080**.

### 4. Остановить
```bash
docker compose down          # контейнеры удалятся, данные MySQL сохранятся в volume
docker compose down -v       # + удалить volume (сброс БД)
```

---

## Авторизация

Проект использует **JWT Bearer**-токены. Первый admin создаётся автоматически при старте.

### Получить токен
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```
Ответ: `{"token":"eyJ...","username":"admin","role":"ROLE_ADMIN"}`

### Использовать токен
```bash
curl http://localhost:8080/api/students \
  -H "Authorization: Bearer <token>"
```

---

## Эндпоинты

### Auth
| Метод | URL | Доступ | Описание |
|-------|-----|--------|----------|
| POST | /api/auth/login | Публичный | Получить JWT |
| POST | /api/auth/register | ADMIN | Создать пользователя |

### Классы `/api/classes`
| Метод | URL | Описание |
|-------|-----|----------|
| GET | /api/classes | Все классы |
| GET | /api/classes/{id} | Класс по ID |
| GET | /api/classes?year=2024 | Классы за год |
| POST | /api/classes | Создать класс (ADMIN) |
| PUT | /api/classes/{id} | Обновить (ADMIN) |
| DELETE | /api/classes/{id} | Удалить (ADMIN) |

### Ученики `/api/students`
| Метод | URL | Описание |
|-------|-----|----------|
| GET | /api/students | Все ученики |
| GET | /api/students/{id} | Ученик по ID |
| GET | /api/students/class/{classId} | Ученики класса |
| GET | /api/students/search?q=Иван | Поиск по имени/фамилии |
| GET | /api/students/{id}/average | Средние оценки по предметам |
| POST | /api/students | Создать (ADMIN) |
| PUT | /api/students/{id} | Обновить (ADMIN) |
| DELETE | /api/students/{id} | Удалить (ADMIN) |

### Предметы `/api/subjects`
| Метод | URL | Описание |
|-------|-----|----------|
| GET | /api/subjects | Все предметы |
| GET | /api/subjects/{id} | Предмет по ID |
| POST | /api/subjects | Создать (ADMIN) |
| PUT | /api/subjects/{id} | Обновить (ADMIN) |
| DELETE | /api/subjects/{id} | Удалить (ADMIN) |

### Оценки `/api/grades`
| Метод | URL | Описание |
|-------|-----|----------|
| GET | /api/grades | Все оценки |
| GET | /api/grades/{id} | Оценка по ID |
| GET | /api/grades/student/{studentId} | Оценки ученика |
| GET | /api/grades/subject/{subjectId} | Оценки по предмету |
| GET | /api/grades/class/{classId} | Оценки класса |
| GET | /api/grades?from=2024-01-01&to=2024-06-30 | Оценки за период |
| GET | /api/grades?studentId=1&subjectId=2 | Оценки ученика по предмету |
| POST | /api/grades | Выставить оценку |
| PUT | /api/grades/{id} | Исправить оценку |
| DELETE | /api/grades/{id} | Удалить оценку |
| **POST** | **/api/grades/import** | **Импорт из CSV/Excel** |

---

## Импорт оценок

### CSV формат
Первая строка — заголовок (обязательна):
```
student_id,subject_id,value,grade_date,comment
1,2,5,2024-01-15,Отлично
2,3,4,2024-01-16,
```

### Excel формат
Файл `.xlsx`, первый лист, первая строка — заголовок, те же 5 колонок.

### Запрос
```bash
curl -X POST http://localhost:8080/api/grades/import \
  -H "Authorization: Bearer <token>" \
  -F "file=@grades.csv"
```

### Ответ
```json
{
  "imported": 10,
  "skipped": 1,
  "errors": ["Line 5: Student not found with id: 999"]
}
```

---

## Структура проекта

```
src/main/java/com/school/grades/
├── config/          # SecurityConfig, DataInitializer
├── controller/      # REST-контроллеры
├── dto/             # Request/Response DTO
├── entity/          # JPA-сущности
├── exception/       # GlobalExceptionHandler
├── repository/      # Spring Data JPA
├── security/        # JWT + UserDetailsService
└── service/         # Бизнес-логика + ImportService
```

---

## Технические решения

### JWT (исправление двойного Base64)
Секрет из `application.yml` преобразуется **один раз**: `secret.getBytes(UTF_8)` → `Keys.hmacShaKeyFor(...)`.  
Никакого `Decoders.BASE64.decode(Encoders.BASE64.encode(...))`, который порождал пробелы в паддинге.

### BCrypt $2b$ vs $2a$
`PasswordEncoder` в `SecurityConfig` нормализует хэши с префиксом `$2b$` (Python / Node.js bcrypt)  
до `$2a$` перед проверкой — они семантически одинаковы, но Spring по умолчанию отклоняет `$2b$`.

---

## Сущности

| Сущность | Таблица | Описание |
|----------|---------|----------|
| `User` | `users` | Пользователи системы (ADMIN, TEACHER) |
| `SchoolClass` | `school_classes` | Школьный класс (5А, 10Б, год) |
| `Student` | `students` | Ученик, привязан к классу |
| `Subject` | `subjects` | Учебный предмет |
| `Grade` | `grades` | Оценка (1–5), связывает ученика, предмет и учителя |
