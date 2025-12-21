package com.se.quiz.quiz_management_system.controller.teacher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ChooseQuizzesController {
    private TeacherMainScreenController mainController;

    public void setMainController(TeacherMainScreenController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private Button btnAssign;

    @FXML
    void btnAssignPressed(ActionEvent event) {
        mainController.showAssignStudentList();
    }

}
