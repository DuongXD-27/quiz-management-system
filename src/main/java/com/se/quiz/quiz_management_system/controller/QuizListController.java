package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.entity.Quiz;
import com.se.quiz.quiz_management_system.navigation.AppScreen;
import com.se.quiz.quiz_management_system.navigation.NavigationManager;
import com.se.quiz.quiz_management_system.service.AuthService;
import com.se.quiz.quiz_management_system.service.QuizService;
import com.se.quiz.quiz_management_system.session.SessionManager;
import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private QuizService quizService;
    
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
    
    /**
     * Set the QuizService instance (injected from Spring context)
     * @param quizService the quiz service
     */
    public void setQuizService(QuizService quizService) {
        this.quizService = quizService;
        
        // Reload data when service is injected (after initialization)
        if (tblQuizzes != null) {
            loadQuizData();
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
     * Load quiz data from database into the table
     */
    private void loadQuizData() {
        try {
            // Check if QuizService is available
            if (quizService == null) {
                System.out.println("QuizService not yet injected, using empty data");
                quizData = FXCollections.observableArrayList();
                tblQuizzes.setItems(quizData);
                return;
            }
            
            // Fetch all quizzes from database
            List<Quiz> quizzes = quizService.getAllQuizzes();
            
            // Convert Quiz entities to QuizModel for TableView
            ObservableList<QuizModel> modelList = FXCollections.observableArrayList();
            for (Quiz quiz : quizzes) {
                modelList.add(new QuizModel(
                    quiz.getQuizId(),
                    quiz.getQuizName(),
                    quiz.getTimeLimit() != null ? quiz.getTimeLimit() : 0,
                    quiz.getNumberOfQuestion() != null ? quiz.getNumberOfQuestion() : 0
                ));
            }
            
            // Set data to table
            quizData = modelList;
            tblQuizzes.setItems(quizData);
            
            // Force table refresh
            tblQuizzes.refresh();
            
            System.out.println("Loaded " + quizzes.size() + " quizzes from database");
            
        } catch (Exception e) {
            e.printStackTrace();
            JavaFXHelper.showError("Data Load Error", 
                "Failed to load quizzes from database: " + e.getMessage());
            
            // Set empty list on error
            quizData = FXCollections.observableArrayList();
            tblQuizzes.setItems(quizData);
        }
    }
    
    /**
     * Handle Assign to Students button click
     * @param quiz the selected quiz
     */
    private void handleAssignToStudents(QuizModel quiz) {
        System.out.println("Assigning quiz to students: " + quiz.getName() + " (ID: " + quiz.getQuizId() + ")");
        
        // Pass quiz ID and name to AddStudentToQuiz screen
        Map<String, Object> data = new HashMap<>();
        data.put("quizId", quiz.getQuizId());
        data.put("quizName", quiz.getName());
        
        NavigationManager.getInstance().navigateTo(AppScreen.ADD_STUDENT_TO_QUIZ, data);
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
     * QuizModel class - represents a quiz in the table
     * Wrapper class for Quiz entity with JavaFX properties
     */
    public static class QuizModel {
        private final SimpleLongProperty quizId;
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty timeLimit;
        private final SimpleIntegerProperty questionCount;
        
        public QuizModel(Long quizId, String name, Integer timeLimit, Integer questionCount) {
            this.quizId = new SimpleLongProperty(quizId != null ? quizId : 0L);
            this.name = new SimpleStringProperty(name != null ? name : "Untitled Quiz");
            this.timeLimit = new SimpleIntegerProperty(timeLimit != null ? timeLimit : 0);
            this.questionCount = new SimpleIntegerProperty(questionCount != null ? questionCount : 0);
        }
        
        // Quiz ID
        public Long getQuizId() {
            return quizId.get();
        }
        
        public void setQuizId(Long quizId) {
            this.quizId.set(quizId);
        }
        
        public SimpleLongProperty quizIdProperty() {
            return quizId;
        }
        
        // Name
        public String getName() {
            return name.get();
        }
        
        public void setName(String name) {
            this.name.set(name);
        }
        
        public SimpleStringProperty nameProperty() {
            return name;
        }
        
        // Time Limit
        public Integer getTimeLimit() {
            return timeLimit.get();
        }
        
        public void setTimeLimit(Integer timeLimit) {
            this.timeLimit.set(timeLimit);
        }
        
        public SimpleIntegerProperty timeLimitProperty() {
            return timeLimit;
        }
        
        // Question Count
        public Integer getQuestionCount() {
            return questionCount.get();
        }
        
        public void setQuestionCount(Integer questionCount) {
            this.questionCount.set(questionCount);
        }
        
        public SimpleIntegerProperty questionCountProperty() {
            return questionCount;
        }
    }
}
