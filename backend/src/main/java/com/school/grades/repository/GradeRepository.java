package com.school.grades.repository;

import com.school.grades.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudentId(Long studentId);

    List<Grade> findBySubjectId(Long subjectId);

    List<Grade> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    @Query("SELECT g FROM Grade g " +
           "JOIN FETCH g.student s " +
           "JOIN FETCH s.schoolClass sc " +
           "WHERE sc.id = :classId")
    List<Grade> findBySchoolClassId(@Param("classId") Long classId);

    @Query("SELECT g FROM Grade g WHERE g.gradeDate BETWEEN :from AND :to")
    List<Grade> findByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT AVG(g.value) FROM Grade g WHERE g.student.id = :studentId")
    Double findAverageByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT AVG(g.value) FROM Grade g " +
           "WHERE g.student.id = :studentId AND g.subject.id = :subjectId")
    Double findAverageByStudentAndSubject(
            @Param("studentId") Long studentId,
            @Param("subjectId") Long subjectId);

    @Query("SELECT g.subject.name, AVG(g.value) FROM Grade g " +
           "WHERE g.student.id = :studentId GROUP BY g.subject.id, g.subject.name")
    List<Object[]> findAveragePerSubjectByStudent(@Param("studentId") Long studentId);
}
