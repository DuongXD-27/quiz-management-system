package com.se.quiz.quiz_management_system;

import com.se.quiz.quiz_management_system.controller.LoginController;
import com.se.quiz.quiz_management_system.service.AuthService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * JavaFX Application entry point
 * Integrates JavaFX with Spring Boot
 */
public class JavaFXApplication extends Application {
    
    private static ConfigurableApplicationContext springContext;
    private Parent root;
    
    @Override
    public void init() throws Exception {
        // Start Spring Boot context
        springContext = SpringApplication.run(QuizManagementSystemApplication.class);
        
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
        root = fxmlLoader.load();
        
        // Manually inject AuthService into LoginController
        LoginController controller = fxmlLoader.getController();
        AuthService authService = springContext.getBean(AuthService.class);
        controller.setAuthService(authService);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Quiz Management System - Login");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        // Close Spring context when JavaFX application closes
        if (springContext != null) {
            springContext.close();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

