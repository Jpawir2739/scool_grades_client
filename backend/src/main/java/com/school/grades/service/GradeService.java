package com.school.grades.service;

import com.school.grades.dto.GradeCreateDto;
import com.school.grades.dto.GradeDto;
import com.school.grades.entity.*;
import com.school.grades.exception.ResourceNotFoundException;
import com.school.grades.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<GradeDto> findAll() {
        return gradeRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public GradeDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<GradeDto> findByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<GradeDto> findBySubject(Long subjectId) {
        return gradeRepository.findBySubjectId(subjectId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<GradeDto> findByClass(Long classId) {
        return gradeRepository.findBySchoolClassId(classId).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<GradeDto> findByDateRange(LocalDate from, LocalDate to) {
        return gradeRepository.findByDateRange(from, to).stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<GradeDto> findByStudentAndSubject(Long studentId, Long subjectId) {
        return gradeRepository.findByStudentIdAndSubjectId(studentId, subjectId)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public GradeDto create(GradeCreateDto dto) {
        Grade grade = new Grade();
        grade.setStudent(studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", dto.getStudentId())));
        grade.setSubject(subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", dto.getSubjectId())));
        grade.setTeacher(getCurrentUser());
        grade.setValue(dto.getValue());
        grade.setGradeDate(dto.getGradeDate());
        grade.setComment(dto.getComment());
        return toDto(gradeRepository.save(grade));
    }

    @Transactional
    public GradeDto update(Long id, GradeCreateDto dto) {
        Grade grade = getOrThrow(id);
        grade.setStudent(studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", dto.getStudentId())));
        grade.setSubject(subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", dto.getSubjectId())));
        grade.setValue(dto.getValue());
        grade.setGradeDate(dto.getGradeDate());
        grade.setComment(dto.getComment());
        return toDto(gradeRepository.save(grade));
    }

    @Transactional
    public void delete(Long id) {
        gradeRepository.delete(getOrThrow(id));
    }

    private Grade getOrThrow(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", id));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found in DB: " + username));
    }

    public GradeDto toDto(Grade g) {
        GradeDto dto = new GradeDto();
        dto.setId(g.getId());
        dto.setStudentId(g.getStudent().getId());
        dto.setStudentFullName(g.getStudent().getFirstName() + " " + g.getStudent().getLastName());
        dto.setSubjectId(g.getSubject().getId());
        dto.setSubjectName(g.getSubject().getName());
        dto.setTeacherId(g.getTeacher().getId());
        dto.setTeacherFullName(g.getTeacher().getFirstName() + " " + g.getTeacher().getLastName());
        dto.setValue(g.getValue());
        dto.setGradeDate(g.getGradeDate());
        dto.setComment(g.getComment());
        return dto;
    }
}
