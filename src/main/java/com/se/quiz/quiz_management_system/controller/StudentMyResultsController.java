package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.entity.Quiz;
import com.se.quiz.quiz_management_system.entity.StudentQuizResult;
import com.se.quiz.quiz_management_system.model.Role;
import com.se.quiz.quiz_management_system.model.UserSession;
import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.service.ResultService;
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

// Controller for the Student My Results screen
// Displays a grid of quiz results for the logged-in student

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
    private ResultService resultService;
    
    private List<QuizResult> quizResults;
    
    // Set the AuthService instance
    // @param authService the authentication service
    
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
    
    // Set the ResultService instance (injected from Spring context)
    // @param resultService the result service
    
    public void setResultService(ResultService resultService) {
        this.resultService = resultService;
        
        // Reload results when service is injected
        if (resultsContainer != null) {
            loadQuizResults();
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
    
    // Load quiz results and create dynamic cards
    // CRITICAL: Loads REAL data from database
    
    private void loadQuizResults() {
        // Clear existing children (if any)
        resultsContainer.getChildren().clear();
        
        // Check if service is available
        if (resultService == null) {
            System.out.println("⚠️ [StudentMyResultsController] ResultService not yet injected, showing empty state");
            showEmptyState("Service not available", "Please try again later.");
            return;
        }
        
        // Get current student ID
        Long studentId = getCurrentStudentId();
        if (studentId == null) {
            System.out.println("⚠️ [StudentMyResultsController] Student ID not found, showing empty state");
            showEmptyState("Not logged in", "Please log in to view your results.");
            return;
        }
        
        try {
            System.out.println("🔵 [StudentMyResultsController] Loading results for student ID: " + studentId);
            
            // ═══════════════════════════════════════════════════════════
            // CRITICAL: LOAD REAL RESULTS FROM DATABASE
            // ═══════════════════════════════════════════════════════════
            List<StudentQuizResult> dbResults = resultService.getResultsByStudentId(studentId);
            
            if (dbResults == null || dbResults.isEmpty()) {
                System.out.println("ℹ️ [StudentMyResultsController] No results found for student ID: " + studentId);
                showEmptyState("No results yet", "You haven't completed any quizzes yet.");
                return;
            }
            
            System.out.println("✅ [StudentMyResultsController] Found " + dbResults.size() + " results from database");
            
            // Convert database results to display models
            quizResults = new ArrayList<>();
            for (StudentQuizResult dbResult : dbResults) {
                // Get quiz name from relationship
                Quiz quiz = dbResult.getQuiz();
                String quizName = (quiz != null && quiz.getQuizName() != null) 
                    ? quiz.getQuizName() 
                    : "Quiz ID: " + dbResult.getQuizId();
                
                // Get score
                int score = dbResult.getScore() != null ? dbResult.getScore() : 0;
                int maxScore = dbResult.getTotalPoints() != null ? dbResult.getTotalPoints() : 100;
                
                QuizResult displayResult = new QuizResult(quizName, score, maxScore);
                quizResults.add(displayResult);
                
                System.out.println("   - Quiz: " + quizName + ", Score: " + score + "/" + maxScore);
            }
            
            // Create a card for each quiz result
            for (QuizResult result : quizResults) {
                AnchorPane card = createResultCard(result);
                resultsContainer.getChildren().add(card);
            }
            
            System.out.println("✅ [StudentMyResultsController] Displayed " + quizResults.size() + " result cards");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ [StudentMyResultsController] Error loading results: " + e.getMessage());
            showEmptyState("Error loading results", "Failed to load your quiz results: " + e.getMessage());
        }
    }
    
    // Get current student ID from AuthService or SessionManager
    // @return student ID or null if not found
    
    private Long getCurrentStudentId() {
        // Try to get from AuthService
        if (authService != null && authService.getCurrentUser() != null) {
            UserSession currentUser = authService.getCurrentUser();
            
            // Check if user is a student
            if (currentUser.getRole() == Role.STUDENT && currentUser.getUserId() != null) {
                return currentUser.getUserId();
            }
        }
        
        // Alternative: Get from SessionManager
        Long userId = SessionManager.getCurrentUserId();
        if (userId != null) {
            // Verify role is STUDENT
            UserSession session = SessionManager.getCurrentUserSession();
            if (session != null && session.getRole() == Role.STUDENT) {
                return userId;
            }
        }
        
        return null;
    }
    
    // Show empty state message when no results available
    // @param title the title message
    // @param message the detail message
    
    private void showEmptyState(String title, String message) {
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setStyle("-fx-padding: 60px;");
        emptyState.setPrefWidth(600);
        
        Label titleLabel = new Label("📊 " + title);
        titleLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: 600; " +
            "-fx-text-fill: #757575;"
        );
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #9E9E9E;"
        );
        messageLabel.setWrapText(true);
        
        emptyState.getChildren().addAll(titleLabel, messageLabel);
        resultsContainer.getChildren().add(emptyState);
    }
    
    // Create a result card (AnchorPane)
    // @param result the quiz result data
    // @return AnchorPane representing the result card
    
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
    
    // Handle Back to Dashboard button click
    // Uses NavigationManager to preserve window state
    
    @FXML
    private void handleBackToDashboard() {
        NavigationManager.getInstance().navigateTo(AppScreen.STUDENT_DASHBOARD);
    }
    
    // Handle Logout button click
    
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
    
    // QuizResult - represents a student's quiz result
    
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
