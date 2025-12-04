package com.se.quiz.quiz_management_system.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "problem", columnDefinition = "TEXT")
    private String problem;

    @Column(name = "solution", columnDefinition = "TEXT")
    private String solution;

    @ManyToMany(mappedBy = "questions")
    private List<Quiz> quizzes;

    public Question() {}

    public Question(String problem, String solution, List<Quiz> quizzes) {
        this.problem = problem;
        this.solution = solution;
        this.quizzes = quizzes;
    }

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

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }
}