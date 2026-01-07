package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.exception.AuthenticationException;
import com.se.quiz.quiz_management_system.model.UserSession;
import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
            
            // Get current user session
            UserSession session = SessionManager.getCurrentUserSession();
            if (session == null) {
                JavaFXHelper.showError("Lỗi hệ thống", "Không thể lấy thông tin phiên đăng nhập");
                return;
            }
            
            // Navigate to appropriate dashboard based on role
            String role = session.getRole().name();
            NavigationManager.getInstance().navigateToDashboard(role);
            
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
        NavigationManager.getInstance().navigateTo(AppScreen.REGISTER);
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

