package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the Student Results screen
 * Displays student scores for a specific quiz with CSV export functionality
 */
public class StudentResultsController implements Initializable {
    
    @FXML
    private Label lblPageTitle;
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private Button btnBackToDashboard;
    
    @FXML
    private Button btnExportCSV;
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private VBox studentResultsContainer;
    
    private AuthService authService;
    
    private String currentQuizName = "Quiz 1: Basic";
    
    private List<StudentResult> studentResults;
    
    /**
     * Set the AuthService instance
     * @param authService the authentication service
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
        
        // Update welcome text if user is logged in
        if (authService != null && authService.getCurrentUser() != null) {
            lblWelcome.setText("Welcome, " + authService.getCurrentUser().getUsername());
        } else {
            String username = SessionManager.getCurrentUsername();
            if (username != null) {
                lblWelcome.setText("Welcome, " + username);
            }
        }
    }
    
    /**
     * Set the quiz name and update the UI title
     * @param quizName the name of the quiz
     */
    public void setQuizName(String quizName) {
        this.currentQuizName = quizName;
        lblPageTitle.setText(quizName + " - Student Results");
    }
    
    /**
     * Set the quiz information and update the UI (alias for setQuizName)
     * @param quizName the name of the quiz
     */
    public void setQuizInfo(String quizName) {
        setQuizName(quizName);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up welcome text
        String username = SessionManager.getCurrentUsername();
        if (username != null) {
            lblWelcome.setText("Welcome, " + username);
        } else {
            lblWelcome.setText("Welcome,");
        }
        
        // Set the quiz name dynamically in title (with default value)
        setQuizName("Quiz 1: Basic");
        
        // Load student results data
        loadStudentResults();
    }
    
    /**
     * Load student results and create dynamic cards
     */
    private void loadStudentResults() {
        // Generate mock data
        studentResults = generateMockData();
        
        // Clear existing children (if any)
        studentResultsContainer.getChildren().clear();
        
        // Create a card for each student result
        for (StudentResult result : studentResults) {
            HBox card = createStudentResultCard(result);
            studentResultsContainer.getChildren().add(card);
        }
        
        System.out.println("Loaded " + studentResults.size() + " student results for: " + currentQuizName);
    }
    
    /**
     * Generate mock student result data
     * @return List of StudentResult objects
     */
    private List<StudentResult> generateMockData() {
        List<StudentResult> results = new ArrayList<>();
        
        results.add(new StudentResult("John Smith", "85/100"));
        results.add(new StudentResult("Emily Davis", "92/100"));
        results.add(new StudentResult("Michael Brown", "78/100"));
        
        return results;
    }
    
    /**
     * Create a student result card (HBox)
     * @param result the student result data
     * @return HBox representing the student result card
     */
    private HBox createStudentResultCard(StudentResult result) {
        // Main card container
        HBox card = new HBox();
        card.getStyleClass().add("student-result-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(20);
        card.setPrefWidth(900);
        card.setMaxWidth(900);
        
        // Student Name Label
        Label nameLabel = new Label(result.getStudentName());
        nameLabel.setPrefWidth(600);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: #2D3447;");
        
        // Score Label
        Label scoreLabel = new Label(result.getScore());
        scoreLabel.setPrefWidth(300);
        scoreLabel.setAlignment(Pos.CENTER_LEFT);
        scoreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #2D3447;");
        
        // Add all elements to card
        card.getChildren().addAll(nameLabel, scoreLabel);
        
        return card;
    }
    
    /**
     * Handle Export to CSV button click
     */
    @FXML
    private void handleExportCSV() {
        try {
            // Create FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Student Results to CSV");
            fileChooser.setInitialFileName(currentQuizName.replaceAll("[^a-zA-Z0-9]", "_") + "_results.csv");
            
            // Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            
            // Show save dialog
            Stage stage = (Stage) btnExportCSV.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            
            if (file != null) {
                // Write CSV content
                writeCSVFile(file);
                
                // Show success message
                JavaFXHelper.showInfo("Export Successful", 
                    "Student results have been exported successfully to:\n" + file.getAbsolutePath());
                
                System.out.println("Exported results to: " + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JavaFXHelper.showError("Export Error", 
                "Failed to export CSV file: " + e.getMessage());
        }
    }
    
    /**
     * Write student results to CSV file
     * @param file the file to write to
     * @throws IOException if writing fails
     */
    private void writeCSVFile(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // Write CSV header
            writer.write("Quiz Name,Username,Score\n");
            
            // Write data rows
            for (StudentResult result : studentResults) {
                writer.write(currentQuizName + ",");
                writer.write(result.getStudentName() + ",");
                writer.write(result.getScore() + "\n");
            }
            
            writer.flush();
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
     * StudentResult - represents a student's quiz result
     */
    private static class StudentResult {
        private final String studentName;
        private final String score;
        
        public StudentResult(String studentName, String score) {
            this.studentName = studentName;
            this.score = score;
        }
        
        public String getStudentName() {
            return studentName;
        }
        
        public String getScore() {
            return score;
        }
    }
}
