package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Take Quiz view
 * Handles quiz question display, answer selection, and timer
 */
public class TakeQuizController implements Initializable {
    
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
    
    // Current question data
    private Question currentQuestion;
    private Button selectedAnswerButton;
    private int timeRemaining = 30; // seconds
    private Timeline timerTimeline;
    
    // Quiz tracking
    private int currentQuestionNumber = 1;
    private int totalQuestions = 5; // Mock: 5 questions in this quiz
    private int correctAnswers = 0;
    private int startTime; // To track completion time
    
    private AuthService authService;
    
    /**
     * Set the AuthService instance
     * @param authService the authentication service
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Simple Question data class
     */
    private static class Question {
        private final String questionText;
        private final List<String> answers;
        private final int correctAnswerIndex; // 0-based index
        
        public Question(String questionText, List<String> answers, int correctAnswerIndex) {
            this.questionText = questionText;
            this.answers = answers;
            this.correctAnswerIndex = correctAnswerIndex;
        }
        
        public String getQuestionText() {
            return questionText;
        }
        
        public List<String> getAnswers() {
            return answers;
        }
        
        public int getCorrectAnswerIndex() {
            return correctAnswerIndex;
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Record start time (in seconds)
        startTime = (int) (System.currentTimeMillis() / 1000);
        
        // Initialize with mock question
        loadQuestion();
        
        // Start timer
        startTimer();
    }
    
    /**
     * Load a mock question
     */
    private void loadQuestion() {
        // Create mock question
        List<String> answers = new ArrayList<>();
        answers.add("London");
        answers.add("Paris");
        answers.add("Berlin");
        answers.add("Madrid");
        
        currentQuestion = new Question("What is the capital of France?", answers, 1);
        
        // Display question
        lblQuestion.setText(currentQuestion.getQuestionText());
        
        // Display answers
        btnAnswerA.setText("A. " + answers.get(0));
        btnAnswerB.setText("B. " + answers.get(1));
        btnAnswerC.setText("C. " + answers.get(2));
        btnAnswerD.setText("D. " + answers.get(3));
        
        // Reset selection
        clearSelection();
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
     */
    private void startTimer() {
        timeRemaining = 30;
        updateTimerDisplay();
        
        // Create timeline that updates every second
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimerDisplay();
            
            if (timeRemaining <= 0) {
                stopTimer();
                // Auto-submit or show timeout message
                JavaFXHelper.showWarning("Time's Up!", "Time has run out for this question.");
            }
        }));
        
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
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
        
        // Get selected answer index
        int selectedIndex = -1;
        if (selectedAnswerButton == btnAnswerA) selectedIndex = 0;
        else if (selectedAnswerButton == btnAnswerB) selectedIndex = 1;
        else if (selectedAnswerButton == btnAnswerC) selectedIndex = 2;
        else if (selectedAnswerButton == btnAnswerD) selectedIndex = 3;
        
        // Check if answer is correct
        boolean isCorrect = (selectedIndex == currentQuestion.getCorrectAnswerIndex());
        
        // Track correct answers
        if (isCorrect) {
            correctAnswers++;
        }
        
        // Check if this was the last question
        if (currentQuestionNumber >= totalQuestions) {
            // Quiz finished - Navigate to Result screen
            stopTimer();
            navigateToResultScreen();
        } else {
            // Move to next question
            currentQuestionNumber++;
            stopTimer();
            loadQuestion();
            startTimer();
        }
    }
    
    /**
     * Navigate to the Quiz Result screen
     */
    private void navigateToResultScreen() {
        try {
            // Calculate completion time
            int endTime = (int) (System.currentTimeMillis() / 1000);
            int timeTakenSeconds = endTime - startTime;
            int minutes = timeTakenSeconds / 60;
            int seconds = timeTakenSeconds % 60;
            String timeTaken = minutes + " minute" + (minutes != 1 ? "s" : "");
            if (seconds > 0) {
                timeTaken += " " + seconds + " second" + (seconds != 1 ? "s" : "");
            }
            
            // Load QuizResult.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/QuizResult.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Get the controller and set result data
            QuizResultController controller = loader.getController();
            controller.setResultData(
                "Mathematics - Chapter 1",  // Subject (mock data)
                correctAnswers * 10,         // Score (10 points per correct answer)
                totalQuestions * 10,         // Total points
                timeTaken
            );
            
            // Get current stage and set new scene
            Stage stage = (Stage) btnNext.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Quiz Result - Quiz Management System");
            
        } catch (IOException e) {
            e.printStackTrace();
            JavaFXHelper.showError("Navigation Error", "Failed to load Quiz Result screen.");
        }
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
