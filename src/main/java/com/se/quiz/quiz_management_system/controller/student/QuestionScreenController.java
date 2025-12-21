package com.se.quiz.quiz_management_system.controller.student;

import java.io.IOException;

import com.se.quiz.quiz_management_system.controller.testingapp.TestingApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class QuestionScreenController {

    @FXML
    private Button btnBackToMainScreen;

    @FXML
    private Button btnSubmit;

    @FXML
    void btnBackToMainScreenPressed(ActionEvent event) throws IOException {
        TestingApp.setRoot("/view/student/student_main_screen");
    }

    @FXML
    void btnSubmitPressed(ActionEvent event) throws IOException {
        TestingApp.setRoot("/view/student/summary_screen");
    }

}
