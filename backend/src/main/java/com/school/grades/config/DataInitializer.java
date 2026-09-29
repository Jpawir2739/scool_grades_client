package com.school.grades.config;

import com.school.grades.entity.*;
import com.school.grades.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Наполняет базу данных начальными данными при первом запуске.
 *
 * Пользователи:
 *   admin   / admin123   — ROLE_ADMIN
 *   teacher / teacher123 — ROLE_TEACHER
 *
 * Тестовые данные: 3 класса, 9 учеников, 4 предмета, 34 оценки.
 * Повторный запуск безопасен — данные создаются только если их ещё нет.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository        userRepository;
    private final SchoolClassRepository classRepository;
    private final StudentRepository     studentRepository;
    private final SubjectRepository     subjectRepository;
    private final GradeRepository       gradeRepository;
    private final PasswordEncoder       passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // ── Пользователи ──────────────────────────────────────────────────────
        User admin = createUserIfAbsent("admin", "admin123", "System", "Admin", Role.ROLE_ADMIN);
        User teacher = createUserIfAbsent("teacher", "teacher123", "Иван", "Петров", Role.ROLE_TEACHER);

        // Если классы уже есть — считаем БД уже инициализированной
        if (classRepository.count() > 0) {
            log.info("Database already seeded, skipping test data.");
            return;
        }

        // ── Классы ────────────────────────────────────────────────────────────
        SchoolClass class8v  = saveClass("8В",  2024);
        SchoolClass class9a  = saveClass("9А",  2024);
        SchoolClass class10b = saveClass("10Б", 2024);

        // ── Предметы ──────────────────────────────────────────────────────────
        Subject math    = saveSubject("Математика",  "Алгебра и геометрия");
        Subject physics = saveSubject("Физика",      "Основы физики");
        Subject russian = saveSubject("Русский язык","Грамматика и литература");
        Subject history = saveSubject("История",     "История России и мира");

        // ── Ученики 9А ────────────────────────────────────────────────────────
        Student ivanov   = saveStudent("Алексей",   "Иванов",   LocalDate.of(2009, 3, 15), class9a);
        Student smirnova = saveStudent("Мария",     "Смирнова", LocalDate.of(2009, 7, 22), class9a);
        Student petrov   = saveStudent("Дмитрий",  "Петров",   LocalDate.of(2009, 11, 5), class9a);

        // ── Ученики 10Б ───────────────────────────────────────────────────────
        Student kozlova  = saveStudent("Анна",      "Козлова",  LocalDate.of(2008, 1, 30), class10b);
        Student novikov  = saveStudent("Сергей",    "Новиков",  LocalDate.of(2008, 6, 18), class10b);
        Student morozova = saveStudent("Екатерина", "Морозова", LocalDate.of(2008, 9, 12), class10b);

        // ── Ученики 8В ────────────────────────────────────────────────────────
        Student sokolov  = saveStudent("Павел",  "Соколов",  LocalDate.of(2010, 2, 14), class8v);
        Student volkova  = saveStudent("Ольга",  "Волкова",  LocalDate.of(2010, 5, 9),  class8v);
        Student orlov    = saveStudent("Никита", "Орлов",    LocalDate.of(2010, 12, 1), class8v);

        // ── Оценки ────────────────────────────────────────────────────────────
        // Иванов
        saveGrade(ivanov,   math,    teacher, 5, LocalDate.of(2024, 9, 10), "Отлично");
        saveGrade(ivanov,   math,    teacher, 4, LocalDate.of(2024, 10, 1), null);
        saveGrade(ivanov,   physics, teacher, 3, LocalDate.of(2024, 9, 20), "Нужно подтянуть");
        saveGrade(ivanov,   russian, teacher, 5, LocalDate.of(2024, 9, 25), null);

        // Смирнова
        saveGrade(smirnova, math,    teacher, 4, LocalDate.of(2024, 9, 10), null);
        saveGrade(smirnova, physics, teacher, 5, LocalDate.of(2024, 9, 20), "Молодец");
        saveGrade(smirnova, history, teacher, 4, LocalDate.of(2024, 10, 5), null);

        // Петров
        saveGrade(petrov,   math,    teacher, 3, LocalDate.of(2024, 9, 10), null);
        saveGrade(petrov,   russian, teacher, 4, LocalDate.of(2024, 9, 25), null);
        saveGrade(petrov,   history, teacher, 3, LocalDate.of(2024, 10, 5), "Слабовато");

        // Козлова
        saveGrade(kozlova,  math,    teacher, 5, LocalDate.of(2024, 9, 12), "Отличная работа");
        saveGrade(kozlova,  physics, teacher, 5, LocalDate.of(2024, 9, 22), null);
        saveGrade(kozlova,  history, teacher, 4, LocalDate.of(2024, 10, 7), null);

        // Новиков
        saveGrade(novikov,  math,    teacher, 2, LocalDate.of(2024, 9, 12), "Не готов к уроку");
        saveGrade(novikov,  physics, teacher, 3, LocalDate.of(2024, 9, 22), null);
        saveGrade(novikov,  russian, teacher, 4, LocalDate.of(2024, 10, 3), null);

        // Морозова
        saveGrade(morozova, math,    teacher, 4, LocalDate.of(2024, 9, 12), null);
        saveGrade(morozova, history, teacher, 5, LocalDate.of(2024, 10, 7), "Отлично");
        saveGrade(morozova, russian, teacher, 5, LocalDate.of(2024, 10, 3), null);
        saveGrade(morozova, physics, teacher, 4, LocalDate.of(2024, 10, 15), null);

        // Недостающие предметы у остальных учеников
        saveGrade(ivanov,   history, teacher, 4, LocalDate.of(2024, 10, 8), null);
        saveGrade(smirnova, russian, teacher, 5, LocalDate.of(2024, 10, 11), "Грамотная работа");
        saveGrade(petrov,   physics, teacher, 3, LocalDate.of(2024, 10, 14), null);
        saveGrade(kozlova,  russian, teacher, 5, LocalDate.of(2024, 10, 16), null);
        saveGrade(novikov,  history, teacher, 3, LocalDate.of(2024, 10, 18), null);

        // 8В
        saveGrade(sokolov, math,    teacher, 4, LocalDate.of(2024, 9, 16), null);
        saveGrade(sokolov, physics, teacher, 4, LocalDate.of(2024, 9, 27), "Старается");
        saveGrade(sokolov, russian, teacher, 3, LocalDate.of(2024, 10, 4), null);
        saveGrade(volkova, math,    teacher, 5, LocalDate.of(2024, 9, 16), "Отлично");
        saveGrade(volkova, history, teacher, 5, LocalDate.of(2024, 10, 9), null);
        saveGrade(volkova, russian, teacher, 4, LocalDate.of(2024, 10, 21), null);
        saveGrade(orlov,   physics, teacher, 2, LocalDate.of(2024, 9, 27), "Пропуск лабораторной");
        saveGrade(orlov,   history, teacher, 4, LocalDate.of(2024, 10, 9), null);
        saveGrade(orlov,   math,    teacher, 3, LocalDate.of(2024, 10, 23), null);

        log.info("Test data seeded: 3 classes, 9 students, 4 subjects, 34 grades.");
    }

    // ── Вспомогательные методы ────────────────────────────────────────────────

    private User createUserIfAbsent(String username, String password,
                                    String firstName, String lastName, Role role) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            User u = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .firstName(firstName)
                    .lastName(lastName)
                    .role(role)
                    .build();
            userRepository.save(u);
            log.info("User created: {} / {} ({})", username, password, role);
            return u;
        });
    }

    private SchoolClass saveClass(String name, int year) {
        return classRepository.findByNameAndAcademicYear(name, year).orElseGet(() ->
                classRepository.save(SchoolClass.builder().name(name).academicYear(year).build()));
    }

    private Subject saveSubject(String name, String description) {
        return subjectRepository.findByNameIgnoreCase(name).orElseGet(() ->
                subjectRepository.save(Subject.builder().name(name).description(description).build()));
    }

    private Student saveStudent(String firstName, String lastName,
                                LocalDate dob, SchoolClass schoolClass) {
        Student s = Student.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dob)
                .schoolClass(schoolClass)
                .build();
        return studentRepository.save(s);
    }

    private void saveGrade(Student student, Subject subject, User teacher,
                           int value, LocalDate date, String comment) {
        gradeRepository.save(Grade.builder()
                .student(student)
                .subject(subject)
                .teacher(teacher)
                .value(value)
                .gradeDate(date)
                .comment(comment)
                .build());
    }
}
