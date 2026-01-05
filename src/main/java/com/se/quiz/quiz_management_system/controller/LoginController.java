package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.exception.AuthenticationException;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Login view
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField txtUsername;
    
    @FXML
    private PasswordField txtPassword;
    
    @FXML
    private Button btnLogin;
    
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
        // Initialize controller
    }
    
    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        
        // Validate input
        if (username.isEmpty()) {
            JavaFXHelper.showError("Lỗi đăng nhập", "Vui lòng nhập email hoặc username");
            return;
        }
        
        if (password.isEmpty()) {
            JavaFXHelper.showError("Lỗi đăng nhập", "Vui lòng nhập mật khẩu");
            return;
        }
        
        // Check if AuthService is available
        if (authService == null) {
            JavaFXHelper.showError("Lỗi hệ thống", "AuthService chưa được khởi tạo");
            return;
        }
        
        try {
            // Attempt login
            authService.login(username, password);
            
            // Login successful
            System.out.println("Login Success");
            JavaFXHelper.showInfo("Đăng nhập thành công", "Chào mừng bạn quay trở lại!");
            
            // TODO: Navigate to Dashboard
            // For now, just print to console as requested
            
        } catch (AuthenticationException e) {
            // Login failed
            JavaFXHelper.showError("Đăng nhập thất bại", e.getMessage());
        } catch (Exception e) {
            // Unexpected error
            JavaFXHelper.showError("Lỗi hệ thống", "Đã xảy ra lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Handle register link click - Navigate to Register screen
     */
    @FXML
    private void handleRegister() {
        navigateToRegister();
    }
    
    /**
     * Navigate to Register screen
     */
    private void navigateToRegister() {
        try {
            // Get current stage
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            
            // Load Register FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Register.fxml"));
            Parent root = loader.load();
            
            // Inject AuthService into RegisterController
            RegisterController registerController = loader.getController();
            if (authService != null) {
                registerController.setAuthService(authService);
            }
            
            // Create new scene and set it
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Register");
            
        } catch (Exception e) {
            JavaFXHelper.showError("Lỗi hệ thống", "Không thể chuyển đến màn hình đăng ký: " + e.getMessage());
        }
    }
    
    /**
     * Handle forgot password link click (placeholder)
     */
    @FXML
    private void handleForgotPassword() {
        // TODO: Navigate to Forgot Password screen
        JavaFXHelper.showInfo("Quên mật khẩu", "Chức năng quên mật khẩu sẽ được triển khai sau");
    }
}

