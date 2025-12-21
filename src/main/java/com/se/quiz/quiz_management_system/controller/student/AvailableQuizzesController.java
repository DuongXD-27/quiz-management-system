package com.se.quiz.quiz_management_system.controller.student;

import com.se.quiz.quiz_management_system.controller.testingapp.TestingApp;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AvailableQuizzesController {

    @FXML
    private Button btnChooseQuiz;

    @FXML
    void btnChooseQuizPressed(ActionEvent event) throws IOException{
        // Load the question screen when a quiz is chosen
        TestingApp.setRoot("/view/student/question_screen");
    }

}
