package com.school.grades.service;

import com.school.grades.dto.SchoolClassDto;
import com.school.grades.entity.SchoolClass;
import com.school.grades.exception.ResourceNotFoundException;
import com.school.grades.repository.SchoolClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolClassService {

    private final SchoolClassRepository classRepository;

    @Transactional(readOnly = true)
    public List<SchoolClassDto> findAll() {
        return classRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SchoolClassDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<SchoolClassDto> findByYear(Integer year) {
        return classRepository.findByAcademicYear(year).stream().map(this::toDto).toList();
    }

    @Transactional
    public SchoolClassDto create(SchoolClassDto dto) {
        classRepository.findByNameAndAcademicYear(dto.getName(), dto.getAcademicYear())
                .ifPresent(c -> { throw new IllegalArgumentException(
                        "Class " + dto.getName() + " already exists for year " + dto.getAcademicYear()); });
        SchoolClass entity = new SchoolClass();
        entity.setName(dto.getName());
        entity.setAcademicYear(dto.getAcademicYear());
        return toDto(classRepository.save(entity));
    }

    @Transactional
    public SchoolClassDto update(Long id, SchoolClassDto dto) {
        SchoolClass entity = getOrThrow(id);
        entity.setName(dto.getName());
        entity.setAcademicYear(dto.getAcademicYear());
        return toDto(classRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        classRepository.delete(getOrThrow(id));
    }

    private SchoolClass getOrThrow(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SchoolClass", id));
    }

    private SchoolClassDto toDto(SchoolClass c) {
        SchoolClassDto dto = new SchoolClassDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setAcademicYear(c.getAcademicYear());
        dto.setStudentCount(c.getStudents().size());
        return dto;
    }
}
