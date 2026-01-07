package com.se.quiz.quiz_management_system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * StudentQuizResult entity - Stores quiz completion results
 * Maps to student_quiz_result table
 * 
 * This entity stores the actual quiz results including:
 * - Score achieved
 * - Completion time
 * - Submission timestamp
 */
@Entity
@Table(name = "student_quiz_result",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "quiz_id"}))
public class StudentQuizResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "quiz_id", nullable = false)
    private Long quizId;
    
    /**
     * Score achieved (e.g., 80 out of 100)
     */
    @Column(name = "score", nullable = false)
    private Integer score;
    
    /**
     * Total possible points (e.g., 100)
     */
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;
    
    /**
     * Time taken to complete quiz in seconds
     */
    @Column(name = "completion_time_seconds")
    private Integer completionTimeSeconds;
    
    /**
     * Timestamp when the quiz was submitted
     */
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;
    
    /**
     * Number of correct answers
     */
    @Column(name = "correct_answers")
    private Integer correctAnswers;
    
    /**
     * Total number of questions
     */
    @Column(name = "total_questions")
    private Integer totalQuestions;
    
    // Relationships - CRITICAL: Proper mapping for Hibernate to fetch related entities
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false, insertable = false, updatable = false)
    private Student student;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz_id", nullable = false, insertable = false, updatable = false)
    private Quiz quiz;
    
    // Constructors
    
    public StudentQuizResult() {
    }
    
    public StudentQuizResult(Long studentId, Long quizId, Integer score, Integer totalPoints) {
        this.studentId = studentId;
        this.quizId = quizId;
        this.score = score;
        this.totalPoints = totalPoints;
        this.submittedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public Long getResultId() {
        return resultId;
    }
    
    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }
    
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
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public Integer getTotalPoints() {
        return totalPoints;
    }
    
    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }
    
    public Integer getCompletionTimeSeconds() {
        return completionTimeSeconds;
    }
    
    public void setCompletionTimeSeconds(Integer completionTimeSeconds) {
        this.completionTimeSeconds = completionTimeSeconds;
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public Integer getCorrectAnswers() {
        return correctAnswers;
    }
    
    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
    
    public Integer getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
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
    
    /**
     * Calculate score percentage
     * @return score as percentage (0-100)
     */
    public double getScorePercentage() {
        if (totalPoints == null || totalPoints == 0) {
            return 0.0;
        }
        return (score * 100.0) / totalPoints;
    }
    
    /**
     * Get formatted completion time (e.g., "5m 30s")
     * @return formatted time string
     */
    public String getFormattedCompletionTime() {
        if (completionTimeSeconds == null) {
            return "N/A";
        }
        int minutes = completionTimeSeconds / 60;
        int seconds = completionTimeSeconds % 60;
        return minutes + "m " + seconds + "s";
    }
    
    @Override
    public String toString() {
        return "StudentQuizResult{" +
                "resultId=" + resultId +
                ", studentId=" + studentId +
                ", quizId=" + quizId +
                ", score=" + score +
                ", totalPoints=" + totalPoints +
                ", correctAnswers=" + correctAnswers +
                ", totalQuestions=" + totalQuestions +
                ", submittedAt=" + submittedAt +
                '}';
    }
}

