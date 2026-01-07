package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Teacher Dashboard view
 */
public class TeacherDashboardController implements Initializable {
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private VBox cardQuestions;
    
    @FXML
    private VBox cardQuizzes;
    
    @FXML
    private VBox cardResults;
    
    @FXML
    private BorderPane mainContent;
    
    @FXML
    private StackPane dialogContainer;
    
    private AuthService authService;
    
    /**
     * Set the AuthService instance (injected from Spring context)
     * @param authService the AuthService instance
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set welcome text with username
        String username = SessionManager.getCurrentUsername();
        if (username != null) {
            lblWelcome.setText("Xin chào, " + username);
        } else {
            lblWelcome.setText("Xin chào, Teacher");
        }
        
        // Set mouse click handlers for cards
        if (cardQuestions != null) {
            cardQuestions.setOnMouseClicked(this::handleManageQuestions);
        }
        if (cardQuizzes != null) {
            cardQuizzes.setOnMouseClicked(this::handleManageQuizzes);
        }
        if (cardResults != null) {
            cardResults.setOnMouseClicked(this::handleViewResults);
        }
    }
    
    /**
     * Handle Manage Questions card click - Navigate to Create Question Screen
     */
    private void handleManageQuestions(MouseEvent event) {
        NavigationManager.getInstance().navigateTo(AppScreen.CREATE_QUESTION);
    }
    
    /**
     * Close the modal dialog and remove blur effect
     */
    public void closeModal() {
        // Hide dialog container
        dialogContainer.setVisible(false);
        
        // Clear dialog content
        dialogContainer.getChildren().clear();
        
        // Remove blur effect from main content
        mainContent.setEffect(null);
    }
    
    /**
     * Handle Manage Quizzes card click - Navigate to Quiz List Screen
     */
    private void handleManageQuizzes(MouseEvent event) {
        NavigationManager.getInstance().navigateTo(AppScreen.QUIZ_LIST);
    }
    
    /**
     * Handle View Results card click - Navigate to Quiz Results List Screen
     * (Shows list of quizzes, then user can select a quiz to view student results)
     */
    private void handleViewResults(MouseEvent event) {
        NavigationManager.getInstance().navigateTo(AppScreen.QUIZ_RESULTS_LIST);
    }
    
    /**
     * Handle logout button click
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

