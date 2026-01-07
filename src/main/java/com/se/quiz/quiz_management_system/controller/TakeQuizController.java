package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.entity.Question;
import com.se.quiz.quiz_management_system.entity.Quiz;
import com.se.quiz.quiz_management_system.entity.StudentQuizResult;
import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationAware;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.service.QuizService;
import com.se.quiz.quiz_management_system.service.ResultService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the Take Quiz view
 * Handles quiz question display, answer selection, and timer
 */
public class TakeQuizController implements Initializable, NavigationAware {
    
    @FXML
    private Label lblTimer;
    
    @FXML
    private Label lblQuestion;
    
    @FXML
    private Button btnAnswerA;
    
    @FXML
    private Button btnAnswerB;
    
    @FXML
    private Button btnAnswerC;
    
    @FXML
    private Button btnAnswerD;
    
    @FXML
    private Button btnNext;
    
    @FXML
    private Button btnExit;
    
    @FXML
    private Label lblCurrentQuestion;
    
    @FXML
    private Label lblTotalQuestions;
    
    // Current question data
    private Question currentQuestion;
    private Button selectedAnswerButton;
    private int timeRemaining; // seconds (loaded from quiz)
    private Timeline timerTimeline;
    
    // Quiz tracking
    private Quiz currentQuiz;
    private List<Question> questions;
    private int currentQuestionIndex = 0; // 0-based index
    private int correctAnswers = 0;
    private int startTime; // To track completion time
    
    private AuthService authService;
    private QuizService quizService;
    private ResultService resultService;
    private Long quizId; // Quiz ID passed from previous screen
    private boolean quizDataLoaded = false;
    
    /**
     * Set the AuthService instance
     * @param authService the authentication service
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Set the QuizService instance (injected from Spring context)
     * @param quizService the quiz service
     */
    public void setQuizService(QuizService quizService) {
        this.quizService = quizService;
        
        // If quiz ID already set, load data now
        if (quizId != null && !quizDataLoaded) {
            loadQuizData();
        }
    }
    
    /**
     * Set the ResultService instance (injected from Spring context)
     * @param resultService the result service
     */
    public void setResultService(ResultService resultService) {
        this.resultService = resultService;
    }
    
    /**
     * Called when navigated to this screen
     * Receives data passed from previous screen
     */
    @Override
    public void onNavigatedTo(Map<String, Object> data) {
        if (data != null && data.containsKey("quizId")) {
            this.quizId = (Long) data.get("quizId");
            System.out.println("TakeQuizController received Quiz ID: " + quizId);
            
            // Load quiz data if service is available
            if (quizService != null && !quizDataLoaded) {
                loadQuizData();
            }
        }
    }
    
    /**
     * Load quiz data from database
     * CRITICAL: Loads real time limit and questions from DB
     */
    private void loadQuizData() {
        try {
            // Validate quiz service
            if (quizService == null) {
                JavaFXHelper.showError("Service Error", "Quiz service is not available.");
                handleExit();
                return;
            }
            
            // Validate quiz ID
            if (quizId == null) {
                JavaFXHelper.showError("Error", "No quiz selected.");
                handleExit();
                return;
            }
            
            // ✅ LOAD QUIZ FROM DATABASE
            currentQuiz = quizService.getQuizById(quizId);
            
            // ✅ SET TIME LIMIT FROM QUIZ (convert minutes to seconds)
            if (currentQuiz.getTimeLimit() != null && currentQuiz.getTimeLimit() > 0) {
                timeRemaining = currentQuiz.getTimeLimit() * 60; // minutes → seconds
            } else {
                // Default 30 minutes if not set
                timeRemaining = 30 * 60;
            }
            
            // ✅ LOAD QUESTIONS FROM DATABASE
            questions = quizService.getQuestionsForQuiz(quizId);
            
            // Validate questions exist
            if (questions == null || questions.isEmpty()) {
                JavaFXHelper.showError("No Questions", 
                    "This quiz has no questions. Please contact your teacher.");
                handleExit();
                return;
            }
            
            // Mark as loaded
            quizDataLoaded = true;
            
            // Record start time
            startTime = (int) (System.currentTimeMillis() / 1000);
            
            // ✅ UPDATE UI WITH REAL DATA
            if (lblTotalQuestions != null) {
                lblTotalQuestions.setText("/" + questions.size());
            }
            
            // ✅ START QUIZ
            showQuestion(0);
            startTimer();
            
            System.out.println("Loaded quiz: " + currentQuiz.getQuizName() + 
                             " with " + questions.size() + " questions, " + 
                             currentQuiz.getTimeLimit() + " minutes time limit");
            
        } catch (Exception e) {
            e.printStackTrace();
            JavaFXHelper.showError("Load Error", 
                "Failed to load quiz: " + e.getMessage());
            handleExit();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ✅ DO NOT START QUIZ HERE
        // Wait for quiz data to be loaded via onNavigatedTo() + loadQuizData()
        // This ensures we have real quiz data before starting
        
        System.out.println("TakeQuizController initialized - waiting for quiz data");
    }
    
    /**
     * Show a question from the loaded questions list
     * @param questionIndex the 0-based index of the question to display
     */
    private void showQuestion(int questionIndex) {
        // Validate index
        if (questions == null || questionIndex < 0 || questionIndex >= questions.size()) {
            System.err.println("Invalid question index: " + questionIndex);
            return;
        }
        
        // Get current question
        currentQuestion = questions.get(questionIndex);
        currentQuestionIndex = questionIndex;
        
        // ✅ DISPLAY REAL QUESTION FROM DATABASE
        lblQuestion.setText(currentQuestion.getProblem());
        
        // ✅ DISPLAY REAL OPTIONS FROM DATABASE
        btnAnswerA.setText("A. " + (currentQuestion.getOptionA() != null ? currentQuestion.getOptionA() : ""));
        btnAnswerB.setText("B. " + (currentQuestion.getOptionB() != null ? currentQuestion.getOptionB() : ""));
        btnAnswerC.setText("C. " + (currentQuestion.getOptionC() != null ? currentQuestion.getOptionC() : ""));
        btnAnswerD.setText("D. " + (currentQuestion.getOptionD() != null ? currentQuestion.getOptionD() : ""));
        
        // Update question counter
        if (lblCurrentQuestion != null) {
            lblCurrentQuestion.setText(String.valueOf(questionIndex + 1));
        }
        
        // Reset selection
        clearSelection();
        
        System.out.println("Showing question " + (questionIndex + 1) + "/" + questions.size());
    }
    
    /**
     * Handle answer button click
     */
    @FXML
    private void handleAnswerClick(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        
        // Remove selected class from all buttons
        clearSelection();
        
        // Add selected class to clicked button
        clickedButton.getStyleClass().add("selected");
        selectedAnswerButton = clickedButton;
    }
    
    /**
     * Clear selection from all answer buttons
     */
    private void clearSelection() {
        btnAnswerA.getStyleClass().remove("selected");
        btnAnswerB.getStyleClass().remove("selected");
        btnAnswerC.getStyleClass().remove("selected");
        btnAnswerD.getStyleClass().remove("selected");
        selectedAnswerButton = null;
    }
    
    /**
     * Start the countdown timer
     * ✅ Uses timeRemaining from quiz data (already set in loadQuizData)
     */
    private void startTimer() {
        // ✅ DO NOT RESET timeRemaining here - it's already set from quiz
        updateTimerDisplay();
        
        // Create timeline that updates every second
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimerDisplay();
            
            if (timeRemaining <= 0) {
                stopTimer();
                // Auto-submit quiz when time runs out
                JavaFXHelper.showWarning("Time's Up!", 
                    "Time has run out for this quiz. Your answers will be submitted automatically.");
                navigateToResultScreen();
            }
        }));
        
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
        
        System.out.println("Timer started: " + (timeRemaining / 60) + " minutes");
    }
    
    /**
     * Stop the timer
     */
    private void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }
    
    /**
     * Update timer display
     */
    private void updateTimerDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        lblTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }
    
    /**
     * Handle Next Question button click
     */
    @FXML
    private void handleNext() {
        if (selectedAnswerButton == null) {
            JavaFXHelper.showWarning("No Selection", "Please select an answer before proceeding.");
            return;
        }
        
        // Get selected answer letter (A, B, C, or D)
        String selectedAnswer = null;
        if (selectedAnswerButton == btnAnswerA) selectedAnswer = "A";
        else if (selectedAnswerButton == btnAnswerB) selectedAnswer = "B";
        else if (selectedAnswerButton == btnAnswerC) selectedAnswer = "C";
        else if (selectedAnswerButton == btnAnswerD) selectedAnswer = "D";
        
        // ✅ CHECK ANSWER AGAINST DATABASE VALUE
        String correctAnswer = currentQuestion.getCorrectAnswer();
        boolean isCorrect = (selectedAnswer != null && selectedAnswer.equalsIgnoreCase(correctAnswer));
        
        // Track correct answers
        if (isCorrect) {
            correctAnswers++;
            System.out.println("Correct! Answer: " + selectedAnswer);
        } else {
            System.out.println("Incorrect. Selected: " + selectedAnswer + ", Correct: " + correctAnswer);
        }
        
        // Check if this was the last question
        if (currentQuestionIndex >= questions.size() - 1) {
            // Quiz finished - Navigate to Result screen
            stopTimer();
            navigateToResultScreen();
        } else {
            // Move to next question
            showQuestion(currentQuestionIndex + 1);
        }
    }
    
    /**
     * Navigate to the Quiz Result screen
     * CRITICAL: Saves result to database before navigating
     */
    private void navigateToResultScreen() {
        // Calculate completion time
        int endTime = (int) (System.currentTimeMillis() / 1000);
        int timeTakenSeconds = endTime - startTime;
        int minutes = timeTakenSeconds / 60;
        int seconds = timeTakenSeconds % 60;
        String timeTaken = minutes + " minute" + (minutes != 1 ? "s" : "");
        if (seconds > 0) {
            timeTaken += " " + seconds + " second" + (seconds != 1 ? "s" : "");
        }
        
        // Calculate total questions
        int totalQuestions = questions != null ? questions.size() : 0;
        
        // Calculate score (10 points per question)
        int score = correctAnswers * 10;
        int totalPoints = totalQuestions * 10;
        
        // ═══════════════════════════════════════════════════════════
        // CRITICAL: SAVE RESULT TO DATABASE
        // ═══════════════════════════════════════════════════════════
        System.out.println("========================================");
        System.out.println("🔴 [TakeQuizController] STARTING RESULT SAVE PROCESS");
        System.out.println("========================================");
        
        try {
            // Get current student ID from session
            Long studentId = SessionManager.getCurrentUserId();
            System.out.println("🔵 [TakeQuizController] Retrieved studentId from session: " + studentId);
            System.out.println("🔵 [TakeQuizController] Current quizId: " + quizId);
            System.out.println("🔵 [TakeQuizController] ResultService available: " + (resultService != null));
            
            if (studentId == null) {
                System.err.println("❌ [TakeQuizController] CRITICAL ERROR: Student ID is NULL!");
                System.err.println("   - This means SessionManager.getCurrentUserId() returned null");
                System.err.println("   - Student may not be logged in properly");
            }
            
            if (quizId == null) {
                System.err.println("❌ [TakeQuizController] CRITICAL ERROR: Quiz ID is NULL!");
                System.err.println("   - This means quiz data was not passed correctly");
            }
            
            if (resultService == null) {
                System.err.println("❌ [TakeQuizController] CRITICAL ERROR: ResultService is NULL!");
                System.err.println("   - This means Spring dependency injection failed");
                System.err.println("   - Check if NavigationManager.injectResultService() is being called");
            }
            
            if (studentId != null && quizId != null && resultService != null) {
                System.out.println("🔵 [TakeQuizController] All required data available - creating result object");
                
                // Create result object
                StudentQuizResult result = new StudentQuizResult();
                result.setStudentId(studentId);
                result.setQuizId(quizId);
                result.setScore(score);
                result.setTotalPoints(totalPoints);
                result.setCorrectAnswers(correctAnswers);
                result.setTotalQuestions(totalQuestions);
                result.setCompletionTimeSeconds(timeTakenSeconds);
                // submittedAt will be set automatically in service
                
                System.out.println("🔵 [TakeQuizController] Result object created:");
                System.out.println("   - Student ID: " + result.getStudentId());
                System.out.println("   - Quiz ID: " + result.getQuizId());
                System.out.println("   - Score: " + result.getScore() + "/" + result.getTotalPoints());
                System.out.println("   - Correct: " + result.getCorrectAnswers() + "/" + result.getTotalQuestions());
                System.out.println("   - Time: " + result.getCompletionTimeSeconds() + " seconds");
                
                // CRITICAL: Save to database
                System.out.println("🔵 [TakeQuizController] Calling resultService.saveResult()...");
                StudentQuizResult savedResult = resultService.saveResult(result);
                
                System.out.println("========================================");
                System.out.println("✅ [TakeQuizController] RESULT SAVED SUCCESSFULLY!");
                System.out.println("   - Result ID: " + savedResult.getResultId());
                System.out.println("   - Student: " + savedResult.getStudentId());
                System.out.println("   - Quiz: " + savedResult.getQuizId());
                System.out.println("   - Score: " + savedResult.getScore() + "/" + savedResult.getTotalPoints());
                System.out.println("   - Submitted At: " + savedResult.getSubmittedAt());
                System.out.println("========================================");
            } else {
                System.err.println("========================================");
                System.err.println("❌ [TakeQuizController] CANNOT SAVE RESULT - Missing required data:");
                System.err.println("   - studentId: " + studentId);
                System.err.println("   - quizId: " + quizId);
                System.err.println("   - resultService: " + (resultService != null ? "available" : "NULL"));
                System.err.println("========================================");
            }
        } catch (IllegalStateException e) {
            // Student already completed this quiz (shouldn't happen, but handle it)
            System.err.println("❌ Error: Student has already completed this quiz");
            JavaFXHelper.showError("Duplicate Submission", 
                "You have already completed this quiz. Duplicate submissions are not allowed.");
        } catch (Exception e) {
            // Log error but continue to result screen
            e.printStackTrace();
            System.err.println("❌ Error saving result to database: " + e.getMessage());
            JavaFXHelper.showError("Save Error", 
                "Failed to save result to database. Please contact your teacher.");
        }
        
        // Prepare result data to pass to next screen
        java.util.Map<String, Object> resultData = new java.util.HashMap<>();
        resultData.put("subject", currentQuiz != null ? currentQuiz.getQuizName() : "Quiz"); // ✅ REAL QUIZ NAME
        resultData.put("score", score);
        resultData.put("totalPoints", totalPoints);
        resultData.put("timeTaken", timeTaken);
        resultData.put("correctAnswers", correctAnswers); // Add for clarity
        resultData.put("totalQuestions", totalQuestions); // Add for clarity
        
        System.out.println("Quiz completed: " + correctAnswers + "/" + totalQuestions + " correct");
        
        // Navigate to Quiz Result screen with data
        NavigationManager.getInstance().navigateTo(AppScreen.QUIZ_RESULT, resultData);
    }
    
    /**
     * Handle Exit button click
     */
    @FXML
    private void handleExit() {
        stopTimer();
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
}
