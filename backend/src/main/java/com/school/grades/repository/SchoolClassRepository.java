package com.school.grades.repository;

import com.school.grades.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findByAcademicYear(Integer year);
    Optional<SchoolClass> findByNameAndAcademicYear(String name, Integer year);
}
