package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.entity.Student;
import com.se.quiz.quiz_management_system.entity.StudentQuizResult;
import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationAware;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.service.ResultService;
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
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the Student Results screen
 * Displays student scores for a specific quiz with CSV export functionality
 */
public class StudentResultsController implements Initializable, NavigationAware {
    
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
    private ResultService resultService;
    
    private String currentQuizName = "Quiz 1: Basic";
    private Long currentQuizId; // Store quiz ID from navigation data
    
    private List<StudentResult> studentResults;
    private List<StudentQuizResult> dbResults; // Store database results for CSV export
    
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
     * Set the ResultService instance (injected from Spring context)
     * @param resultService the result service
     */
    public void setResultService(ResultService resultService) {
        this.resultService = resultService;
        
        // Reload results if quiz ID is already set
        if (currentQuizId != null && studentResultsContainer != null) {
            loadStudentResults();
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
    
    /**
     * Called when navigated to this screen
     * Receives quiz data from previous screen (QuizResultsListController)
     */
    @Override
    public void onNavigatedTo(Map<String, Object> data) {
        if (data != null) {
            // Get quiz ID and store it
            if (data.containsKey("quizId")) {
                this.currentQuizId = (Long) data.get("quizId");
                System.out.println("StudentResultsController received Quiz ID: " + currentQuizId);
                
                // Load real student results from database
                if (resultService != null && studentResultsContainer != null) {
                    loadStudentResults();
                }
            }
            
            // Get quiz name and update UI
            if (data.containsKey("quizName")) {
                String quizName = (String) data.get("quizName");
                setQuizName(quizName);
                System.out.println("Displaying results for quiz: " + quizName);
            }
        }
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
        // Will be overridden by onNavigatedTo if data is passed
        setQuizName("Quiz 1: Basic");
        
        // Load student results data
        loadStudentResults();
    }
    
    /**
     * Load student results and create dynamic cards
     * ✅ CRITICAL: Loads REAL data from database
     */
    private void loadStudentResults() {
        // Clear existing children (if any)
        studentResultsContainer.getChildren().clear();
        
        // Check if services and data are available
        if (resultService == null) {
            System.out.println("ResultService not yet injected, showing empty state");
            showEmptyState("Service not available", "Please try again later.");
            return;
        }
        
        if (currentQuizId == null) {
            System.out.println("Quiz ID not set, showing empty state");
            showEmptyState("No quiz selected", "Please select a quiz to view results.");
            return;
        }
        
        try {
            // ═══════════════════════════════════════════════════════════
            // CRITICAL: LOAD REAL RESULTS FROM DATABASE
            // ═══════════════════════════════════════════════════════════
            this.dbResults = resultService.getResultsByQuizId(currentQuizId);
            
            if (dbResults == null || dbResults.isEmpty()) {
                System.out.println("No results found for quiz ID: " + currentQuizId);
                showEmptyState("No results yet", "No students have completed this quiz yet.");
                return;
            }
            
            // Convert database results to display models
            studentResults = new ArrayList<>();
            for (StudentQuizResult dbResult : dbResults) {
                // Get student info
                Student student = dbResult.getStudent();
                String studentName = (student != null && student.getFullName() != null) 
                    ? student.getFullName() 
                    : "Student ID: " + dbResult.getStudentId();
                
                // Format score
                String scoreDisplay = dbResult.getScore() + "/" + dbResult.getTotalPoints();
                
                StudentResult displayResult = new StudentResult(studentName, scoreDisplay);
                studentResults.add(displayResult);
            }
            
            // Create a card for each student result
            for (StudentResult result : studentResults) {
                HBox card = createStudentResultCard(result);
                studentResultsContainer.getChildren().add(card);
            }
            
            System.out.println("✅ Loaded " + studentResults.size() + " real student results for quiz: " + currentQuizName);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error loading student results: " + e.getMessage());
            showEmptyState("Error loading results", "Failed to load student results: " + e.getMessage());
        }
    }
    
    /**
     * Show empty state message when no results available
     * @param title the title message
     * @param message the detail message
     */
    private void showEmptyState(String title, String message) {
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setStyle("-fx-padding: 60px;");
        
        Label titleLabel = new Label("📊 " + title);
        titleLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: 600; " +
            "-fx-text-fill: #757575;"
        );
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #9E9E9E;"
        );
        
        emptyState.getChildren().addAll(titleLabel, messageLabel);
        studentResultsContainer.getChildren().add(emptyState);
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
     * CRITICAL: Export raw numeric score only (not "85/100" format) to prevent Excel date conversion
     * @param file the file to write to
     * @throws IOException if writing fails
     */
    private void writeCSVFile(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // Write CSV header
            writer.write("Quiz Name,Username,Score\n");
            
            // Write data rows
            // CRITICAL: Use database results to get raw numeric score
            if (dbResults != null && !dbResults.isEmpty()) {
                for (StudentQuizResult dbResult : dbResults) {
                    // Get student name
                    String studentName = "Unknown";
                    if (dbResult.getStudent() != null && dbResult.getStudent().getFullName() != null) {
                        studentName = dbResult.getStudent().getFullName();
                    } else if (dbResult.getStudent() != null && dbResult.getStudent().getUsername() != null) {
                        studentName = dbResult.getStudent().getUsername();
                    }
                    
                    // CRITICAL: Export raw numeric score only (not "85/100" format)
                    // This prevents Excel from auto-converting "85/100" to a date like "30-Oct"
                    Integer score = dbResult.getScore();
                    String scoreValue = (score != null) ? String.valueOf(score) : "0";
                    
                    writer.write(currentQuizName + ",");
                    writer.write(studentName + ",");
                    writer.write(scoreValue + "\n");
                }
            } else {
                // Fallback: If dbResults not available, parse from display format
                for (StudentResult result : studentResults) {
                    String scoreStr = result.getScore();
                    // Extract numeric part before "/" if format is "85/100"
                    String scoreValue = scoreStr;
                    if (scoreStr != null && scoreStr.contains("/")) {
                        scoreValue = scoreStr.substring(0, scoreStr.indexOf("/")).trim();
                    }
                    
                    writer.write(currentQuizName + ",");
                    writer.write(result.getStudentName() + ",");
                    writer.write(scoreValue + "\n");
                }
            }
            
            writer.flush();
            
            System.out.println("✅ [StudentResultsController] CSV exported successfully with raw numeric scores");
        }
    }
    
    /**
     * Handle Back to Dashboard button click
     * Returns to Quiz Results List (not directly to dashboard)
     */
    @FXML
    private void handleBackToDashboard() {
        NavigationManager.getInstance().navigateTo(AppScreen.QUIZ_RESULTS_LIST);
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
