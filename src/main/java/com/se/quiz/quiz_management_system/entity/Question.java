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
    
    @Column(name = "option_a")
    private String optionA;
    
    @Column(name = "option_b")
    private String optionB;
    
    @Column(name = "option_c")
    private String optionC;
    
    @Column(name = "option_d")
    private String optionD;
    
    @Column(name = "correct_answer")
    private String correctAnswer;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> quizQuestions;
    
    public Question() {
    }
    
    public Question(String problem, String solution) {
        this.problem = problem;
        this.solution = solution;
    }
    
    public Question(String problem, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        this.problem = problem;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.solution = correctAnswer; // For backward compatibility
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
    
    public String getOptionA() {
        return optionA;
    }
    
    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }
    
    public String getOptionB() {
        return optionB;
    }
    
    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }
    
    public String getOptionC() {
        return optionC;
    }
    
    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }
    
    public String getOptionD() {
        return optionD;
    }
    
    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
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

