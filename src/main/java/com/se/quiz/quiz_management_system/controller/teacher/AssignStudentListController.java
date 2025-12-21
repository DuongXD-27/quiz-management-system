package com.se.quiz.quiz_management_system.controller.teacher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AssignStudentListController {
    private TeacherMainScreenController mainController;

    public void setMainController(TeacherMainScreenController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private Button btnBackChooseQuizzes;

    @FXML
    void btnBackChooseQuizzesPressed(ActionEvent event) {
        mainController.showChooseQuizzes();
    }

}
