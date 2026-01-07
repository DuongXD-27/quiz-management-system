package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Student Dashboard view
 */
public class StudentDashboardController implements Initializable {
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private VBox cardTakeQuiz;
    
    @FXML
    private VBox cardMyResults;
    
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
            lblWelcome.setText("Xin chào, Student");
        }
        
        // Set mouse click handlers for cards
        if (cardTakeQuiz != null) {
            cardTakeQuiz.setOnMouseClicked(this::handleTakeQuiz);
        }
        if (cardMyResults != null) {
            cardMyResults.setOnMouseClicked(this::handleMyResults);
        }
    }
    
    /**
     * Handle Take Quiz card click
     */
    private void handleTakeQuiz(MouseEvent event) {
        NavigationManager.getInstance().navigateTo(AppScreen.AVAILABLE_QUIZZES);
    }
    
    /**
     * Handle My Results card click - Navigate to Student My Results Screen
     */
    private void handleMyResults(MouseEvent event) {
        NavigationManager.getInstance().navigateTo(AppScreen.STUDENT_MY_RESULTS);
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

