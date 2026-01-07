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
    
    // Composite primary key fields matching StudentQuizId
    @Id
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Id
    @Column(name = "quiz_id", nullable = false)
    private Long quizId;
    
    // Relationships to Student and Quiz, mapped via the FK columns above
    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    private Quiz quiz;
    
    public StudentQuiz() {
    }
    
    public StudentQuiz(Student student, Quiz quiz) {
        this.student = student;
        this.quiz = quiz;
        this.studentId = student != null ? student.getStudentId() : null;
        this.quizId = quiz != null ? quiz.getQuizId() : null;
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
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
        this.studentId = student != null ? student.getStudentId() : null;
    }
    
    public Quiz getQuiz() {
        return quiz;
    }
    
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        this.quizId = quiz != null ? quiz.getQuizId() : null;
    }
    
    @Override
    public String toString() {
        return "StudentQuiz{" +
                "studentId=" + studentId +
                ", quizId=" + quizId +
                '}';
    }
}

