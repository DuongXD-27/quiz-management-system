package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.entity.Quiz;
import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.service.QuizService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for Quiz Results List View
 * Displays all quizzes with "View All Student Results" button for each quiz
 */
public class QuizResultsListController implements Initializable {
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private Button btnBackToDashboard;
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private VBox quizCardsContainer;
    
    private AuthService authService;
    private QuizService quizService;
    
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
    
    /**
     * Set the QuizService instance (injected from Spring context)
     * @param quizService the quiz service
     */
    public void setQuizService(QuizService quizService) {
        this.quizService = quizService;
        
        // Reload data when service is injected
        if (quizCardsContainer != null) {
            loadQuizCards();
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
        
        // Load quiz cards (will be called again when service is injected)
        loadQuizCards();
    }
    
    /**
     * Load quiz cards from database
     */
    private void loadQuizCards() {
        // Clear existing cards
        quizCardsContainer.getChildren().clear();
        
        // Validate service
        if (quizService == null) {
            System.out.println("QuizService not yet injected, waiting...");
            return;
        }
        
        try {
            // Fetch all quizzes from database
            List<Quiz> quizzes = quizService.getAllQuizzes();
            
            if (quizzes == null || quizzes.isEmpty()) {
                // Show empty state
                showEmptyState();
                return;
            }
            
            // Create a card for each quiz
            for (Quiz quiz : quizzes) {
                VBox quizCard = createQuizCard(quiz);
                quizCardsContainer.getChildren().add(quizCard);
            }
            
            System.out.println("Loaded " + quizzes.size() + " quiz cards");
            
        } catch (Exception e) {
            e.printStackTrace();
            JavaFXHelper.showError("Load Error", "Failed to load quizzes: " + e.getMessage());
        }
    }
    
    /**
     * Create a quiz card UI component
     * @param quiz the quiz entity
     * @return VBox containing the quiz card
     */
    private VBox createQuizCard(Quiz quiz) {
        // Main card container
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(15);
        card.setPadding(new Insets(25, 30, 25, 30));
        card.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 12px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 2); " +
            "-fx-border-color: #E0E0E0; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 12px;"
        );
        
        // Quiz info section
        VBox infoSection = new VBox(8);
        infoSection.setAlignment(Pos.CENTER_LEFT);
        
        // Quiz name
        Label lblQuizName = new Label(quiz.getQuizName());
        lblQuizName.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: 700; " +
            "-fx-text-fill: #1A237E;"
        );
        
        // Quiz details
        HBox detailsBox = new HBox(20);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        
        // Time limit
        Label lblTimeLimit = new Label("⏱ Time Limit: " + quiz.getTimeLimit() + " minutes");
        lblTimeLimit.setStyle("-fx-font-size: 14px; -fx-text-fill: #546E7A;");
        
        // Number of questions
        Label lblQuestions = new Label("📝 Questions: " + quiz.getNumberOfQuestion());
        lblQuestions.setStyle("-fx-font-size: 14px; -fx-text-fill: #546E7A;");
        
        detailsBox.getChildren().addAll(lblTimeLimit, lblQuestions);
        
        infoSection.getChildren().addAll(lblQuizName, detailsBox);
        
        // Action section
        HBox actionSection = new HBox(15);
        actionSection.setAlignment(Pos.CENTER_LEFT);
        
        // Spacer to push button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // View Results button
        Button btnViewResults = new Button("View All Student Results");
        btnViewResults.setStyle(
            "-fx-background-color: #1976D2; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: 600; " +
            "-fx-padding: 12 24 12 24; " +
            "-fx-background-radius: 8px; " +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        btnViewResults.setOnMouseEntered(e -> {
            btnViewResults.setStyle(
                "-fx-background-color: #1565C0; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: 600; " +
                "-fx-padding: 12 24 12 24; " +
                "-fx-background-radius: 8px; " +
                "-fx-cursor: hand;"
            );
        });
        
        btnViewResults.setOnMouseExited(e -> {
            btnViewResults.setStyle(
                "-fx-background-color: #1976D2; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: 600; " +
                "-fx-padding: 12 24 12 24; " +
                "-fx-background-radius: 8px; " +
                "-fx-cursor: hand;"
            );
        });
        
        // Button click handler
        btnViewResults.setOnAction(e -> handleViewStudentResults(quiz));
        
        actionSection.getChildren().addAll(spacer, btnViewResults);
        
        // Add all sections to card
        card.getChildren().addAll(infoSection, actionSection);
        
        return card;
    }
    
    /**
     * Show empty state when no quizzes exist
     */
    private void showEmptyState() {
        VBox emptyState = new VBox(20);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(60));
        emptyState.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 12px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);"
        );
        
        Label lblEmpty = new Label("📋 No Quizzes Available");
        lblEmpty.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: 600; " +
            "-fx-text-fill: #757575;"
        );
        
        Label lblMessage = new Label("Create quizzes first to view student results.");
        lblMessage.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #9E9E9E;"
        );
        
        Button btnCreateQuiz = new Button("Go to Quiz List");
        btnCreateQuiz.setStyle(
            "-fx-background-color: #1976D2; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: 600; " +
            "-fx-padding: 12 24 12 24; " +
            "-fx-background-radius: 8px; " +
            "-fx-cursor: hand;"
        );
        btnCreateQuiz.setOnAction(e -> NavigationManager.getInstance().navigateTo(AppScreen.QUIZ_LIST));
        
        emptyState.getChildren().addAll(lblEmpty, lblMessage, btnCreateQuiz);
        quizCardsContainer.getChildren().add(emptyState);
    }
    
    /**
     * Handle View Student Results button click
     * Navigate to Student Results screen with quiz data
     * @param quiz the selected quiz
     */
    private void handleViewStudentResults(Quiz quiz) {
        try {
            // Prepare data to pass to Student Results screen
            Map<String, Object> data = new HashMap<>();
            data.put("quizId", quiz.getQuizId());
            data.put("quizName", quiz.getQuizName());
            
            System.out.println("Navigating to Student Results for quiz: " + quiz.getQuizName());
            
            // Navigate to Student Results screen
            NavigationManager.getInstance().navigateTo(AppScreen.STUDENT_RESULTS, data);
            
        } catch (Exception e) {
            e.printStackTrace();
            JavaFXHelper.showError("Navigation Error", "Failed to view student results: " + e.getMessage());
        }
    }
    
    /**
     * Handle Back to Dashboard button click
     */
    @FXML
    private void handleBackToDashboard() {
        NavigationManager.getInstance().navigateTo(AppScreen.TEACHER_DASHBOARD);
    }
    
    /**
     * Handle Logout button click
     */
    @FXML
    private void handleLogout() {
        // Clear session
        if (authService != null) {
            authService.logout();
        } else {
            SessionManager.clearSession();
        }
        
        // Navigate to Login screen
        NavigationManager.getInstance().navigateToLogin();
    }
}


