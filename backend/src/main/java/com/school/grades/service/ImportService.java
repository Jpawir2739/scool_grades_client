package com.school.grades.service;

import com.opencsv.CSVReader;
import com.school.grades.dto.GradeCreateDto;
import com.school.grades.dto.ImportResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Импорт оценок из CSV или Excel.
 *
 * Формат файла (первая строка — заголовок, обязательна):
 *   student_id, subject_id, value, grade_date (yyyy-MM-dd), comment (необязательно)
 *
 * Пример CSV:
 *   student_id,subject_id,value,grade_date,comment
 *   1,2,5,2024-01-15,Отлично
 *   2,3,4,2024-01-15,
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final GradeService gradeService;

    public ImportResultDto importGrades(MultipartFile file) {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (filename.endsWith(".csv")) {
            return importCsv(file);
        } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            return importExcel(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type. Use .csv or .xlsx");
        }
    }

    // ─── CSV ────────────────────────────────────────────────────────────────

    private ImportResultDto importCsv(MultipartFile file) {
        ImportResultDto result = new ImportResultDto();
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            List<String[]> rows = reader.readAll();
            if (rows.isEmpty()) return result;

            // пропускаем заголовок
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                processRow(row, i + 1, result);
            }
        } catch (Exception e) {
            result.getErrors().add("Failed to read CSV: " + e.getMessage());
        }
        return result;
    }

    // ─── Excel ──────────────────────────────────────────────────────────────

    private ImportResultDto importExcel(MultipartFile file) {
        ImportResultDto result = new ImportResultDto();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean headerSkipped = false;
            for (Row row : sheet) {
                if (!headerSkipped) { headerSkipped = true; continue; }
                if (isRowEmpty(row)) continue;

                String[] values = new String[5];
                for (int c = 0; c < 5; c++) {
                    Cell cell = row.getCell(c);
                    values[c] = cell == null ? "" : getCellValue(cell);
                }
                processRow(values, row.getRowNum() + 1, result);
            }
        } catch (Exception e) {
            result.getErrors().add("Failed to read Excel: " + e.getMessage());
        }
        return result;
    }

    // ─── Общая логика строки ─────────────────────────────────────────────────

    private void processRow(String[] cols, int lineNum, ImportResultDto result) {
        try {
            if (cols.length < 4) {
                result.getErrors().add("Line " + lineNum + ": not enough columns (need 4+)");
                result.setSkipped(result.getSkipped() + 1);
                return;
            }

            long studentId = Long.parseLong(cols[0].trim());
            long subjectId = Long.parseLong(cols[1].trim());
            int value     = Integer.parseInt(cols[2].trim());
            LocalDate date = LocalDate.parse(cols[3].trim(), DATE_FMT);
            String comment = cols.length > 4 ? cols[4].trim() : null;

            GradeCreateDto dto = new GradeCreateDto();
            dto.setStudentId(studentId);
            dto.setSubjectId(subjectId);
            dto.setValue(value);
            dto.setGradeDate(date);
            dto.setComment(comment);

            gradeService.create(dto);
            result.setImported(result.getImported() + 1);

        } catch (Exception e) {
            result.getErrors().add("Line " + lineNum + ": " + e.getMessage());
            result.setSkipped(result.getSkipped() + 1);
        }
    }

    private String getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case NUMERIC -> {
                double v = cell.getNumericCellValue();
                // целое число — без десятичной части
                yield v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA  -> cell.getCellFormula();
            default       -> cell.getStringCellValue().trim();
        };
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}
