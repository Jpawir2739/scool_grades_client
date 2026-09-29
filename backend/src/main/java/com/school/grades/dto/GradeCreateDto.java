package com.school.grades.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class GradeCreateDto {
    @NotNull private Long studentId;
    @NotNull private Long subjectId;
    @NotNull @Min(1) @Max(5) private Integer value;
    @NotNull private LocalDate gradeDate;
    private String comment;
}
