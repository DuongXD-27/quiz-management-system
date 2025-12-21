package com.se.quiz.quiz_management_system.controller.login;

import java.io.IOException;

import com.se.quiz.quiz_management_system.controller.testingapp.TestingApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class CreateAccountController {

    @FXML
    private Button btnBackToLoginScreen;

    @FXML
    private Button btnRegister;

    @FXML
    private Label lblException;

    @FXML
    private RadioButton radStudent;

    @FXML
    private RadioButton radTeacher;

    @FXML
    private TextField tfEnterPasswordAgain;

    @FXML
    private TextField tfPassword;

    @FXML
    private TextField tfUsername;

    @FXML
    private ToggleGroup tgChooseRole;

    @FXML
    void btnBackToLoginScreenPressed(ActionEvent event) throws IOException{
        TestingApp.setRoot("/view/login/login_screen");
    }

    @FXML
    void btnRegisterPressed(ActionEvent event) throws IOException {
        TestingApp.setRoot("/view/login/login_screen");
    }

}
