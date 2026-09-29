package com.school.grades.controller;

import com.school.grades.dto.GradeCreateDto;
import com.school.grades.dto.GradeDto;
import com.school.grades.dto.ImportResultDto;
import com.school.grades.service.GradeService;
import com.school.grades.service.ImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Tag(name = "Оценки", description = "Выставление, просмотр и импорт оценок учеников")
public class GradeController {

    private final GradeService gradeService;
    private final ImportService importService;

    @Operation(summary = "Получить все оценки", description = "Возвращает полный список оценок по всем ученикам и предметам.")
    @ApiResponse(responseCode = "200", description = "Список оценок")
    @GetMapping
    public List<GradeDto> findAll() {
        return gradeService.findAll();
    }

    @Operation(summary = "Получить оценку по ID", description = "Возвращает данные одной оценки по её идентификатору.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Оценка найдена"),
        @ApiResponse(responseCode = "404", description = "Оценка с таким ID не существует")
    })
    @GetMapping("/{id}")
    public GradeDto findById(
            @Parameter(description = "ID оценки", example = "1") @PathVariable Long id) {
        return gradeService.findById(id);
    }

    @Operation(
        summary = "Получить оценки ученика",
        description = "Возвращает все оценки указанного ученика по всем предметам."
    )
    @ApiResponse(responseCode = "200", description = "Список оценок ученика")
    @GetMapping("/student/{studentId}")
    public List<GradeDto> findByStudent(
            @Parameter(description = "ID ученика", example = "1") @PathVariable Long studentId) {
        return gradeService.findByStudent(studentId);
    }

    @Operation(
        summary = "Получить оценки по предмету",
        description = "Возвращает все оценки всех учеников по указанному предмету."
    )
    @ApiResponse(responseCode = "200", description = "Список оценок по предмету")
    @GetMapping("/subject/{subjectId}")
    public List<GradeDto> findBySubject(
            @Parameter(description = "ID предмета", example = "1") @PathVariable Long subjectId) {
        return gradeService.findBySubject(subjectId);
    }

    @Operation(
        summary = "Получить оценки класса",
        description = "Возвращает все оценки всех учеников указанного класса по всем предметам."
    )
    @ApiResponse(responseCode = "200", description = "Список оценок класса")
    @GetMapping("/class/{classId}")
    public List<GradeDto> findByClass(
            @Parameter(description = "ID класса", example = "1") @PathVariable Long classId) {
        return gradeService.findByClass(classId);
    }

    @Operation(
        summary = "Получить оценки за период",
        description = "Фильтрует оценки по диапазону дат. Параметры в формате `YYYY-MM-DD`. " +
                      "Например: `?from=2024-01-01&to=2024-06-30`."
    )
    @ApiResponse(responseCode = "200", description = "Список оценок за указанный период")
    @GetMapping(params = {"from", "to"})
    public List<GradeDto> findByDateRange(
            @Parameter(description = "Начало периода (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Конец периода (YYYY-MM-DD)", example = "2024-06-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return gradeService.findByDateRange(from, to);
    }

    @Operation(
        summary = "Получить оценки ученика по предмету",
        description = "Возвращает все оценки конкретного ученика по конкретному предмету. " +
                      "Например: `?studentId=1&subjectId=2`."
    )
    @ApiResponse(responseCode = "200", description = "Список оценок ученика по предмету")
    @GetMapping(params = {"studentId", "subjectId"})
    public List<GradeDto> findByStudentAndSubject(
            @Parameter(description = "ID ученика", example = "1") @RequestParam Long studentId,
            @Parameter(description = "ID предмета", example = "2") @RequestParam Long subjectId) {
        return gradeService.findByStudentAndSubject(studentId, subjectId);
    }

    @Operation(
        summary = "Выставить оценку",
        description = "Создаёт новую оценку для ученика по предмету. " +
                      "Значение оценки от 1 до 5. Дата выставления — в формате `YYYY-MM-DD`."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Оценка выставлена"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные"),
        @ApiResponse(responseCode = "404", description = "Ученик или предмет не найден")
    })
    @PostMapping
    public ResponseEntity<GradeDto> create(@Valid @RequestBody GradeCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeService.create(dto));
    }

    @Operation(
        summary = "Исправить оценку",
        description = "Обновляет данные существующей оценки (значение, дату, комментарий)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Оценка обновлена"),
        @ApiResponse(responseCode = "404", description = "Оценка не найдена")
    })
    @PutMapping("/{id}")
    public GradeDto update(
            @Parameter(description = "ID оценки") @PathVariable Long id,
            @Valid @RequestBody GradeCreateDto dto) {
        return gradeService.update(id, dto);
    }

    @Operation(
        summary = "Удалить оценку",
        description = "Удаляет оценку из системы по её ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Оценка удалена"),
        @ApiResponse(responseCode = "404", description = "Оценка не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID оценки") @PathVariable Long id) {
        gradeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Импортировать оценки из CSV или Excel",
        description = "Загружает файл `.csv` или `.xlsx` с оценками и массово добавляет их в систему.\n\n" +
                      "**Формат CSV** (первая строка — заголовок):\n" +
                      "```\nstudent_id,subject_id,value,grade_date,comment\n1,2,5,2024-01-15,Отлично\n```\n\n" +
                      "**Формат Excel** `.xlsx`: первый лист, первая строка — заголовок, те же 5 колонок.\n\n" +
                      "Ответ содержит количество импортированных и пропущенных записей, а также список ошибок."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Все записи импортированы без ошибок"),
        @ApiResponse(responseCode = "207", description = "Часть записей импортирована, часть пропущена из-за ошибок — см. поле `errors`"),
        @ApiResponse(responseCode = "400", description = "Файл не передан или имеет неподдерживаемый формат")
    })
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResultDto> importGrades(
            @Parameter(description = "Файл CSV или XLSX с оценками", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        ImportResultDto result = importService.importGrades(file);
        HttpStatus status = result.getErrors().isEmpty() ? HttpStatus.OK : HttpStatus.MULTI_STATUS;
        return ResponseEntity.status(status).body(result);
    }
}
