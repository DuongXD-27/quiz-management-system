package com.se.quiz.quiz_management_system.service;

import com.se.quiz.quiz_management_system.entity.StudentQuizResult;
import com.se.quiz.quiz_management_system.repository.StudentQuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

    // ResultService - Business logic for quiz results
    // Handles checking completion status, saving results, and retrieving results
    
@Service
public class ResultService {
    
    @Autowired
    private StudentQuizResultRepository resultRepository;
    
    // Check if a student has already completed a specific quiz
    // CRITICAL: Used to prevent students from taking quiz multiple times
    // @param studentId the student ID
    // @param quizId the quiz ID
    // @return true if student has completed the quiz, false otherwise
    
    @Transactional(readOnly = true)
    public boolean hasStudentCompletedQuiz(Long studentId, Long quizId) {
        if (studentId == null || quizId == null) {
            return false;
        }
        return resultRepository.existsByStudentIdAndQuizId(studentId, quizId);
    }
    
    // Save quiz result after student completes quiz
    // CRITICAL: This is called when student submits quiz
    // @param result the quiz result to save
    // @return the saved result
    
    @Transactional
    public StudentQuizResult saveResult(StudentQuizResult result) {
        // DEBUG: Log entry to method
        System.out.println("🔵 [ResultService.saveResult] ENTRY - Attempting to save result");
        
        if (result == null) {
            System.err.println("❌ [ResultService.saveResult] Result object is NULL!");
            throw new IllegalArgumentException("Result cannot be null");
        }
        
        // DEBUG: Log result details
        System.out.println("🔵 [ResultService.saveResult] Result details:");
        System.out.println("   - Student ID: " + result.getStudentId());
        System.out.println("   - Quiz ID: " + result.getQuizId());
        System.out.println("   - Score: " + result.getScore() + "/" + result.getTotalPoints());
        System.out.println("   - Correct Answers: " + result.getCorrectAnswers() + "/" + result.getTotalQuestions());
        
        // Validate required fields
        if (result.getStudentId() == null || result.getQuizId() == null) {
            System.err.println("❌ [ResultService.saveResult] Missing required fields: studentId=" + 
                             result.getStudentId() + ", quizId=" + result.getQuizId());
            throw new IllegalArgumentException("Student ID and Quiz ID are required");
        }
        
        // Check if result already exists (prevent duplicate submissions)
        boolean exists = resultRepository.existsByStudentIdAndQuizId(result.getStudentId(), result.getQuizId());
        System.out.println("🔵 [ResultService.saveResult] Duplicate check: exists=" + exists);
        
        if (exists) {
            System.err.println("❌ [ResultService.saveResult] DUPLICATE - Student " + result.getStudentId() + 
                             " already completed Quiz " + result.getQuizId());
            throw new IllegalStateException("Student has already completed this quiz");
        }
        
        // Set submission timestamp if not set
        if (result.getSubmittedAt() == null) {
            result.setSubmittedAt(LocalDateTime.now());
            System.out.println("🔵 [ResultService.saveResult] Set timestamp: " + result.getSubmittedAt());
        }
        
        // CRITICAL: Save to database
        System.out.println("🔵 [ResultService.saveResult] Calling repository.save()...");
        StudentQuizResult savedResult = resultRepository.save(result);
        
        // DEBUG: Log success
        System.out.println("✅ [ResultService.saveResult] SUCCESS - Result saved with ID: " + savedResult.getResultId());
        System.out.println("   - Database record created for Student " + savedResult.getStudentId() + 
                         " on Quiz " + savedResult.getQuizId());
        
        return savedResult;
    }
    
    // Get result for a specific student and quiz
    // @param studentId the student ID
    // @param quizId the quiz ID
    // @return Optional containing the result if found
    
    @Transactional(readOnly = true)
    public Optional<StudentQuizResult> getResult(Long studentId, Long quizId) {
        if (studentId == null || quizId == null) {
            return Optional.empty();
        }
        return resultRepository.findByStudentIdAndQuizId(studentId, quizId);
    }
    
    // Get all results for a specific quiz
    // CRITICAL: Used by teacher to view all student results for a quiz
    // Returns results with Student and Quiz relationships loaded
    // @param quizId the quiz ID
    // @return list of results for the quiz (ordered by submission time, newest first)
    
    @Transactional(readOnly = true)
    public List<StudentQuizResult> getResultsByQuizId(Long quizId) {
        if (quizId == null) {
            throw new IllegalArgumentException("Quiz ID cannot be null");
        }
        
        System.out.println("🔵 [ResultService.getResultsByQuizId] Fetching results for quiz: " + quizId);
        List<StudentQuizResult> results = resultRepository.findByQuizIdOrderBySubmittedAtDesc(quizId);
        System.out.println("✅ [ResultService.getResultsByQuizId] Found " + results.size() + " results");
        
        // DEBUG: Log each result to verify relationships are loaded
        for (StudentQuizResult result : results) {
            System.out.println("   - Result ID: " + result.getResultId() + 
                             ", Student: " + (result.getStudent() != null ? result.getStudent().getFullName() : "NULL") +
                             ", Score: " + result.getScore() + "/" + result.getTotalPoints());
        }
        
        return results;
    }
    
    // Get all results for a specific student
    // Used by student to view their quiz history
    // CRITICAL: Returns results with Student and Quiz relationships loaded
    // @param studentId the student ID
    // @return list of results for the student (ordered by submission time, newest first)
    
    @Transactional(readOnly = true)
    public List<StudentQuizResult> getResultsByStudentId(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        
        System.out.println("🔵 [ResultService.getResultsByStudentId] Fetching results for student: " + studentId);
        List<StudentQuizResult> results = resultRepository.findByStudentIdOrderBySubmittedAtDesc(studentId);
        System.out.println("✅ [ResultService.getResultsByStudentId] Found " + results.size() + " results");
        
        // DEBUG: Log each result to verify relationships are loaded
        for (StudentQuizResult result : results) {
            System.out.println("   - Result ID: " + result.getResultId() + 
                             ", Quiz: " + (result.getQuiz() != null ? result.getQuiz().getQuizName() : "NULL") +
                             ", Score: " + result.getScore() + "/" + result.getTotalPoints());
        }
        
        return results;
    }
    
    // Get quiz statistics (average, highest, lowest scores)
    // @param quizId the quiz ID
    // @return QuizStatistics object containing stats
    
    @Transactional(readOnly = true)
    public QuizStatistics getQuizStatistics(Long quizId) {
        if (quizId == null) {
            throw new IllegalArgumentException("Quiz ID cannot be null");
        }
        
        long totalStudents = resultRepository.countByQuizId(quizId);
        Double averageScore = resultRepository.getAverageScoreByQuizId(quizId);
        Integer highestScore = resultRepository.getHighestScoreByQuizId(quizId);
        Integer lowestScore = resultRepository.getLowestScoreByQuizId(quizId);
        
        return new QuizStatistics(totalStudents, averageScore, highestScore, lowestScore);
    }
    
    // Delete a result (admin/teacher only)
    // @param resultId the result ID to delete
    // @return true if deleted, false if not found
    
    @Transactional
    public boolean deleteResult(Long resultId) {
        if (resultId == null) {
            return false;
        }
        
        if (resultRepository.existsById(resultId)) {
            resultRepository.deleteById(resultId);
            return true;
        }
        
        return false;
    }
    

    // Update result (for corrections/regrading)
    // @param resultId the result ID
    // @param newScore the new score
    // @return updated result
    
    @Transactional
    public Optional<StudentQuizResult> updateScore(Long resultId, Integer newScore) {
        if (resultId == null || newScore == null) {
            return Optional.empty();
        }
        
        Optional<StudentQuizResult> resultOpt = resultRepository.findById(resultId);
        if (resultOpt.isPresent()) {
            StudentQuizResult result = resultOpt.get();
            result.setScore(newScore);
            return Optional.of(resultRepository.save(result));
        }
        
        return Optional.empty();
    }
    
    // Inner class for quiz statistics
    
    public static class QuizStatistics {
        private final long totalStudents;
        private final Double averageScore;
        private final Integer highestScore;
        private final Integer lowestScore;
        
        public QuizStatistics(long totalStudents, Double averageScore, 
                            Integer highestScore, Integer lowestScore) {
            this.totalStudents = totalStudents;
            this.averageScore = averageScore;
            this.highestScore = highestScore;
            this.lowestScore = lowestScore;
        }
        
        public long getTotalStudents() {
            return totalStudents;
        }
        
        public Double getAverageScore() {
            return averageScore;
        }
        
        public Integer getHighestScore() {
            return highestScore;
        }
        
        public Integer getLowestScore() {
            return lowestScore;
        }
        
        @Override
        public String toString() {
            return "QuizStatistics{" +
                    "totalStudents=" + totalStudents +
                    ", averageScore=" + averageScore +
                    ", highestScore=" + highestScore +
                    ", lowestScore=" + lowestScore +
                    '}';
        }
    }
}

