package com.school.grades.controller;

import com.school.grades.dto.SchoolClassDto;
import com.school.grades.service.SchoolClassService;
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
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "Классы", description = "Управление школьными классами (5А, 10Б и т.д.)")
public class SchoolClassController {

    private final SchoolClassService classService;

    @Operation(summary = "Получить все классы", description = "Возвращает список всех школьных классов в системе.")
    @ApiResponse(responseCode = "200", description = "Список классов (может быть пустым)")
    @GetMapping
    public List<SchoolClassDto> findAll() {
        return classService.findAll();
    }

    @Operation(summary = "Получить класс по ID", description = "Возвращает данные одного класса по его идентификатору.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Класс найден"),
        @ApiResponse(responseCode = "404", description = "Класс с таким ID не существует")
    })
    @GetMapping("/{id}")
    public SchoolClassDto findById(
            @Parameter(description = "ID класса", example = "1") @PathVariable Long id) {
        return classService.findById(id);
    }

    @Operation(
        summary = "Получить классы за учебный год",
        description = "Фильтрует классы по году начала обучения. Например, `?year=2024` вернёт все классы набора 2024 года."
    )
    @ApiResponse(responseCode = "200", description = "Список классов за указанный год")
    @GetMapping(params = "year")
    public List<SchoolClassDto> findByYear(
            @Parameter(description = "Год начала обучения", example = "2024") @RequestParam Integer year) {
        return classService.findByYear(year);
    }

    @Operation(
        summary = "Создать новый класс (только ADMIN)",
        description = "Добавляет школьный класс в систему. Доступно только администратору."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Класс успешно создан"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные"),
        @ApiResponse(responseCode = "403", description = "Требуется роль ROLE_ADMIN")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SchoolClassDto> create(@Valid @RequestBody SchoolClassDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classService.create(dto));
    }

    @Operation(
        summary = "Обновить класс (только ADMIN)",
        description = "Изменяет данные существующего класса по его ID. Доступно только администратору."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Класс обновлён"),
        @ApiResponse(responseCode = "404", description = "Класс не найден"),
        @ApiResponse(responseCode = "403", description = "Требуется роль ROLE_ADMIN")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SchoolClassDto update(
            @Parameter(description = "ID класса") @PathVariable Long id,
            @Valid @RequestBody SchoolClassDto dto) {
        return classService.update(id, dto);
    }

    @Operation(
        summary = "Удалить класс (только ADMIN)",
        description = "Удаляет класс из системы по его ID. Доступно только администратору."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Класс удалён"),
        @ApiResponse(responseCode = "404", description = "Класс не найден"),
        @ApiResponse(responseCode = "403", description = "Требуется роль ROLE_ADMIN")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID класса") @PathVariable Long id) {
        classService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
