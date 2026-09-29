package com.school.grades.controller;

import com.school.grades.dto.SubjectDto;
import com.school.grades.service.SubjectService;
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
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Tag(name = "Предметы", description = "Управление учебными предметами (математика, физика и т.д.)")
public class SubjectController {

    private final SubjectService subjectService;

    @Operation(summary = "Получить все предметы", description = "Возвращает список всех учебных предметов в системе.")
    @ApiResponse(responseCode = "200", description = "Список предметов")
    @GetMapping
    public List<SubjectDto> findAll() {
        return subjectService.findAll();
    }

    @Operation(summary = "Получить предмет по ID", description = "Возвращает данные одного предмета по его идентификатору.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Предмет найден"),
        @ApiResponse(responseCode = "404", description = "Предмет с таким ID не существует")
    })
    @GetMapping("/{id}")
    public SubjectDto findById(
            @Parameter(description = "ID предмета", example = "1") @PathVariable Long id) {
        return subjectService.findById(id);
    }

    @Operation(
        summary = "Создать предмет (только ADMIN)",
        description = "Добавляет новый учебный предмет. Доступно только администратору."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Предмет создан"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные"),
        @ApiResponse(responseCode = "403", description = "Требуется роль ROLE_ADMIN")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SubjectDto> create(@Valid @RequestBody SubjectDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(dto));
    }

    @Operation(
        summary = "Обновить предмет (только ADMIN)",
        description = "Изменяет название или описание предмета. Доступно только администратору."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Предмет обновлён"),
        @ApiResponse(responseCode = "404", description = "Предмет не найден"),
        @ApiResponse(responseCode = "403", description = "Требуется роль ROLE_ADMIN")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SubjectDto update(
            @Parameter(description = "ID предмета") @PathVariable Long id,
            @Valid @RequestBody SubjectDto dto) {
        return subjectService.update(id, dto);
    }

    @Operation(
        summary = "Удалить предмет (только ADMIN)",
        description = "Удаляет учебный предмет из системы. Доступно только администратору."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Предмет удалён"),
        @ApiResponse(responseCode = "404", description = "Предмет не найден"),
        @ApiResponse(responseCode = "403", description = "Требуется роль ROLE_ADMIN")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID предмета") @PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
