package com.school.grades.dto;

import com.school.grades.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String username;
    @NotBlank @Size(min = 6) private String password;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private Role role = Role.ROLE_TEACHER;
}
