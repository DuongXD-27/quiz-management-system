package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Quiz List view (Teacher)
 * Displays quizzes in a TableView with action buttons
 */
public class QuizListController implements Initializable {
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private Button btnBackToDashboard;
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private TableView<QuizModel> tblQuizzes;
    
    @FXML
    private TableColumn<QuizModel, String> colQuizName;
    
    @FXML
    private TableColumn<QuizModel, Void> colAction;
    
    private AuthService authService;
    
    private ObservableList<QuizModel> quizData;
    
    /**
     * Set the AuthService instance
     * @param authService the authentication service
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
        
        // Update welcome text if user is logged in
        if (authService != null && authService.getCurrentUser() != null) {
            lblWelcome.setText("Xin chào, " + authService.getCurrentUser().getUsername());
        } else {
            String username = SessionManager.getCurrentUsername();
            if (username != null) {
                lblWelcome.setText("Xin chào, " + username);
            }
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up welcome text
        String username = SessionManager.getCurrentUsername();
        if (username != null) {
            lblWelcome.setText("Xin chào, " + username);
        } else {
            lblWelcome.setText("Xin chào,");
        }
        
        // Set up table columns
        setupTableColumns();
        
        // Load quiz data
        loadQuizData();
    }
    
    /**
     * Set up table columns with cell value factories and custom cell factories
     */
    private void setupTableColumns() {
        // Quiz Name column - bind to name property
        colQuizName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Action column - custom cell factory to render buttons
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button assignButton = new Button("Assign to Students");
            
            {
                // Style the button
                assignButton.getStyleClass().add("assign-button");
                
                // Set button action
                assignButton.setOnAction(event -> {
                    QuizModel quiz = getTableView().getItems().get(getIndex());
                    handleAssignToStudents(quiz);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(assignButton);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }
    
    /**
     * Load quiz data into the table
     */
    private void loadQuizData() {
        // Create mock quiz data
        quizData = FXCollections.observableArrayList(
            new QuizModel("Quiz 1: Basic"),
            new QuizModel("Quiz 2: Basic"),
            new QuizModel("Quiz 3: Basic"),
            new QuizModel("Quiz 4: Basic")
        );
        
        // Set data to table
        tblQuizzes.setItems(quizData);
    }
    
    /**
     * Handle Assign to Students button click
     * @param quiz the selected quiz
     */
    private void handleAssignToStudents(QuizModel quiz) {
        System.out.println("Assigning quiz to students: " + quiz.getName());
        
        try {
            // Load Add Student to Quiz screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddStudentToQuiz.fxml"));
            Parent root = loader.load();
            
            // Pass AuthService to the controller
            AddStudentToQuizController controller = loader.getController();
            if (authService != null) {
                controller.setAuthService(authService);
            }
            
            // Get current stage and set new scene
            Stage stage = (Stage) tblQuizzes.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quiz Management System - Add Students to Quiz");
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            JavaFXHelper.showError("Navigation Error", 
                "Failed to load Add Students screen: " + e.getMessage());
        }
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
            stage.setTitle("Quiz Management System - Teacher Dashboard");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            JavaFXHelper.showError("Navigation Error", "Failed to load Teacher Dashboard");
        }
    }
    
    /**
     * Handle Logout button click
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
            JavaFXHelper.showError("Logout Error", "Failed to logout. Please try again.");
        }
    }
    
    /**
     * QuizModel class - represents a quiz in the table
     */
    public static class QuizModel {
        private final StringProperty name;
        
        public QuizModel(String name) {
            this.name = new SimpleStringProperty(name);
        }
        
        public String getName() {
            return name.get();
        }
        
        public void setName(String name) {
            this.name.set(name);
        }
        
        public StringProperty nameProperty() {
            return name;
        }
    }
}
