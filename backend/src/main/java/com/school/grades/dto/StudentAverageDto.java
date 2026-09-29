package com.school.grades.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class StudentAverageDto {
    private Long studentId;
    private String studentFullName;
    private Double overallAverage;
    private Map<String, Double> averagePerSubject;
}
