package com.school.grades.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResultDto {
    private int imported;
    private int skipped;
    private List<String> errors = new ArrayList<>();
}
