package com.school.grades.service;

import com.school.grades.dto.StudentAverageDto;
import com.school.grades.dto.StudentDto;
import com.school.grades.entity.SchoolClass;
import com.school.grades.entity.Student;
import com.school.grades.exception.ResourceNotFoundException;
import com.school.grades.repository.GradeRepository;
import com.school.grades.repository.SchoolClassRepository;
import com.school.grades.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository classRepository;
    private final GradeRepository gradeRepository;

    @Transactional(readOnly = true)
    public List<StudentDto> findAll() {
        return studentRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public StudentDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<StudentDto> findByClass(Long classId) {
        return studentRepository.findBySchoolClassId(classId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<StudentDto> search(String query) {
        return studentRepository
                .findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(query, query)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public StudentAverageDto getAverage(Long studentId) {
        Student student = getOrThrow(studentId);
        Double overall = gradeRepository.findAverageByStudentId(studentId);
        List<Object[]> perSubject = gradeRepository.findAveragePerSubjectByStudent(studentId);
        Map<String, Double> avgMap = new LinkedHashMap<>();
        perSubject.forEach(row -> avgMap.put((String) row[0], (Double) row[1]));
        return new StudentAverageDto(
                studentId,
                student.getFirstName() + " " + student.getLastName(),
                overall,
                avgMap
        );
    }

    @Transactional
    public StudentDto create(StudentDto dto) {
        Student entity = new Student();
        fillEntity(entity, dto);
        return toDto(studentRepository.save(entity));
    }

    @Transactional
    public StudentDto update(Long id, StudentDto dto) {
        Student entity = getOrThrow(id);
        fillEntity(entity, dto);
        return toDto(studentRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        studentRepository.delete(getOrThrow(id));
    }

    private void fillEntity(Student entity, StudentDto dto) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getSchoolClassId() != null) {
            SchoolClass sc = classRepository.findById(dto.getSchoolClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", dto.getSchoolClassId()));
            entity.setSchoolClass(sc);
        }
    }

    private Student getOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    public StudentDto toDto(Student s) {
        StudentDto dto = new StudentDto();
        dto.setId(s.getId());
        dto.setFirstName(s.getFirstName());
        dto.setLastName(s.getLastName());
        dto.setDateOfBirth(s.getDateOfBirth());
        if (s.getSchoolClass() != null) {
            dto.setSchoolClassId(s.getSchoolClass().getId());
            dto.setSchoolClassName(s.getSchoolClass().getName());
        }
        return dto;
    }
}
