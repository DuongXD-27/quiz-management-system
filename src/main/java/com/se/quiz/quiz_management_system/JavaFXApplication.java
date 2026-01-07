package com.se.quiz.quiz_management_system;

import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * JavaFX Application entry point
 * Integrates JavaFX with Spring Boot
 */
public class JavaFXApplication extends Application {
    
    private static ConfigurableApplicationContext springContext;
    
    @Override
    public void init() throws Exception {
        // Start Spring Boot context
        springContext = SpringApplication.run(QuizManagementSystemApplication.class);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize NavigationManager with primary stage and Spring context
        NavigationManager.getInstance().initialize(primaryStage, springContext);
        
        // Configure primary stage - maximized (full screen)
        primaryStage.setMaximized(true);
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        primaryStage.setResizable(true);
        
        // Navigate to Login screen (first screen)
        NavigationManager.getInstance().navigateTo(AppScreen.LOGIN);
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

