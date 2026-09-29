package com.school.grades.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GradeDto {
    private Long id;
    private Long studentId;
    private String studentFullName;
    private Long subjectId;
    private String subjectName;
    private Long teacherId;
    private String teacherFullName;
    private Integer value;
    private LocalDate gradeDate;
    private String comment;
}
