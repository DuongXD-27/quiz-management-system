package com.se.quiz.quiz_management_system.entity;

import jakarta.persistence.*;

/**
 * StudentQuiz entity - Maps to student_quiz table
 * Junction table for many-to-many relationship between student and quiz
 */
@Entity
@Table(name = "student_quiz")
@IdClass(StudentQuizId.class)
public class StudentQuiz {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    
    public StudentQuiz() {
    }
    
    public StudentQuiz(Student student, Quiz quiz) {
        this.student = student;
        this.quiz = quiz;
    }
    
    // Getters and Setters
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public Quiz getQuiz() {
        return quiz;
    }
    
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
    
    // Helper methods to get IDs for composite key
    public Long getStudentId() {
        return student != null ? student.getStudentId() : null;
    }
    
    public Long getQuizId() {
        return quiz != null ? quiz.getQuizId() : null;
    }
    
    @Override
    public String toString() {
        return "StudentQuiz{" +
                "studentId=" + getStudentId() +
                ", quizId=" + getQuizId() +
                '}';
    }
}

