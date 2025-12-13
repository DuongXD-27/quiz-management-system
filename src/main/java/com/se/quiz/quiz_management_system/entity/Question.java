package com.se.quiz.quiz_management_system.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Question entity - Maps to question table
 */
@Entity
@Table(name = "question")
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;
    
    @Column(name = "problem", nullable = false, columnDefinition = "TEXT")
    private String problem;
    
    @Column(name = "solution", nullable = false, columnDefinition = "TEXT")
    private String solution;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> quizQuestions;
    
    public Question() {
    }
    
    public Question(String problem, String solution) {
        this.problem = problem;
        this.solution = solution;
    }
    
    // Getters and Setters
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public String getProblem() {
        return problem;
    }
    
    public void setProblem(String problem) {
        this.problem = problem;
    }
    
    public String getSolution() {
        return solution;
    }
    
    public void setSolution(String solution) {
        this.solution = solution;
    }
    
    public List<QuizQuestion> getQuizQuestions() {
        return quizQuestions;
    }
    
    public void setQuizQuestions(List<QuizQuestion> quizQuestions) {
        this.quizQuestions = quizQuestions;
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", problem='" + problem + '\'' +
                ", solution='" + solution + '\'' +
                '}';
    }
}

