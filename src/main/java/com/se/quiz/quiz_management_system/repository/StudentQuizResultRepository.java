package com.se.quiz.quiz_management_system.repository;

import com.se.quiz.quiz_management_system.entity.StudentQuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for StudentQuizResult entity
 * Handles CRUD operations and custom queries for quiz results
 */
@Repository
public interface StudentQuizResultRepository extends JpaRepository<StudentQuizResult, Long> {
    
    /**
     * Check if a student has completed a specific quiz
     * @param studentId the student ID
     * @param quizId the quiz ID
     * @return true if result exists, false otherwise
     */
    boolean existsByStudentIdAndQuizId(Long studentId, Long quizId);
    
    /**
     * Find result for a specific student and quiz
     * @param studentId the student ID
     * @param quizId the quiz ID
     * @return Optional containing the result if found
     */
    Optional<StudentQuizResult> findByStudentIdAndQuizId(Long studentId, Long quizId);
    
    /**
     * Get all results for a specific quiz
     * @param quizId the quiz ID
     * @return list of results for the quiz
     */
    List<StudentQuizResult> findByQuizId(Long quizId);
    
    /**
     * Get all results for a specific student
     * @param studentId the student ID
     * @return list of results for the student
     */
    List<StudentQuizResult> findByStudentId(Long studentId);
    
    /**
     * Get all results for a specific student ordered by submission time (newest first)
     * @param studentId the student ID
     * @return list of results ordered by submission time
     */
    List<StudentQuizResult> findByStudentIdOrderBySubmittedAtDesc(Long studentId);
    
    /**
     * Get all results for a specific quiz ordered by score descending
     * @param quizId the quiz ID
     * @return list of results ordered by score (highest first)
     */
    List<StudentQuizResult> findByQuizIdOrderByScoreDesc(Long quizId);
    
    /**
     * Get all results for a specific quiz ordered by submission time
     * @param quizId the quiz ID
     * @return list of results ordered by submission time (newest first)
     */
    List<StudentQuizResult> findByQuizIdOrderBySubmittedAtDesc(Long quizId);
    
    /**
     * Count number of students who completed a specific quiz
     * @param quizId the quiz ID
     * @return number of students who completed the quiz
     */
    long countByQuizId(Long quizId);
    
    /**
     * Get average score for a specific quiz
     * @param quizId the quiz ID
     * @return average score (null if no results)
     */
    @Query("SELECT AVG(r.score) FROM StudentQuizResult r WHERE r.quizId = :quizId")
    Double getAverageScoreByQuizId(@Param("quizId") Long quizId);
    
    /**
     * Get highest score for a specific quiz
     * @param quizId the quiz ID
     * @return highest score (null if no results)
     */
    @Query("SELECT MAX(r.score) FROM StudentQuizResult r WHERE r.quizId = :quizId")
    Integer getHighestScoreByQuizId(@Param("quizId") Long quizId);
    
    /**
     * Get lowest score for a specific quiz
     * @param quizId the quiz ID
     * @return lowest score (null if no results)
     */
    @Query("SELECT MIN(r.score) FROM StudentQuizResult r WHERE r.quizId = :quizId")
    Integer getLowestScoreByQuizId(@Param("quizId") Long quizId);
    
    /**
     * Delete all results for a specific quiz
     * @param quizId the quiz ID
     */
    void deleteByQuizId(Long quizId);
    
    /**
     * Delete all results for a specific student
     * @param studentId the student ID
     */
    void deleteByStudentId(Long studentId);
}

