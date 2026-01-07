package com.se.quiz.quiz_management_system.repository;

import com.se.quiz.quiz_management_system.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

    // QuestionRepository - JPA repository for Question entity
    
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // Find questions by problem content (partial match)
    // @param problem the problem text to search for
    // @return list of matching questions
    
    List<Question> findByProblemContainingIgnoreCase(String problem);
}

