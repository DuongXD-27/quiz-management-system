package com.se.quiz.quiz_management_system.service;

import com.se.quiz.quiz_management_system.entity.Question;
import com.se.quiz.quiz_management_system.entity.Quiz;
import com.se.quiz.quiz_management_system.entity.QuizQuestion;
import com.se.quiz.quiz_management_system.entity.Student;
import com.se.quiz.quiz_management_system.entity.StudentQuiz;
import com.se.quiz.quiz_management_system.exception.ResourceNotFoundException;
import com.se.quiz.quiz_management_system.repository.QuestionRepository;
import com.se.quiz.quiz_management_system.repository.QuizQuestionRepository;
import com.se.quiz.quiz_management_system.repository.QuizRepository;
import com.se.quiz.quiz_management_system.repository.StudentRepository;
import com.se.quiz.quiz_management_system.repository.StudentQuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * QuizService - Handles quiz management operations
 */
@Service
public class QuizService {
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private StudentQuizRepository studentQuizRepository;
    
    /**
     * Create a new quiz with multiple questions in a single transaction
     * 
     * @param quizName the name of the quiz
     * @param timeLimit the time limit per question in minutes
     * @param questions the list of questions to add to the quiz
     * @return the created Quiz entity with generated ID
     * @throws IllegalArgumentException if questions list is empty
     */
    @Transactional
    public Quiz createQuizWithQuestions(String quizName, Integer timeLimit, List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Quiz must have at least one question");
        }
        
        try {
            // Step 1: Create and save the Quiz entity
            Quiz quiz = new Quiz(quizName, timeLimit, questions.size());
            quiz = quizRepository.save(quiz);
            
            // Step 2: Save all questions and create quiz-question relationships
            for (Question question : questions) {
                // Save the question to get generated ID
                Question savedQuestion = questionRepository.save(question);
                
                // Create quiz-question relationship
                QuizQuestion quizQuestion = new QuizQuestion();
                quizQuestion.setQuizId(quiz.getQuizId());
                quizQuestion.setQuestionId(savedQuestion.getQuestionId());
                quizQuestion.setQuiz(quiz);
                quizQuestion.setQuestion(savedQuestion);
                
                quizQuestionRepository.save(quizQuestion);
            }
            
            return quiz;
            
        } catch (Exception e) {
            // Transaction will automatically rollback on exception
            throw new RuntimeException("Failed to create quiz with questions: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get all quizzes
     * @return list of all quizzes
     */
    @Transactional(readOnly = true)
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAllByOrderByQuizIdDesc();
    }
    
    /**
     * Get a quiz by ID
     * @param quizId the quiz ID
     * @return the Quiz entity
     * @throws ResourceNotFoundException if quiz not found
     */
    @Transactional(readOnly = true)
    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
            .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));
    }
    
    /**
     * Get all questions for a specific quiz
     * @param quizId the quiz ID
     * @return list of questions
     * @throws ResourceNotFoundException if quiz not found
     */
    @Transactional(readOnly = true)
    public List<Question> getQuestionsForQuiz(Long quizId) {
        // Verify quiz exists
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with ID: " + quizId);
        }
        
        // Get all quiz-question relationships
        List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuizId(quizId);
        
        // Extract questions
        List<Question> questions = new ArrayList<>();
        for (QuizQuestion qq : quizQuestions) {
            if (qq.getQuestion() != null) {
                questions.add(qq.getQuestion());
            } else {
                // Fetch question if not loaded
                questionRepository.findById(qq.getQuestionId()).ifPresent(questions::add);
            }
        }
        
        return questions;
    }
    
    /**
     * Delete a quiz and all its questions
     * @param quizId the quiz ID
     * @throws ResourceNotFoundException if quiz not found
     */
    @Transactional
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));
        
        // Delete all quiz-question relationships (will cascade delete if configured)
        quizQuestionRepository.deleteByQuizId(quizId);
        
        // Delete the quiz itself
        quizRepository.delete(quiz);
    }
    
    /**
     * Search quizzes by name
     * @param quizName the name to search for (partial match)
     * @return list of matching quizzes
     */
    @Transactional(readOnly = true)
    public List<Quiz> searchQuizzesByName(String quizName) {
        return quizRepository.findByQuizNameContainingIgnoreCase(quizName);
    }
    
    /**
     * Assign a quiz to a student
     * Creates a record in student_quiz junction table
     * 
     * @param quizId the quiz ID to assign
     * @param studentUsername the username of the student
     * @return true if assignment successful, false otherwise
     * @throws ResourceNotFoundException if quiz or student not found
     * @throws IllegalStateException if student already assigned to quiz
     */
    @Transactional
    public boolean assignQuizToStudent(Long quizId, String studentUsername) {
        // Verify quiz exists
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));
        
        // Find student by username
        Student student = studentRepository.findByUsername(studentUsername)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with username: " + studentUsername));
        
        // Check if already assigned
        if (studentQuizRepository.existsByStudentIdAndQuizId(student.getStudentId(), quizId)) {
            throw new IllegalStateException("Student is already assigned to this quiz");
        }
        
        // Create assignment
        StudentQuiz studentQuiz = new StudentQuiz();
        studentQuiz.setStudentId(student.getStudentId());
        studentQuiz.setQuizId(quiz.getQuizId());
        studentQuiz.setStudent(student);
        studentQuiz.setQuiz(quiz);
        
        studentQuizRepository.save(studentQuiz);
        
        return true;
    }
    
    /**
     * Remove a student's assignment from a quiz
     * 
     * @param quizId the quiz ID
     * @param studentUsername the username of the student
     * @return true if removal successful
     * @throws ResourceNotFoundException if quiz or student not found
     */
    @Transactional
    public boolean removeStudentFromQuiz(Long quizId, String studentUsername) {
        // Verify quiz exists
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with ID: " + quizId);
        }
        
        // Find student by username
        Student student = studentRepository.findByUsername(studentUsername)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with username: " + studentUsername));
        
        // Delete assignment
        studentQuizRepository.deleteByStudentIdAndQuizId(student.getStudentId(), quizId);
        
        return true;
    }
    
    /**
     * Get all quizzes assigned to a specific student
     * Uses JOIN query to fetch only quizzes the student has access to
     * 
     * @param studentId the student ID
     * @return list of quizzes assigned to the student
     */
    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesForStudent(Long studentId) {
        // Get all student-quiz relationships for this student
        List<StudentQuiz> studentQuizzes = studentQuizRepository.findByStudentId(studentId);
        
        // Extract quiz IDs and fetch quizzes
        List<Quiz> quizzes = new ArrayList<>();
        for (StudentQuiz sq : studentQuizzes) {
            if (sq.getQuiz() != null) {
                quizzes.add(sq.getQuiz());
            } else {
                // Fetch quiz if not loaded
                quizRepository.findById(sq.getQuizId()).ifPresent(quizzes::add);
            }
        }
        
        return quizzes;
    }
    
    /**
     * Get all students assigned to a specific quiz
     * 
     * @param quizId the quiz ID
     * @return list of students assigned to the quiz
     */
    @Transactional(readOnly = true)
    public List<Student> getStudentsForQuiz(Long quizId) {
        // Verify quiz exists
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with ID: " + quizId);
        }
        
        // Get all student-quiz relationships for this quiz
        List<StudentQuiz> studentQuizzes = studentQuizRepository.findByQuizId(quizId);
        
        // Extract students
        List<Student> students = new ArrayList<>();
        for (StudentQuiz sq : studentQuizzes) {
            if (sq.getStudent() != null) {
                students.add(sq.getStudent());
            } else {
                // Fetch student if not loaded
                studentRepository.findById(sq.getStudentId()).ifPresent(students::add);
            }
        }
        
        return students;
    }
}

