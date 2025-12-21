package com.se.quiz.quiz_management_system.controller.student;

import java.io.IOException;

import com.se.quiz.quiz_management_system.controller.teacher.TeacherMainScreenController;
import com.se.quiz.quiz_management_system.controller.testingapp.TestingApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class StudentMainScreenController {

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnQuizzes;

    @FXML
    private Button btnViewResult;

    @FXML
    private StackPane stpCenter;

    @FXML
    void btnLogoutPressed(ActionEvent event) throws IOException {
        TestingApp.setRoot("/view/login/login_screen");
    }

    @FXML
    void btnQuizzesPressed(ActionEvent event) {
        loadView("/view/student/available_quizzes");
    }

    @FXML
    void btnViewResultPressed(ActionEvent event) {

    }

    public void loadView(String fxmlPath) {
        try {
            Parent node = FXMLLoader.load(TeacherMainScreenController.class.getResource(fxmlPath + ".fxml"));
            stpCenter.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
