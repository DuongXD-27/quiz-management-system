package com.se.quiz.quiz_management_system.controller.login;

import java.io.IOException;

import com.se.quiz.quiz_management_system.controller.testingapp.TestingApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginScreenController {

    @FXML
    private Button btnCreateAccount;

    @FXML
    private Button btnLoginStudent;

    @FXML
    private Button btnLoginTeacher;

    @FXML
    private Label lblException;

    @FXML
    private TextField tfPassword;

    @FXML
    private TextField tfUsername;

    @FXML
    void btnCreateAccountPressed(ActionEvent event) throws IOException {
        TestingApp.setRoot("/view/login/create_account");
    }

    @FXML
    void btnLoginStudentPressed(ActionEvent event) throws IOException {
        TestingApp.setRoot("/view/student/student_main_screen");
    }

    @FXML
    void btnLoginTeacherPressed(ActionEvent event) throws IOException {
        TestingApp.setRoot("/view/teacher/teacher_main_screen");
    }

}
