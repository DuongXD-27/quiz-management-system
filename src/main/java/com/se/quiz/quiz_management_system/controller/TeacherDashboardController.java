package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        try {
            // Load CreateQuestion.fxml (Full Screen)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateQuestion.fxml"));
            Parent root = loader.load();
            
            // Get CreateQuestionController and pass AuthService
            CreateQuestionController controller = loader.getController();
            if (authService != null) {
                controller.setAuthService(authService);
            }
            
            // Get current stage and set new scene
            Stage stage = (Stage) cardQuestions.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            JavaFXHelper.showError("System Error", "Failed to load Create Question screen: " + e.getMessage());
            e.printStackTrace();
        }
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
        try {
            // Load QuizList.fxml (Full Screen)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/QuizList.fxml"));
            Parent root = loader.load();
            
            // Get QuizListController and pass AuthService
            QuizListController controller = loader.getController();
            if (authService != null) {
                controller.setAuthService(authService);
            }
            
            // Get current stage and set new scene
            Stage stage = (Stage) cardQuizzes.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Quiz List");
            stage.show();
            
        } catch (Exception e) {
            JavaFXHelper.showError("System Error", "Failed to load Quiz List screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle View Results card click - Navigate to Student Results Screen
     */
    private void handleViewResults(MouseEvent event) {
        try {
            // Load StudentResults.fxml (Full Screen)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentResults.fxml"));
            Parent root = loader.load();
            
            // Get StudentResultsController and pass data
            StudentResultsController controller = loader.getController();
            if (authService != null) {
                controller.setAuthService(authService);
            }
            
            // Set the quiz information (this would normally come from user selection)
            controller.setQuizInfo("Quiz 1: Basic");
            
            // Get current stage and set new scene
            Stage stage = (Stage) cardResults.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Student Results");
            stage.show();
            
        } catch (Exception e) {
            JavaFXHelper.showError("System Error", "Failed to load Student Results screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle logout button click
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
            
            // Navigate back to Login screen
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            
            // Inject AuthService into LoginController
            LoginController loginController = loader.getController();
            if (authService != null) {
                loginController.setAuthService(authService);
            }
            
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Login");
            
        } catch (Exception e) {
            JavaFXHelper.showError("Lỗi hệ thống", "Không thể đăng xuất: " + e.getMessage());
        }
    }
}

