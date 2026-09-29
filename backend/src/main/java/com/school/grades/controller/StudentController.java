package com.school.grades.controller;

import com.school.grades.dto.StudentAverageDto;
import com.school.grades.dto.StudentDto;
import com.school.grades.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Ученики", description = "Управление учениками школы")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Получить всех учеников", description = "Возвращает полный список учеников во всех классах.")
    @ApiResponse(responseCode = "200", description = "Список учеников")
    @GetMapping
    public List<StudentDto> findAll() {
        return studentService.findAll();
    }

    @Operation(summary = "Получить ученика по ID", description = "Возвращает данные одного ученика по его идентификатору.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ученик найден"),
        @ApiResponse(responseCode = "404", description = "Ученик с таким ID не существует")
    })
    @GetMapping("/{id}")
    public StudentDto findById(
            @Parameter(description = "ID ученика", example = "1") @PathVariable Long id) {
        return studentService.findById(id);
    }

    @Operation(
        summary = "Получить учеников класса",
        description = "Возвращает всех учеников, прикреплённых к указанному классу."
    )
    @ApiResponse(responseCode = "200", description = "Список учеников класса")
    @GetMapping("/class/{classId}")
    public List<StudentDto> findByClass(
            @Parameter(description = "ID класса", example = "1") @PathVariable Long classId) {
        return studentService.findByClass(classId);
    }

    @Operation(
        summary = "Поиск учеников по имени или фамилии",
        description = "Ищет учеников, в имени или фамилии которых содержится указанная строка (без учёта регистра). " +
                      "Например, `?q=Ива` найдёт всех Иванов, Иванченко и т.д."
    )
    @ApiResponse(responseCode = "200", description = "Список совпадающих учеников")
    @GetMapping("/search")
    public List<StudentDto> search(
            @Parameter(description = "Строка для поиска", example = "Иванов") @RequestParam String q) {
        return studentService.search(q);
    }

    @Operation(
        summary = "Получить средние оценки ученика",
        description = "Возвращает средний балл ученика по каждому предмету, по которому у него есть оценки."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Средние оценки по предметам"),
        @ApiResponse(responseCode = "404", description = "Ученик не найден")
    })
    @GetMapping("/{id}/average")
    public StudentAverageDto getAverage(
            @Parameter(description = "ID ученика", example = "1") @PathVariable Long id) {
        return studentService.getAverage(id);
    }

    @Operation(
        summary = "Создать ученика (только ADMIN)",
        description = "Добавляет нового ученика и прикрепляет его к классу. Доступно только администратору."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ученик создан"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные"),
        @ApiResponse(responseCode = "403", description = "Требуется роль ROLE_ADMIN")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<StudentDto> create(@Valid @RequestBody StudentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(dto));
    }

    @Operation(
        summary = "Обновить данные ученика (только ADMIN)",
        description = "Изменяет ФИО или класс ученика. Доступно только администратору."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Данные обновлены"),
        @ApiResponse(responseCode = "404", description = "Ученик не найден"),
        @ApiResponse(responseCode = "403", description = "Требуется роль ROLE_ADMIN")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public StudentDto update(
            @Parameter(description = "ID ученика") @PathVariable Long id,
            @Valid @RequestBody StudentDto dto) {
        return studentService.update(id, dto);
    }

    @Operation(
        summary = "Удалить ученика (только ADMIN)",
        description = "Удаляет ученика и все его оценки из системы. Доступно только администратору."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ученик удалён"),
        @ApiResponse(responseCode = "404", description = "Ученик не найден"),
        @ApiResponse(responseCode = "403", description = "Требуется роль ROLE_ADMIN")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID ученика") @PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
