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
 * Controller for the Add Student to Quiz view
 * Allows teachers to assign students to a specific quiz
 */
public class AddStudentToQuizController implements Initializable {
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private Button btnBackToDashboard;
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private TextField txtUsername;
    
    @FXML
    private Button btnAddStudent;
    
    @FXML
    private Button btnAddAll;
    
    @FXML
    private TableView<Student> tblAssignedStudents;
    
    @FXML
    private TableColumn<Student, String> colUsername;
    
    @FXML
    private TableColumn<Student, Void> colAction;
    
    private AuthService authService;
    
    private ObservableList<Student> assignedStudents;
    
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
        
        // Load mock student data
        loadMockData();
    }
    
    /**
     * Set up table columns with cell value factories and custom cell factories
     */
    private void setupTableColumns() {
        // Username column - bind to username property
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        // Action column - custom cell factory to render Remove buttons
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");
            
            {
                // Style the button
                removeButton.getStyleClass().add("remove-button");
                
                // Set button action
                removeButton.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    handleRemoveStudent(student);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }
    
    /**
     * Load mock student data
     */
    private void loadMockData() {
        // Create observable list for assigned students
        assignedStudents = FXCollections.observableArrayList();
        
        // Add mock data - one student for demonstration
        assignedStudents.add(new Student("j.smith"));
        
        // Set data to table
        tblAssignedStudents.setItems(assignedStudents);
    }
    
    /**
     * Handle Add Student button click
     */
    @FXML
    private void handleAddStudent() {
        // Get username from text field
        String username = txtUsername.getText().trim();
        
        // Validate input
        if (username.isEmpty()) {
            JavaFXHelper.showError("Validation Error", "Please enter a student username");
            return;
        }
        
        // Check if student already assigned
        for (Student student : assignedStudents) {
            if (student.getUsername().equalsIgnoreCase(username)) {
                JavaFXHelper.showError("Duplicate Entry", 
                    "Student \"" + username + "\" is already assigned to this quiz");
                return;
            }
        }
        
        // Add student to list
        assignedStudents.add(new Student(username));
        
        // Clear text field
        txtUsername.clear();
        
        // Show success message
        JavaFXHelper.showInfo("Success", 
            "Student \"" + username + "\" has been added to the quiz");
        
        System.out.println("Added student: " + username);
    }
    
    /**
     * Handle Add All Students button click
     */
    @FXML
    private void handleAddAll() {
        // TODO: Implement logic to add all students from a class or course
        // For now, show a placeholder message
        JavaFXHelper.showInfo("Add All Students", 
            "This feature will add all students from the selected class.\n\n" +
            "Implementation coming soon!");
    }
    
    /**
     * Handle Remove Student button click
     * @param student the student to remove
     */
    private void handleRemoveStudent(Student student) {
        // Confirm removal
        boolean confirmed = JavaFXHelper.showConfirmation("Remove Student", 
            "Are you sure you want to remove \"" + student.getUsername() + "\" from this quiz?");
        
        if (confirmed) {
            // Remove student from list
            assignedStudents.remove(student);
            
            // Show success message
            JavaFXHelper.showInfo("Removed", 
                "Student \"" + student.getUsername() + "\" has been removed from the quiz");
            
            System.out.println("Removed student: " + student.getUsername());
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
     * Student class - represents a student assigned to the quiz
     */
    public static class Student {
        private final StringProperty username;
        
        public Student(String username) {
            this.username = new SimpleStringProperty(username);
        }
        
        public String getUsername() {
            return username.get();
        }
        
        public void setUsername(String username) {
            this.username.set(username);
        }
        
        public StringProperty usernameProperty() {
            return username;
        }
    }
}
