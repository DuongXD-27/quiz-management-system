package com.se.quiz.quiz_management_system.controller.teacher;

import java.io.IOException;

import com.se.quiz.quiz_management_system.controller.testingapp.TestingApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class TeacherMainScreenController {

    @FXML
    private Button btnChooseQuizzes;

    @FXML
    private Button btnCreateNewQuiz;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnViewResults;

    @FXML
    private StackPane stpCenter;

    @FXML
    void btnChooseQuizzesPressed(ActionEvent event) {
        showChooseQuizzes();
    }

    @FXML
    void btnCreateNewQuizPressed(ActionEvent event) {
        loadView("/view/teacher/create_new_quiz");
    }

    @FXML
    void btnLogoutPressed(ActionEvent event) throws IOException {
        TestingApp.setRoot("/view/login/login_screen");
    }

    @FXML
    void btnViewResultsPressed(ActionEvent event) {

    }

    public void loadView(String fxmlPath) {
        try {
            Parent node = FXMLLoader.load(TeacherMainScreenController.class.getResource(fxmlPath + ".fxml"));
            stpCenter.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showChooseQuizzes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/teacher/choose_quizzes.fxml"));
            Parent node = loader.load();

            // Lấy controller của file vừa load và truyền "chính mình" vào đó
            ChooseQuizzesController childController = loader.getController();
            childController.setMainController(this);

            stpCenter.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAssignStudentList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/teacher/assign_student-list.fxml"));
            Parent node = loader.load();

            // Lấy controller của file vừa load và truyền "chính mình" vào đó
            AssignStudentListController childController = loader.getController();
            childController.setMainController(this);

            stpCenter.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
