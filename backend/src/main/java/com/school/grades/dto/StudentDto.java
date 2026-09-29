package com.school.grades.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentDto {
    private Long id;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private LocalDate dateOfBirth;
    private Long schoolClassId;
    private String schoolClassName;
}
