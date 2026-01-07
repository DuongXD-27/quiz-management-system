package com.se.quiz.quiz_management_system.repository;

import com.se.quiz.quiz_management_system.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * QuizRepository - JPA repository for Quiz entity
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    /**
     * Find quizzes by name (case-insensitive partial match)
     * @param quizName the quiz name to search for
     * @return list of matching quizzes
     */
    List<Quiz> findByQuizNameContainingIgnoreCase(String quizName);
    
    /**
     * Find all quizzes ordered by creation date (most recent first)
     * @return list of all quizzes
     */
    List<Quiz> findAllByOrderByQuizIdDesc();
}

