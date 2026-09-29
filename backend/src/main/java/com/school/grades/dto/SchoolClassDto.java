package com.school.grades.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SchoolClassDto {
    private Long id;
    @NotBlank private String name;
    @NotNull private Integer academicYear;
    private int studentCount;
}
