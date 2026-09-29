package com.school.grades.service;

import com.school.grades.dto.SubjectDto;
import com.school.grades.entity.Subject;
import com.school.grades.exception.ResourceNotFoundException;
import com.school.grades.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public List<SubjectDto> findAll() {
        return subjectRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SubjectDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional
    public SubjectDto create(SubjectDto dto) {
        if (subjectRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Subject already exists: " + dto.getName());
        }
        Subject entity = new Subject();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return toDto(subjectRepository.save(entity));
    }

    @Transactional
    public SubjectDto update(Long id, SubjectDto dto) {
        Subject entity = getOrThrow(id);
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return toDto(subjectRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        subjectRepository.delete(getOrThrow(id));
    }

    private Subject getOrThrow(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
    }

    public Subject getEntityOrThrow(Long id) {
        return getOrThrow(id);
    }

    private SubjectDto toDto(Subject s) {
        SubjectDto dto = new SubjectDto();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        return dto;
    }
}
