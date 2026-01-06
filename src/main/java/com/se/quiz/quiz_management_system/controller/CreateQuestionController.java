package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Create Question view
 * Allows teachers to create new questions and quizzes
 */
public class CreateQuestionController implements Initializable {
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private TextField txtQuizName;
    
    @FXML
    private TextField txtTimeLimit;
    
    @FXML
    private TextArea txtQuestionContent;
    
    @FXML
    private RadioButton rbOptionA;
    
    @FXML
    private RadioButton rbOptionB;
    
    @FXML
    private RadioButton rbOptionC;
    
    @FXML
    private RadioButton rbOptionD;
    
    @FXML
    private TextField txtOptionA;
    
    @FXML
    private TextField txtOptionB;
    
    @FXML
    private TextField txtOptionC;
    
    @FXML
    private TextField txtOptionD;
    
    @FXML
    private Button btnAddQuestion;
    
    @FXML
    private Button btnSaveQuiz;
    
    @FXML
    private Button btnBackToDashboard;
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private ToggleGroup answerGroup;
    
    private AuthService authService;
    
    // Temporary storage for questions added to the quiz
    private List<QuestionData> questionsList = new ArrayList<>();
    
    /**
     * Set the AuthService instance
     * @param authService the authentication service
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
        
        // Update welcome text if user is logged in
        if (authService != null && authService.getCurrentUser() != null) {
            lblWelcome.setText("Welcome, " + authService.getCurrentUser().getUsername());
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ToggleGroup is already defined in FXML, but ensure it's set
        if (answerGroup == null) {
            answerGroup = new ToggleGroup();
            rbOptionA.setToggleGroup(answerGroup);
            rbOptionB.setToggleGroup(answerGroup);
            rbOptionC.setToggleGroup(answerGroup);
            rbOptionD.setToggleGroup(answerGroup);
        }
        
        // Restrict time limit field to numbers only
        txtTimeLimit.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtTimeLimit.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    /**
     * Handle Add Question button click
     * Validates and temporarily stores the question
     */
    @FXML
    private void handleAddQuestion() {
        // Validate Quiz Name
        String quizName = txtQuizName.getText().trim();
        if (quizName.isEmpty()) {
            JavaFXHelper.showError("Validation Error", "Please enter the Quiz Name");
            return;
        }
        
        // Validate Time Limit
        String timeLimitStr = txtTimeLimit.getText().trim();
        if (timeLimitStr.isEmpty()) {
            JavaFXHelper.showError("Validation Error", "Please enter the Question Time Limit");
            return;
        }
        
        int timeLimit;
        try {
            timeLimit = Integer.parseInt(timeLimitStr);
            if (timeLimit <= 0) {
                JavaFXHelper.showError("Validation Error", "Time limit must be a positive number");
                return;
            }
        } catch (NumberFormatException e) {
            JavaFXHelper.showError("Validation Error", "Please enter a valid number for time limit");
            return;
        }
        
        // Validate Question Content
        String questionContent = txtQuestionContent.getText().trim();
        if (questionContent.isEmpty()) {
            JavaFXHelper.showError("Validation Error", "Please enter the Question Content");
            return;
        }
        
        // Validate Answer Options
        String optionA = txtOptionA.getText().trim();
        String optionB = txtOptionB.getText().trim();
        String optionC = txtOptionC.getText().trim();
        String optionD = txtOptionD.getText().trim();
        
        if (optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()) {
            JavaFXHelper.showError("Validation Error", "Please fill in all answer options (A, B, C, D)");
            return;
        }
        
        // Check if correct answer is selected
        RadioButton selectedRadio = (RadioButton) answerGroup.getSelectedToggle();
        if (selectedRadio == null) {
            JavaFXHelper.showError("Validation Error", "Please select the correct answer");
            return;
        }
        
        // Determine correct answer
        String correctAnswer = "";
        if (selectedRadio == rbOptionA) {
            correctAnswer = "A";
        } else if (selectedRadio == rbOptionB) {
            correctAnswer = "B";
        } else if (selectedRadio == rbOptionC) {
            correctAnswer = "C";
        } else if (selectedRadio == rbOptionD) {
            correctAnswer = "D";
        }
        
        // Create question data object and add to list
        QuestionData questionData = new QuestionData(
            quizName, timeLimit, questionContent,
            optionA, optionB, optionC, optionD, correctAnswer
        );
        questionsList.add(questionData);
        
        // Log for debugging (TODO: Replace with database insertion)
        System.out.println("Question Added:");
        System.out.println("  Quiz: " + quizName);
        System.out.println("  Time Limit: " + timeLimit + " minutes");
        System.out.println("  Question: " + questionContent);
        System.out.println("  Options: A=" + optionA + ", B=" + optionB + ", C=" + optionC + ", D=" + optionD);
        System.out.println("  Correct Answer: " + correctAnswer);
        System.out.println("  Total Questions in Quiz: " + questionsList.size());
        
        // Show success message
        JavaFXHelper.showInfo("Success", "Question added successfully!\nTotal questions: " + questionsList.size());
        
        // Clear only the question-specific fields (keep quiz name and time limit)
        clearQuestionFields();
    }
    
    /**
     * Handle Save Quiz button click
     * Saves all added questions as a complete quiz
     */
    @FXML
    private void handleSaveQuiz() {
        if (questionsList.isEmpty()) {
            JavaFXHelper.showError("Error", "Please add at least one question before saving the quiz");
            return;
        }
        
        // TODO: Implement database save logic here
        // For now, just show a summary
        String quizName = questionsList.get(0).quizName;
        int totalQuestions = questionsList.size();
        
        System.out.println("\n=== SAVING QUIZ ===");
        System.out.println("Quiz Name: " + quizName);
        System.out.println("Total Questions: " + totalQuestions);
        
        JavaFXHelper.showInfo("Quiz Saved", 
            "Quiz \"" + quizName + "\" has been saved successfully!\n" +
            "Total questions: " + totalQuestions);
        
        // Clear all data and return to dashboard
        questionsList.clear();
        clearAllFields();
        handleBackToDashboard();
    }
    
    /**
     * Handle Back to Dashboard button click
     */
    @FXML
    private void handleBackToDashboard() {
        try {
            // Load the Teacher Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TeacherDashboard.fxml"));
            Parent root = loader.load();
            
            // Pass AuthService to the dashboard controller
            TeacherDashboardController controller = loader.getController();
            if (authService != null) {
                controller.setAuthService(authService);
            }
            
            // Get current stage and set new scene
            Stage stage = (Stage) btnBackToDashboard.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            JavaFXHelper.showError("Navigation Error", "Failed to load Teacher Dashboard");
        }
    }
    
    /**
     * Clear only the question-specific fields
     */
    private void clearQuestionFields() {
        txtQuestionContent.clear();
        txtOptionA.clear();
        txtOptionB.clear();
        txtOptionC.clear();
        txtOptionD.clear();
        answerGroup.selectToggle(null);
    }
    
    /**
     * Clear all form fields
     */
    private void clearAllFields() {
        txtQuizName.clear();
        txtTimeLimit.clear();
        clearQuestionFields();
    }
    
    /**
     * Handle Logout button click
     * Navigates back to the Login screen
     */
    @FXML
    private void handleLogout() {
        try {
            // Load the Login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            
            // Pass AuthService to the login controller
            LoginController controller = loader.getController();
            if (authService != null) {
                controller.setAuthService(authService);
            }
            
            // Get current stage and set new scene
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Login");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            JavaFXHelper.showError("Navigation Error", "Failed to logout. Please try again.");
        }
    }
    
    /**
     * Inner class to store question data temporarily
     */
    private static class QuestionData {
        String quizName;
        int timeLimit;
        String questionContent;
        String optionA;
        String optionB;
        String optionC;
        String optionD;
        String correctAnswer;
        
        QuestionData(String quizName, int timeLimit, String questionContent,
                     String optionA, String optionB, String optionC, String optionD,
                     String correctAnswer) {
            this.quizName = quizName;
            this.timeLimit = timeLimit;
            this.questionContent = questionContent;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctAnswer = correctAnswer;
        }
    }
}
