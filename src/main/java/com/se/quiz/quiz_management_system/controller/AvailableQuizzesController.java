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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Available Quizzes view (Student)
 * Displays available quizzes in a card-based layout
 */
public class AvailableQuizzesController implements Initializable {
    
    @FXML
    private VBox quizContainer;
    
    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnLogout;
    
    private AuthService authService;
    
    /**
     * Set the AuthService instance
     * @param authService the AuthService instance
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Mock Quiz data class for demonstration
     */
    private static class QuizData {
        private final String subject;
        private final String duration;
        private final String points;
        
        public QuizData(String subject, String duration, String points) {
            this.subject = subject;
            this.duration = duration;
            this.points = points;
        }
        
        public String getSubject() {
            return subject;
        }
        
        public String getDuration() {
            return duration;
        }
        
        public String getPoints() {
            return points;
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadQuizzes();
    }
    
    /**
     * Load quizzes and create quiz cards dynamically
     */
    private void loadQuizzes() {
        // Generate mock quiz data
        List<QuizData> quizzes = generateMockQuizzes();
        
        // Clear existing children
        quizContainer.getChildren().clear();
        
        // Create a quiz card for each quiz
        for (QuizData quiz : quizzes) {
            HBox quizCard = createQuizCard(quiz);
            quizContainer.getChildren().add(quizCard);
        }
    }
    
    /**
     * Generate mock quiz data
     * @return List of QuizData objects
     */
    private List<QuizData> generateMockQuizzes() {
        List<QuizData> quizzes = new ArrayList<>();
        
        quizzes.add(new QuizData("Mathematics - Chapter 1", "60 minutes", "100 points"));
        quizzes.add(new QuizData("Physics - Mechanics", "45 minutes", "80 points"));
        quizzes.add(new QuizData("Chemistry - Organic", "50 minutes", "90 points"));
        quizzes.add(new QuizData("Biology - Cell Structure", "40 minutes", "75 points"));
        quizzes.add(new QuizData("History - World War II", "35 minutes", "70 points"));
        quizzes.add(new QuizData("English Literature", "55 minutes", "95 points"));
        
        return quizzes;
    }
    
    /**
     * Create a quiz card UI component
     * @param quiz the quiz data
     * @return HBox representing the quiz card
     */
    private HBox createQuizCard(QuizData quiz) {
        // Main card container
        HBox card = new HBox();
        card.getStyleClass().add("quiz-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(20);
        
        // Left section: Quiz information
        VBox infoSection = new VBox();
        infoSection.setAlignment(Pos.CENTER_LEFT);
        infoSection.setSpacing(8);
        infoSection.setPrefWidth(600);
        
        // Subject label
        Label subjectLabel = new Label(quiz.getSubject());
        subjectLabel.getStyleClass().add("quiz-subject");
        
        // Info labels container
        HBox infoContainer = new HBox();
        infoContainer.setSpacing(30);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Duration label
        Label durationLabel = new Label("⏱ " + quiz.getDuration());
        durationLabel.getStyleClass().add("quiz-info");
        
        // Points label
        Label pointsLabel = new Label("⭐ " + quiz.getPoints());
        pointsLabel.getStyleClass().add("quiz-info");
        
        infoContainer.getChildren().addAll(durationLabel, pointsLabel);
        infoSection.getChildren().addAll(subjectLabel, infoContainer);
        
        // Right section: Join Now button
        Button joinButton = new Button("Join Now");
        joinButton.getStyleClass().add("join-button");
        joinButton.setOnAction(e -> {
            // Get stage from the button's scene
            Stage stage = (Stage) joinButton.getScene().getWindow();
            handleJoinQuiz(quiz, stage);
        });
        
        // Add sections to card
        card.getChildren().addAll(infoSection, joinButton);
        
        return card;
    }
    
    /**
     * Handle Join Now button click
     * @param quiz the quiz data
     * @param stage the current stage
     */
    private void handleJoinQuiz(QuizData quiz, Stage stage) {
        try {
            // Load Take Quiz FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TakeQuiz.fxml"));
            Parent root = loader.load();
            
            // Inject AuthService into TakeQuizController if needed
            TakeQuizController takeQuizController = loader.getController();
            if (authService != null) {
                takeQuizController.setAuthService(authService);
            }
            
            // Create new scene and set it
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Take Quiz");
            
        } catch (Exception e) {
            JavaFXHelper.showError("Error", "Failed to load quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle Back button click - Navigate to Student Dashboard
     */
    @FXML
    private void handleBack() {
        try {
            // Get current stage
            Stage stage = (Stage) btnBack.getScene().getWindow();
            
            // Load Student Dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentDashboard.fxml"));
            Parent root = loader.load();
            
            // Inject AuthService into StudentDashboardController
            StudentDashboardController studentDashboardController = loader.getController();
            if (authService != null) {
                studentDashboardController.setAuthService(authService);
            }
            
            // Create new scene and set it
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Student Dashboard");
            
        } catch (Exception e) {
            JavaFXHelper.showError("Navigation Error", "Failed to go back: " + e.getMessage());
            e.printStackTrace();
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
}
