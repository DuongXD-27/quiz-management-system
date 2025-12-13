package com.se.quiz.quiz_management_system.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key for StudentQuiz entity
 */
public class StudentQuizId implements Serializable {
    
    private Long studentId;
    private Long quizId;
    
    public StudentQuizId() {
    }
    
    public StudentQuizId(Long studentId, Long quizId) {
        this.studentId = studentId;
        this.quizId = quizId;
    }
    
    // Getters and Setters
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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
        StudentQuizId that = (StudentQuizId) o;
        return Objects.equals(studentId, that.studentId) &&
               Objects.equals(quizId, that.quizId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(studentId, quizId);
    }
}

