package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.entity.Question;
import com.se.quiz.quiz_management_system.entity.Quiz;
import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.service.QuizService;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// Controller for the Create Question view
// Allows teachers to create new questions and quizzes

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
    
    @FXML
    private Label lblQuestionCount;
    
    private AuthService authService;
    private QuizService quizService;
    
    // Temporary storage for questions added to the quiz
    private List<QuestionData> questionsList = new ArrayList<>();
    
    // Set the AuthService instance
    // @param authService the authentication service
    
    public void setAuthService(AuthService authService) {
        this.authService = authService;
        
        // Update welcome text if user is logged in
        if (authService != null && authService.getCurrentUser() != null) {
            lblWelcome.setText("Welcome, " + authService.getCurrentUser().getUsername());
        }
    }
    
    // Set the QuizService instance (injected from Spring context)
    // @param quizService the quiz service
    
    public void setQuizService(QuizService quizService) {
        this.quizService = quizService;
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
    
    // Handle Add Question button click
    // Validates and temporarily stores the question
    
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
        
        // Update question count display
        updateQuestionCountLabel();
        
        // Show success message
        JavaFXHelper.showInfo("Success", "Question added successfully!\nTotal questions: " + questionsList.size());
        
        // Clear only the question-specific fields (keep quiz name and time limit)
        clearQuestionFields();
    }
    
    // Handle Save Quiz button click
    // Saves all added questions as a complete quiz
    
    @FXML
    private void handleSaveQuiz() {
        // Validate that at least one question has been added
        if (questionsList.isEmpty()) {
            JavaFXHelper.showError("Validation Error", "Please add at least one question before saving the quiz");
            return;
        }
        
        // Check if QuizService is available
        if (quizService == null) {
            JavaFXHelper.showError("Service Error", "Quiz service is not available. Please try again.");
            return;
        }
        
        try {
            // Get quiz information from the first question (all have same quiz name and time limit)
            String quizName = questionsList.get(0).quizName;
            int timeLimit = questionsList.get(0).timeLimit;
            
            // Convert QuestionData objects to Question entities
            List<Question> questions = new ArrayList<>();
            for (QuestionData qData : questionsList) {
                Question question = new Question(
                    qData.questionContent,
                    qData.optionA,
                    qData.optionB,
                    qData.optionC,
                    qData.optionD,
                    qData.correctAnswer
                );
                questions.add(question);
            }
            
            // Save quiz with all questions in a single transaction
            Quiz savedQuiz = quizService.createQuizWithQuestions(quizName, timeLimit, questions);
            
            // Show success message
            JavaFXHelper.showInfo("Success", 
                "Quiz \"" + savedQuiz.getQuizName() + "\" has been saved successfully!\n" +
                "Total questions: " + savedQuiz.getNumberOfQuestion() + "\n" +
                "Quiz ID: " + savedQuiz.getQuizId());
            
            // Clear all data
            questionsList.clear();
            clearAllFields();
            updateQuestionCountLabel();
            
            // Navigate to Quiz List to see the newly created quiz
            NavigationManager.getInstance().navigateTo(AppScreen.QUIZ_LIST);
            
        } catch (Exception e) {
            e.printStackTrace();
            JavaFXHelper.showError("Database Error", 
                "Failed to save quiz: " + e.getMessage() + "\n" +
                "Please check your database connection and try again.");
        }
    }
    
    // Handle Back to Dashboard button click
    // Uses NavigationManager to preserve window state
    
    @FXML
    private void handleBackToDashboard() {
        NavigationManager.getInstance().navigateTo(AppScreen.TEACHER_DASHBOARD);
    }
    
    // Clear only the question-specific fields
    
    private void clearQuestionFields() {
        txtQuestionContent.clear();
        txtOptionA.clear();
        txtOptionB.clear();
        txtOptionC.clear();
        txtOptionD.clear();
        answerGroup.selectToggle(null);
    }
    
    // Clear all form fields
    
    private void clearAllFields() {
        txtQuizName.clear();
        txtTimeLimit.clear();
        clearQuestionFields();
    }
    
    // Update the question count label
    
    private void updateQuestionCountLabel() {
        if (lblQuestionCount != null) {
            lblQuestionCount.setText("Questions added: " + questionsList.size());
        }
    }
    
    // Handle Logout button click
    // Clears session and navigates back to Login screen
    // Uses NavigationManager to preserve window state
    
    @FXML
    private void handleLogout() {
        // Clear session if authService available
        if (authService != null) {
            authService.logout();
        }
        
        // Navigate to Login using NavigationManager
        NavigationManager.getInstance().navigateToLogin();
    }
    
    // Inner class to store question data temporarily
    
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
