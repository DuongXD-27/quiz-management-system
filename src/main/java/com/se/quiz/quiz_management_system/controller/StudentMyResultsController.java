package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Student My Results screen
 * Displays a grid of quiz results for the logged-in student
 */
public class StudentMyResultsController implements Initializable {
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private Button btnBackToDashboard;
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private FlowPane resultsContainer;
    
    private AuthService authService;
    
    private List<QuizResult> quizResults;
    
    /**
     * Set the AuthService instance
     * @param authService the authentication service
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
        
        // Update welcome text if user is logged in
        if (authService != null && authService.getCurrentUser() != null) {
            lblWelcome.setText("Welcome, " + authService.getCurrentUser().getUsername());
        } else {
            String username = SessionManager.getCurrentUsername();
            if (username != null) {
                lblWelcome.setText("Welcome, " + username);
            }
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up welcome text
        String username = SessionManager.getCurrentUsername();
        if (username != null) {
            lblWelcome.setText("Welcome, " + username);
        } else {
            lblWelcome.setText("Welcome,");
        }
        
        // Load quiz results data
        loadQuizResults();
    }
    
    /**
     * Load quiz results and create dynamic cards
     */
    private void loadQuizResults() {
        // Generate mock data
        quizResults = generateMockData();
        
        // Clear existing children (if any)
        resultsContainer.getChildren().clear();
        
        // Create a card for each quiz result
        for (QuizResult result : quizResults) {
            AnchorPane card = createResultCard(result);
            resultsContainer.getChildren().add(card);
        }
        
        System.out.println("Loaded " + quizResults.size() + " quiz results");
    }
    
    /**
     * Generate mock quiz result data
     * @return List of QuizResult objects
     */
    private List<QuizResult> generateMockData() {
        List<QuizResult> results = new ArrayList<>();
        
        results.add(new QuizResult("Midterm Exam", 85, 100));
        results.add(new QuizResult("Java Quiz #1", 92, 100));
        results.add(new QuizResult("Database Basics", 78, 100));
        results.add(new QuizResult("Web Development", 88, 100));
        results.add(new QuizResult("Algorithms Test", 95, 100));
        results.add(new QuizResult("Final Exam", 90, 100));
        results.add(new QuizResult("OOP Concepts", 87, 100));
        results.add(new QuizResult("Data Structures", 91, 100));
        
        return results;
    }
    
    /**
     * Create a result card (AnchorPane)
     * @param result the quiz result data
     * @return AnchorPane representing the result card
     */
    private AnchorPane createResultCard(QuizResult result) {
        // Main card container
        AnchorPane card = new AnchorPane();
        card.getStyleClass().add("my-result-card");
        card.setPrefWidth(260);
        card.setPrefHeight(180);
        card.setMinWidth(260);
        card.setMinHeight(180);
        card.setMaxWidth(260);
        card.setMaxHeight(180);
        
        // Top Left: "Quiz Name" Label
        Label lblQuizTitle = new Label("Quiz Name");
        lblQuizTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: 400; -fx-text-fill: #757575;");
        AnchorPane.setTopAnchor(lblQuizTitle, 20.0);
        AnchorPane.setLeftAnchor(lblQuizTitle, 20.0);
        
        // Top Left: Actual Quiz Name (Bold)
        Label lblQuizName = new Label(result.getQuizName());
        lblQuizName.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #2D3447;");
        lblQuizName.setWrapText(true);
        lblQuizName.setPrefWidth(180);
        AnchorPane.setTopAnchor(lblQuizName, 42.0);
        AnchorPane.setLeftAnchor(lblQuizName, 20.0);
        
        // Top Right: Trophy Icon 🏆
        Label lblTrophy = new Label("🏆");
        lblTrophy.setStyle("-fx-font-size: 32px;");
        AnchorPane.setTopAnchor(lblTrophy, 15.0);
        AnchorPane.setRightAnchor(lblTrophy, 15.0);
        
        // Bottom Left: "Score" Label
        Label lblScoreTitle = new Label("Score");
        lblScoreTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: 400; -fx-text-fill: #757575;");
        AnchorPane.setBottomAnchor(lblScoreTitle, 50.0);
        AnchorPane.setLeftAnchor(lblScoreTitle, 20.0);
        
        // Bottom Left: Actual Score (Bold Large)
        Label lblScore = new Label(result.getScore() + "/" + result.getMaxScore());
        lblScore.setStyle("-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: #6A1B9A;");
        AnchorPane.setBottomAnchor(lblScore, 15.0);
        AnchorPane.setLeftAnchor(lblScore, 20.0);
        
        // Bottom Right: Chart Icon 📊
        Label lblChart = new Label("📊");
        lblChart.setStyle("-fx-font-size: 32px;");
        AnchorPane.setBottomAnchor(lblChart, 15.0);
        AnchorPane.setRightAnchor(lblChart, 15.0);
        
        // Add all elements to card
        card.getChildren().addAll(lblQuizTitle, lblQuizName, lblTrophy, lblScoreTitle, lblScore, lblChart);
        
        return card;
    }
    
    /**
     * Handle Back to Dashboard button click
     */
    @FXML
    private void handleBackToDashboard() {
        try {
            // Load the Student Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentDashboard.fxml"));
            Parent root = loader.load();
            
            // Pass AuthService to the dashboard controller
            StudentDashboardController controller = loader.getController();
            if (authService != null) {
                controller.setAuthService(authService);
            }
            
            // Get current stage and set new scene
            Stage stage = (Stage) btnBackToDashboard.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Student Dashboard");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            JavaFXHelper.showError("Navigation Error", "Failed to load Student Dashboard");
        }
    }
    
    /**
     * Handle Logout button click
     */
    @FXML
    private void handleLogout() {
        try {
            // Clear session
            if (authService != null) {
                authService.logout();
            } else {
                SessionManager.clearSession();
            }
            
            // Load the Login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            
            // Pass AuthService to the login controller
            LoginController controller = loader.getController();
            if (authService != null) {
                controller.setAuthService(authService);
            }
            
            // Get current stage and set new scene
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Login");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            JavaFXHelper.showError("Logout Error", "Failed to logout. Please try again.");
        }
    }
    
    /**
     * QuizResult - represents a student's quiz result
     */
    private static class QuizResult {
        private final String quizName;
        private final int score;
        private final int maxScore;
        
        public QuizResult(String quizName, int score, int maxScore) {
            this.quizName = quizName;
            this.score = score;
            this.maxScore = maxScore;
        }
        
        public String getQuizName() {
            return quizName;
        }
        
        public int getScore() {
            return score;
        }
        
        public int getMaxScore() {
            return maxScore;
        }
    }
}
