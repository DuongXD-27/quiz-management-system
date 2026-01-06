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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        try {
            // Get current stage
            Stage stage = (Stage) cardTakeQuiz.getScene().getWindow();
            
            // Load Available Quizzes FXML (Student View)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AvailableQuizzes.fxml"));
            Parent root = loader.load();
            
            // Inject AuthService into AvailableQuizzesController for navigation back
            AvailableQuizzesController availableQuizzesController = loader.getController();
            if (authService != null) {
                availableQuizzesController.setAuthService(authService);
            }
            
            // Create new scene and set it
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Available Quizzes");
            
        } catch (Exception e) {
            JavaFXHelper.showError("Lỗi hệ thống", "Không thể mở danh sách bài kiểm tra: " + e.getMessage());
        }
    }
    
    /**
     * Handle My Results card click - Navigate to Student My Results Screen
     */
    private void handleMyResults(MouseEvent event) {
        try {
            // Load StudentMyResults.fxml (Full Screen)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StudentMyResults.fxml"));
            Parent root = loader.load();
            
            // Get StudentMyResultsController and pass AuthService
            StudentMyResultsController controller = loader.getController();
            if (authService != null) {
                controller.setAuthService(authService);
            }
            
            // Get current stage and set new scene
            Stage stage = (Stage) cardMyResults.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - My Results");
            stage.show();
            
        } catch (Exception e) {
            JavaFXHelper.showError("System Error", "Failed to load My Results screen: " + e.getMessage());
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

