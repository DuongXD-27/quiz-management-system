package com.se.quiz.quiz_management_system.controller.student;

import com.se.quiz.quiz_management_system.controller.testingapp.TestingApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.io.IOException;

public class SummaryScreenController {

    @FXML
    private Button btnBackToMainScreen;

    @FXML
    void btnBackToMainScreenPressed(ActionEvent event) throws IOException {
        TestingApp.setRoot("/view/student/student_main_screen");
    }

}
