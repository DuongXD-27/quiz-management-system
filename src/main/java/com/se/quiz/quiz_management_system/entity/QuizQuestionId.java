package com.se.quiz.quiz_management_system.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key for QuizQuestion entity
 */
public class QuizQuestionId implements Serializable {
    
    private Long questionId;
    private Long quizId;
    
    public QuizQuestionId() {
    }
    
    public QuizQuestionId(Long questionId, Long quizId) {
        this.questionId = questionId;
        this.quizId = quizId;
    }
    
    // Getters and Setters
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public Long getQuizId() {
        return quizId;
    }
    
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuizQuestionId that = (QuizQuestionId) o;
        return Objects.equals(questionId, that.questionId) &&
               Objects.equals(quizId, that.quizId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(questionId, quizId);
    }
}

