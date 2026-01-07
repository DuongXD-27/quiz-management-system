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

    // Composite primary key fields matching QuizQuestionId
    @Id
    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Id
    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    // Relationships to Question and Quiz, mapped via the FK columns above
    @ManyToOne
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    private Quiz quiz;

    public QuizQuestion() {
    }

    public QuizQuestion(Question question, Quiz quiz) {
        this.question = question;
        this.quiz = quiz;
        this.questionId = question != null ? question.getQuestionId() : null;
        this.quizId = quiz != null ? quiz.getQuizId() : null;
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
        this.questionId = question != null ? question.getQuestionId() : null;
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
        return "QuizQuestion{" +
                "questionId=" + questionId +
                ", quizId=" + quizId +
                '}';
    }
}

