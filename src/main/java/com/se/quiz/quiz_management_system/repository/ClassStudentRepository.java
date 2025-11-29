package com.se.quiz.quiz_management_system.repository;

import com.se.quiz.quiz_management_system.entity.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ClassStudent entity
 */
@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    
    /**
     * Find all students in a specific class
     * @param classId the class ID
     * @return List of ClassStudent records
     */
    List<ClassStudent> findByClassId(Long classId);
    
    /**
     * Check if a student is already enrolled in a class
     * @param classId the class ID
     * @param studentId the student ID
     * @return true if the student is already in the class
     */
    boolean existsByClassIdAndStudentId(Long classId, Long studentId);
    
    /**
     * Delete all students from a specific class
     * @param classId the class ID
     */
    void deleteByClassId(Long classId);
}

