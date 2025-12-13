package com.se.quiz.quiz_management_system.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Quiz entity - Maps to quiz table
 */
@Entity
@Table(name = "quiz")
public class Quiz {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long quizId;
    
    @Column(name = "number_of_question")
    private Integer numberOfQuestion;
    
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> quizQuestions;
    
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentQuiz> studentQuizzes;
    
    public Quiz() {
    }
    
    public Quiz(Integer numberOfQuestion) {
        this.numberOfQuestion = numberOfQuestion;
    }
    
    // Getters and Setters
    
    public Long getQuizId() {
        return quizId;
    }
    
    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
    
    public Integer getNumberOfQuestion() {
        return numberOfQuestion;
    }
    
    public void setNumberOfQuestion(Integer numberOfQuestion) {
        this.numberOfQuestion = numberOfQuestion;
    }
    
    public List<QuizQuestion> getQuizQuestions() {
        return quizQuestions;
    }
    
    public void setQuizQuestions(List<QuizQuestion> quizQuestions) {
        this.quizQuestions = quizQuestions;
    }
    
    public List<StudentQuiz> getStudentQuizzes() {
        return studentQuizzes;
    }
    
    public void setStudentQuizzes(List<StudentQuiz> studentQuizzes) {
        this.studentQuizzes = studentQuizzes;
    }
    
    @Override
    public String toString() {
        return "Quiz{" +
                "quizId=" + quizId +
                ", numberOfQuestion=" + numberOfQuestion +
                '}';
    }
}

