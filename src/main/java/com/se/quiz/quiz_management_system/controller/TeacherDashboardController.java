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
     * Handle Manage Questions card click - Open Create Question Modal
     */
    private void handleManageQuestions(MouseEvent event) {
        try {
            // Load CreateQuestion.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateQuestion.fxml"));
            Parent createQuestionRoot = loader.load();
            
            // Get CreateQuestionController and set parent controller reference
            CreateQuestionController createQuestionController = loader.getController();
            createQuestionController.setParentController(this);
            
            // Clear dialog container and add the form
            dialogContainer.getChildren().clear();
            dialogContainer.getChildren().add(createQuestionRoot);
            
            // Show dialog container
            dialogContainer.setVisible(true);
            
            // Apply blur effect to main content
            BoxBlur blur = new BoxBlur(10, 10, 3);
            mainContent.setEffect(blur);
            
        } catch (Exception e) {
            JavaFXHelper.showError("Lỗi hệ thống", "Không thể mở form tạo câu hỏi: " + e.getMessage());
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
     * Handle Manage Quizzes card click
     */
    private void handleManageQuizzes(MouseEvent event) {
        System.out.println("Clicked: Manage Quizzes");
        // TODO: Navigate to Manage Quizzes screen
        JavaFXHelper.showInfo("Manage Quizzes", "Chức năng quản lý bài kiểm tra sẽ được triển khai sau");
    }
    
    /**
     * Handle View Results card click
     */
    private void handleViewResults(MouseEvent event) {
        System.out.println("Clicked: View Results");
        // TODO: Navigate to View Results screen
        JavaFXHelper.showInfo("View Results", "Chức năng xem kết quả sẽ được triển khai sau");
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

