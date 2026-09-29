package com.school.grades.repository;

import com.school.grades.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findBySchoolClassId(Long classId);

    List<Student> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
            String lastName, String firstName);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.schoolClass WHERE s.id = :id")
    java.util.Optional<Student> findByIdWithClass(@Param("id") Long id);
}
