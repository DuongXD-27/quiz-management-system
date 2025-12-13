package com.se.quiz.quiz_management_system.entity;

import jakarta.persistence.*;

/**
 * QuizQuestion entity - Maps to quiz_question table
 * Junction table for many-to-many relationship between quiz and question
 */
@Entity
@Table(name = "quiz_question")
@IdClass(QuizQuestionId.class)
public class QuizQuestion {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    
    public QuizQuestion() {
    }
    
    public QuizQuestion(Question question, Quiz quiz) {
        this.question = question;
        this.quiz = quiz;
    }
    
    // Getters and Setters
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public Quiz getQuiz() {
        return quiz;
    }
    
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
    
    // Helper methods to get IDs for composite key
    public Long getQuestionId() {
        return question != null ? question.getQuestionId() : null;
    }
    
    public Long getQuizId() {
        return quiz != null ? quiz.getQuizId() : null;
    }
    
    @Override
    public String toString() {
        return "QuizQuestion{" +
                "questionId=" + getQuestionId() +
                ", quizId=" + getQuizId() +
                '}';
    }
}

