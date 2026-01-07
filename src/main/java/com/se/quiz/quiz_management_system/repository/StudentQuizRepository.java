package com.se.quiz.quiz_management_system.repository;

import com.se.quiz.quiz_management_system.entity.StudentQuiz;
import com.se.quiz.quiz_management_system.entity.StudentQuizId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * StudentQuizRepository - JPA repository for StudentQuiz junction table
 * Manages the many-to-many relationship between students and quizzes
 */
@Repository
public interface StudentQuizRepository extends JpaRepository<StudentQuiz, StudentQuizId> {
    
    /**
     * Find all student-quiz relationships for a specific student
     * @param studentId the student ID
     * @return list of StudentQuiz records
     */
    List<StudentQuiz> findByStudentId(Long studentId);
    
    /**
     * Find all student-quiz relationships for a specific quiz
     * @param quizId the quiz ID
     * @return list of StudentQuiz records
     */
    List<StudentQuiz> findByQuizId(Long quizId);
    
    /**
     * Check if a student is already assigned to a quiz
     * @param studentId the student ID
     * @param quizId the quiz ID
     * @return true if assignment exists, false otherwise
     */
    boolean existsByStudentIdAndQuizId(Long studentId, Long quizId);
    
    /**
     * Delete assignment for a specific student and quiz
     * @param studentId the student ID
     * @param quizId the quiz ID
     */
    void deleteByStudentIdAndQuizId(Long studentId, Long quizId);
    
    /**
     * Delete all assignments for a specific quiz
     * @param quizId the quiz ID
     */
    void deleteByQuizId(Long quizId);
}

