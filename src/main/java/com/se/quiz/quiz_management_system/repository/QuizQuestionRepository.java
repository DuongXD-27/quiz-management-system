package com.se.quiz.quiz_management_system.repository;

import com.se.quiz.quiz_management_system.entity.QuizQuestion;
import com.se.quiz.quiz_management_system.entity.QuizQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * QuizQuestionRepository - JPA repository for QuizQuestion junction table
 */
@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, QuizQuestionId> {
    
    /**
     * Find all quiz-question relationships for a specific quiz
     * @param quizId the quiz ID
     * @return list of QuizQuestion records
     */
    List<QuizQuestion> findByQuizId(Long quizId);
    
    /**
     * Delete all quiz-question relationships for a specific quiz
     * @param quizId the quiz ID
     */
    void deleteByQuizId(Long quizId);
}

