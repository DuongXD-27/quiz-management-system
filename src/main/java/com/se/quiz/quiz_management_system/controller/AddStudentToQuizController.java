package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.entity.Student;
import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.navigation.NavigationAware;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.service.QuizService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the Add Student to Quiz view
 * Allows teachers to assign students to a specific quiz
 */
public class AddStudentToQuizController implements Initializable, NavigationAware {
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private Label lblQuizName;
    
    @FXML
    private Button btnBackToDashboard;
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private TextField txtUsername;
    
    @FXML
    private Button btnAddStudent;
    
    @FXML
    private TableView<StudentModel> tblAssignedStudents;
    
    @FXML
    private TableColumn<StudentModel, String> colUsername;
    
    @FXML
    private TableColumn<StudentModel, Void> colAction;
    
    private AuthService authService;
    private QuizService quizService;
    
    private ObservableList<StudentModel> assignedStudents;
    
    // Current quiz ID (passed from navigation)
    private Long currentQuizId;
    
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
    
    /**
     * Set the QuizService instance (injected from Spring context)
     * @param quizService the quiz service
     */
    public void setQuizService(QuizService quizService) {
        this.quizService = quizService;
        
        // Reload assigned students when service is injected
        if (currentQuizId != null && tblAssignedStudents != null) {
            loadAssignedStudents();
        }
    }
    
    /**
     * Called when navigating to this screen
     * Receives data from previous screen (e.g., quiz ID)
     */
    @Override
    public void onNavigatedTo(Map<String, Object> data) {
        if (data != null && data.containsKey("quizId")) {
            this.currentQuizId = (Long) data.get("quizId");
            
            // Update UI with quiz info
            if (data.containsKey("quizName")) {
                String quizName = (String) data.get("quizName");
                if (lblQuizName != null) {
                    lblQuizName.setText("Quiz: " + quizName);
                }
            }
            
            // Load assigned students if service is ready
            if (quizService != null) {
                loadAssignedStudents();
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
        
        // Initialize empty list (will be populated when quiz ID is set via onNavigatedTo)
        assignedStudents = FXCollections.observableArrayList();
        tblAssignedStudents.setItems(assignedStudents);
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
                    StudentModel student = getTableView().getItems().get(getIndex());
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
     * Load assigned students from database
     */
    private void loadAssignedStudents() {
        try {
            // Check if QuizService and quizId are available
            if (quizService == null || currentQuizId == null) {
                System.out.println("QuizService or quizId not available, using empty data");
                assignedStudents = FXCollections.observableArrayList();
                tblAssignedStudents.setItems(assignedStudents);
                return;
            }
            
            // Fetch assigned students from database
            List<Student> students = quizService.getStudentsForQuiz(currentQuizId);
            
            // Convert to StudentModel
            ObservableList<StudentModel> modelList = FXCollections.observableArrayList();
            for (Student student : students) {
                modelList.add(new StudentModel(
                    student.getStudentId(),
                    student.getUsername()
                ));
            }
            
            // Set data to table
            assignedStudents = modelList;
            tblAssignedStudents.setItems(assignedStudents);
            
            System.out.println("Loaded " + students.size() + " assigned students for quiz ID " + currentQuizId);
            
        } catch (Exception e) {
            e.printStackTrace();
            JavaFXHelper.showError("Data Load Error", 
                "Failed to load assigned students: " + e.getMessage());
            
            // Set empty list on error
            assignedStudents = FXCollections.observableArrayList();
            tblAssignedStudents.setItems(assignedStudents);
        }
    }
    
    /**
     * Handle Add Student button click
     * CRITICAL: This now saves to database via QuizService
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
        
        // Validate quizId
        if (currentQuizId == null) {
            JavaFXHelper.showError("Error", "No quiz selected. Please return to quiz list and try again.");
            return;
        }
        
        // Check if QuizService is available
        if (quizService == null) {
            JavaFXHelper.showError("Service Error", "Quiz service is not available. Please try again.");
            return;
        }
        
        try {
            // ✅ CRITICAL: Save to database (INSERT INTO student_quiz)
            boolean success = quizService.assignQuizToStudent(currentQuizId, username);
            
            if (success) {
                // Clear text field
                txtUsername.clear();
                
                // Reload assigned students list
                loadAssignedStudents();
                
                // Show success message
                JavaFXHelper.showInfo("Success", 
                    "Student \"" + username + "\" has been assigned to this quiz");
                
                System.out.println("Assigned student '" + username + "' to quiz ID " + currentQuizId);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            
            // Show user-friendly error message
            String errorMessage = e.getMessage();
            if (errorMessage.contains("not found with username")) {
                JavaFXHelper.showError("Student Not Found", 
                    "No student found with username: " + username);
            } else if (errorMessage.contains("already assigned")) {
                JavaFXHelper.showError("Already Assigned", 
                    "Student \"" + username + "\" is already assigned to this quiz");
            } else {
                JavaFXHelper.showError("Assignment Error", 
                    "Failed to assign student: " + errorMessage);
            }
        }
    }
    
    /**
     * Handle Remove Student button click
     * CRITICAL: This now deletes from database
     * @param student the student to remove
     */
    private void handleRemoveStudent(StudentModel student) {
        // Confirm removal
        boolean confirmed = JavaFXHelper.showConfirmation("Remove Student", 
            "Are you sure you want to remove \"" + student.getUsername() + "\" from this quiz?");
        
        if (confirmed) {
            try {
                // ✅ CRITICAL: Delete from database
                boolean success = quizService.removeStudentFromQuiz(currentQuizId, student.getUsername());
                
                if (success) {
                    // Reload list from database
                    loadAssignedStudents();
                    
                    // Show success message
                    JavaFXHelper.showInfo("Removed", 
                        "Student \"" + student.getUsername() + "\" has been removed from the quiz");
                    
                    System.out.println("Removed student: " + student.getUsername());
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JavaFXHelper.showError("Removal Error", 
                    "Failed to remove student: " + e.getMessage());
            }
        }
    }
    
    /**
     * Handle Back to Dashboard button click
     * Uses NavigationManager to preserve window state
     */
    @FXML
    private void handleBackToDashboard() {
        NavigationManager.getInstance().navigateTo(AppScreen.TEACHER_DASHBOARD);
    }
    
    /**
     * Handle Logout button click
     */
    @FXML
    private void handleLogout() {
        // Clear session
        if (authService != null) {
            authService.logout();
        } else {
            SessionManager.clearSession();
        }
        
        // Navigate to Login using NavigationManager
        NavigationManager.getInstance().navigateToLogin();
    }
    
    /**
     * StudentModel class - represents a student assigned to the quiz
     * Wrapper class for Student entity with JavaFX properties
     */
    public static class StudentModel {
        private final SimpleLongProperty studentId;
        private final SimpleStringProperty username;
        
        public StudentModel(Long studentId, String username) {
            this.studentId = new SimpleLongProperty(studentId != null ? studentId : 0L);
            this.username = new SimpleStringProperty(username != null ? username : "");
        }
        
        public Long getStudentId() {
            return studentId.get();
        }
        
        public void setStudentId(Long studentId) {
            this.studentId.set(studentId);
        }
        
        public SimpleLongProperty studentIdProperty() {
            return studentId;
        }
        
        public String getUsername() {
            return username.get();
        }
        
        public void setUsername(String username) {
            this.username.set(username);
        }
        
        public SimpleStringProperty usernameProperty() {
            return username;
        }
    }
}
