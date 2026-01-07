package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.entity.Quiz;
import com.se.quiz.quiz_management_system.model.Role;
import com.se.quiz.quiz_management_system.model.UserSession;
import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.service.QuizService;
import com.se.quiz.quiz_management_system.service.ResultService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

/**
 * Controller for the Available Quizzes view (Student)
 * Displays available quizzes in a card-based layout
 */
public class AvailableQuizzesController implements Initializable {
    
    @FXML
    private VBox quizContainer;
    
    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnLogout;
    
    private AuthService authService;
    private QuizService quizService;
    private ResultService resultService;
    
    /**
     * Set the AuthService instance
     * @param authService the AuthService instance
     */
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Set the QuizService instance (injected from Spring context)
     * @param quizService the quiz service
     */
    public void setQuizService(QuizService quizService) {
        this.quizService = quizService;
        
        // Reload quizzes when service is injected
        if (quizContainer != null) {
            loadQuizzes();
        }
    }
    
    /**
     * Set the ResultService instance (injected from Spring context)
     * @param resultService the result service
     */
    public void setResultService(ResultService resultService) {
        this.resultService = resultService;
        
        // Reload quizzes to check completion status
        if (quizContainer != null) {
            loadQuizzes();
        }
    }
    
    /**
     * Mock Quiz data class for demonstration
     */
    private static class QuizData {
        private final Long quizId;
        private final String subject;
        private final String duration;
        private final String points;
        
        public QuizData(Long quizId, String subject, String duration, String points) {
            this.quizId = quizId;
            this.subject = subject;
            this.duration = duration;
            this.points = points;
        }
        
        public Long getQuizId() {
            return quizId;
        }
        
        public String getSubject() {
            return subject;
        }
        
        public String getDuration() {
            return duration;
        }
        
        public String getPoints() {
            return points;
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadQuizzes();
    }
    
    /**
     * Load quizzes assigned to the logged-in student
     * CRITICAL: Uses JOIN query to fetch only assigned quizzes
     */
    private void loadQuizzes() {
        try {
            // Clear existing children
            quizContainer.getChildren().clear();
            
            // Check if services are available
            if (quizService == null) {
                System.out.println("QuizService not yet injected, showing empty list");
                addEmptyStateMessage();
                return;
            }
            
            // Get current student ID
            Long studentId = getCurrentStudentId();
            if (studentId == null) {
                System.out.println("Student ID not found, showing empty list");
                addEmptyStateMessage();
                return;
            }
            
            // ✅ CRITICAL: Fetch quizzes assigned to this student via JOIN query
            // SQL: SELECT q.* FROM quiz q JOIN student_quiz sq ON q.quiz_id = sq.quiz_id WHERE sq.student_id = ?
            List<Quiz> quizzes = quizService.getQuizzesForStudent(studentId);
            
            if (quizzes.isEmpty()) {
                addEmptyStateMessage();
                System.out.println("No quizzes assigned to student ID " + studentId);
            } else {
                // Create a quiz card for each quiz
                for (Quiz quiz : quizzes) {
                    QuizData quizData = new QuizData(
                        quiz.getQuizId(),
                        quiz.getQuizName(),
                        (quiz.getTimeLimit() != null ? quiz.getTimeLimit() + " minutes" : "No time limit"),
                        (quiz.getNumberOfQuestion() != null ? quiz.getNumberOfQuestion() + " questions" : "")
                    );
                    HBox quizCard = createQuizCard(quizData);
                    quizContainer.getChildren().add(quizCard);
                }
                System.out.println("Loaded " + quizzes.size() + " quizzes for student ID " + studentId);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JavaFXHelper.showError("Data Load Error", 
                "Failed to load quizzes: " + e.getMessage());
            addEmptyStateMessage();
        }
    }
    
    /**
     * Get current student ID from AuthService or SessionManager
     * @return student ID or null if not found
     */
    private Long getCurrentStudentId() {
        // Try to get from AuthService
        if (authService != null && authService.getCurrentUser() != null) {
            UserSession currentUser = authService.getCurrentUser();
            
            // Check if user is a student
            if (currentUser.getRole() == Role.STUDENT && currentUser.getUserId() != null) {
                // For students, userId IS the student_id
                return currentUser.getUserId();
            }
        }
        
        // Alternative: Get from SessionManager
        Long userId = SessionManager.getCurrentUserId();
        if (userId != null) {
            // Verify role is STUDENT
            UserSession session = SessionManager.getCurrentUserSession();
            if (session != null && session.getRole() == Role.STUDENT) {
                return userId;
            }
        }
        
        System.out.println("Could not get student ID from current user");
        return null;
    }
    
    /**
     * Add empty state message when no quizzes available
     */
    private void addEmptyStateMessage() {
        Label emptyLabel = new Label("No quizzes assigned yet.\nPlease contact your teacher.");
        emptyLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #757575; " +
            "-fx-padding: 40px; " +
            "-fx-text-alignment: center;"
        );
        VBox emptyBox = new VBox(emptyLabel);
        emptyBox.setAlignment(Pos.CENTER);
        quizContainer.getChildren().add(emptyBox);
    }
    
    /**
     * Create a quiz card UI component
     * @param quiz the quiz data
     * @return HBox representing the quiz card
     */
    private HBox createQuizCard(QuizData quiz) {
        // Main card container
        HBox card = new HBox();
        card.getStyleClass().add("quiz-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(20);
        
        // Left section: Quiz information
        VBox infoSection = new VBox();
        infoSection.setAlignment(Pos.CENTER_LEFT);
        infoSection.setSpacing(8);
        infoSection.setPrefWidth(600);
        
        // Subject label
        Label subjectLabel = new Label(quiz.getSubject());
        subjectLabel.getStyleClass().add("quiz-subject");
        
        // Info labels container
        HBox infoContainer = new HBox();
        infoContainer.setSpacing(30);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Duration label
        Label durationLabel = new Label("⏱ " + quiz.getDuration());
        durationLabel.getStyleClass().add("quiz-info");
        
        // Points label
        Label pointsLabel = new Label("⭐ " + quiz.getPoints());
        pointsLabel.getStyleClass().add("quiz-info");
        
        infoContainer.getChildren().addAll(durationLabel, pointsLabel);
        infoSection.getChildren().addAll(subjectLabel, infoContainer);
        
        // Right section: Join Now button
        Button joinButton = new Button("Join Now");
        joinButton.getStyleClass().add("join-button");
        
        // ═══════════════════════════════════════════════════════════
        // CRITICAL: CHECK IF STUDENT HAS COMPLETED THIS QUIZ
        // ═══════════════════════════════════════════════════════════
        boolean hasCompleted = false;
        if (resultService != null) {
            Long studentId = getCurrentStudentId();
            if (studentId != null && quiz.getQuizId() != null) {
                hasCompleted = resultService.hasStudentCompletedQuiz(studentId, quiz.getQuizId());
            }
        }
        
        if (hasCompleted) {
            // Student has already completed this quiz
            joinButton.setText("✓ Completed");
            joinButton.setDisable(true);
            joinButton.setStyle(
                "-fx-background-color: #9E9E9E; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: 600; " +
                "-fx-padding: 12 30 12 30; " +
                "-fx-background-radius: 25px; " +
                "-fx-cursor: default; " +
                "-fx-opacity: 0.7;"
            );
            System.out.println("Quiz " + quiz.getQuizId() + " already completed by student");
        } else {
            // Student hasn't completed this quiz yet - allow start
            joinButton.setOnAction(e -> handleJoinQuiz(quiz));
        }
        
        // Add sections to card
        card.getChildren().addAll(infoSection, joinButton);
        
        // Add vertical spacing between cards inside VBox container
        VBox.setMargin(card, new Insets(6, 0, 6, 0));
        
        return card;
    }
    
    /**
     * Handle Join Now button click
     * @param quiz the quiz data
     */
    private void handleJoinQuiz(QuizData quiz) {
        // Prepare data to pass to TakeQuiz screen
        Map<String, Object> data = new HashMap<>();
        data.put("quizId", quiz.getQuizId());
        data.put("quizTitle", quiz.getSubject());
        
        // Navigate to Take Quiz screen with data
        NavigationManager.getInstance().navigateTo(AppScreen.TAKE_QUIZ, data);
    }
    
    /**
     * Handle Back button click - Navigate to Student Dashboard
     */
    @FXML
    private void handleBack() {
        NavigationManager.getInstance().navigateTo(AppScreen.STUDENT_DASHBOARD);
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
        
        // Navigate to Login screen
        NavigationManager.getInstance().navigateToLogin();
    }
}
