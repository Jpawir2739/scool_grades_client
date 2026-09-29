package com.school.grades.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubjectDto {
    private Long id;
    @NotBlank private String name;
    private String description;
}
