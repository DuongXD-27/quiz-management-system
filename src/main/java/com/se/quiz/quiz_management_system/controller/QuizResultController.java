package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationAware;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the Quiz Result view
 * Displays the quiz results with score, subject, and completion time
 */
public class QuizResultController implements Initializable, NavigationAware {
    
    @FXML
    private Label lblScore;
    
    @FXML
    private Label lblSubject;
    
    @FXML
    private Label lblCompletionTime;
    
    @FXML
    private Button btnReturnToDashboard;
    
    @FXML
    private Button btnReviewAnswers;
    
    @FXML
    private Button btnExit;
    
    // Result data
    private String subjectName;
    private int score;
    private int totalQuestions;
    private String timeTaken;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Default values will be overridden by setResultData()
    }
    
    /**
     * Called when navigated to this screen
     * Receives result data from previous screen via NavigationManager
     */
    @Override
    public void onNavigatedTo(Map<String, Object> data) {
        if (data != null) {
            this.subjectName = (String) data.get("subject");
            this.score = data.containsKey("score") ? (Integer) data.get("score") : 0;
            this.totalQuestions = data.containsKey("totalPoints") ? (Integer) data.get("totalPoints") : 0;
            this.timeTaken = (String) data.get("timeTaken");
            
            // Update UI with received data
            lblScore.setText(score + " / " + totalQuestions);
            lblSubject.setText("Subject: " + subjectName);
            lblCompletionTime.setText("Completion time: " + timeTaken);
        }
    }
    
    /**
     * Handle Return to Dashboard button click
     * Uses NavigationManager to preserve window state
     */
    @FXML
    private void handleReturnToDashboard() {
        NavigationManager.getInstance().navigateTo(AppScreen.STUDENT_DASHBOARD);
    }
    
    /**
     * Handle Review Answers button click
     */
    @FXML
    private void handleReviewAnswers() {
        // TODO: Implement Review Answers functionality
        // This would load a screen showing all questions with user's answers and correct answers
        // NavigationManager.getInstance().navigateTo(AppScreen.REVIEW_ANSWERS, data);
        System.out.println("Review Answers feature - Coming Soon");
    }
    
    /**
     * Handle Exit button click
     */
    @FXML
    private void handleExit() {
        // Return to dashboard
        handleReturnToDashboard();
    }
}
