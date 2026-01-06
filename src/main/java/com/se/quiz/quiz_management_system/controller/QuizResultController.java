package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Quiz Result view
 * Displays the quiz results with score, subject, and completion time
 */
public class QuizResultController implements Initializable {
    
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
     * Set the result data to display
     * This method should be called after loading the FXML
     * 
     * @param subject The quiz subject name
     * @param score The user's score
     * @param totalQuestions The total number of questions
     * @param timeTaken The time taken to complete the quiz (e.g., "25 minutes")
     */
    public void setResultData(String subject, int score, int totalQuestions, String timeTaken) {
        this.subjectName = subject;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeTaken = timeTaken;
        
        // Update UI
        lblScore.setText(score + " / " + totalQuestions);
        lblSubject.setText("Subject: " + subject);
        lblCompletionTime.setText("Completion time: " + timeTaken);
    }
    
    /**
     * Handle Return to Dashboard button click
     */
    @FXML
    private void handleReturnToDashboard() {
        try {
            // Load Student Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentDashboard.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Get current stage and set new scene
            Stage stage = (Stage) btnReturnToDashboard.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Student Dashboard - Quiz Management System");
            
        } catch (IOException e) {
            e.printStackTrace();
            JavaFXHelper.showError("Navigation Error", "Failed to load Student Dashboard.");
        }
    }
    
    /**
     * Handle Review Answers button click
     */
    @FXML
    private void handleReviewAnswers() {
        // TODO: Implement Review Answers functionality
        // This would load a screen showing all questions with user's answers and correct answers
        JavaFXHelper.showInfo("Coming Soon", "Review Answers feature will be implemented in the next version.");
    }
    
    /**
     * Handle Exit button click
     */
    @FXML
    private void handleExit() {
        // Return to dashboard instead of closing
        handleReturnToDashboard();
    }
}
