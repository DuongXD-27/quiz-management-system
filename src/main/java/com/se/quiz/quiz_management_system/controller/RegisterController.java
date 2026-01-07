package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.exception.DuplicateUsernameException;
import com.se.quiz.quiz_management_system.model.Role;
import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

// Controller for the Register view

public class RegisterController implements Initializable {
    
    @FXML
    private TextField txtUsername;
    
    @FXML
    private TextField txtFullName;
    
    @FXML
    private PasswordField txtPassword;
    
    @FXML
    private ComboBox<String> comboRole;
    
    @FXML
    private Button btnSignUp;
    
    private AuthService authService;
    
    // Set the AuthService instance (injected from Spring context)
    // @param authService the AuthService instance
    
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ComboBox with role options
        comboRole.getItems().addAll("TEACHER", "STUDENT");
        comboRole.setValue("TEACHER"); // Set default value
    }
    
    // Handle sign up button click
    
    @FXML
    private void handleSignUp() {
        String username = txtUsername.getText().trim();
        String fullName = txtFullName.getText().trim();
        String password = txtPassword.getText();
        String selectedRole = comboRole.getValue();
        
        // Validate input
        if (username.isEmpty()) {
            JavaFXHelper.showError("Registration error", "Vui lòng nhập username");
            return;
        }
        
        if (fullName.isEmpty()) {
            JavaFXHelper.showError("Registration error", "Please enter your full name");
            return;
        }
        
        if (password.isEmpty()) {
            JavaFXHelper.showError("Registration error", "Please enter your password");
            return;
        }
        
        if (selectedRole == null || selectedRole.isEmpty()) {
            JavaFXHelper.showError("Registration error", "Please select a role");
            return;
        }
        
        // Check if AuthService is available
        if (authService == null) {
            JavaFXHelper.showError("System error", "AuthService is not initialized");
            return;
        }
        
        try {
            // Convert string role to Role enum
            Role role = selectedRole.equals("TEACHER") ? Role.LECTURER : Role.STUDENT;
            
            // Attempt registration
            authService.register(username, password, fullName, role);
            
            // Registration successful
            JavaFXHelper.showInfo("Registration successful", "Your account has been created successfully!");
            
            // Navigate back to Login screen
            navigateToLogin();
            
        } catch (DuplicateUsernameException e) {
            // Registration failed - duplicate username
            JavaFXHelper.showError("Registration failed", e.getMessage());
        } catch (Exception e) {
            // Unexpected error
            JavaFXHelper.showError("System error", "An error occurred: " + e.getMessage());
        }
    }
    
    // Handle login link click - Navigate to Login screen
    
    @FXML
    private void handleLogin() {
        navigateToLogin();
    }
    
    // Navigate to Login screen
    // Uses NavigationManager to preserve window state
    
    private void navigateToLogin() {
        NavigationManager.getInstance().navigateTo(AppScreen.LOGIN);
    }
}

