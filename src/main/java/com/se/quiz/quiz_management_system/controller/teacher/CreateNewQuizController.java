package com.se.quiz.quiz_management_system.controller.teacher;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.input.*;

public class CreateNewQuizController {

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDeleteOption;

    @FXML
    private Button btnDeleteQuestion;

    @FXML
    private Button btnNewOption;

    @FXML
    private Button btnNewQuestion;

    @FXML
    private Button btnSave;

    @FXML
    private TextField tfDescription;

    @FXML
    private TextField tfTitle;

    @FXML
    private VBox vboxQuiz;

    private ArrayList<VBox> vboxQuestionList = new ArrayList<>();

    private ArrayList<HBox> hboxOptionsList = new ArrayList<>(); 

    private int currentQuestionIndex = -1;

    private int currentOptionIndex = -1;

    @FXML
    void btnClearPressed(ActionEvent event) {
        vboxQuiz.getChildren().clear();
        vboxQuestionList = new ArrayList<>();
    }

    @FXML
    void btnDeleteOptionPressed(ActionEvent event) {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < vboxQuestionList.size()
                && currentOptionIndex >= 0 && currentOptionIndex < hboxOptionsList.size()) {
            VBox vboxCurrentQuestion = vboxQuestionList.get(currentQuestionIndex);
            HBox hboxOptionToRemove = hboxOptionsList.get(currentOptionIndex);

            vboxCurrentQuestion.getChildren().remove(hboxOptionToRemove);
            hboxOptionsList.remove(currentOptionIndex);

            // Cập nhật lại chỉ số tùy chọn hiện tại
            currentOptionIndex = -1;
        }
    }

    @FXML
    void btnDeleteQuestionPressed(ActionEvent event) {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < vboxQuestionList.size()) {
            vboxQuiz.getChildren().remove(vboxQuestionList.get(currentQuestionIndex));
            vboxQuestionList.remove(currentQuestionIndex);
            // Cập nhật lại chỉ số câu hỏi hiện tại
            currentQuestionIndex = -1;

            // Cập nhật lại nhãn câu hỏi
            for (int i = 0; i < vboxQuestionList.size(); i++) {
                VBox vboxQuestion = vboxQuestionList.get(i);
                Label lblQuestion = (Label) vboxQuestion.getChildren().get(0);
                lblQuestion.setText("Question " + (i + 1) + ":");
            }
        }
    }

    @FXML
    void btnNewOptionPressed(ActionEvent event) {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < vboxQuestionList.size()) {
            VBox vboxCurrentQuestion = vboxQuestionList.get(currentQuestionIndex);

            HBox hboxOption = new HBox();
            hboxOption.setSpacing(10.0);

            RadioButton radOption = new RadioButton();
            TextField tfOption = new TextField();
            tfOption.setPromptText("Enter option text here...");
            tfOption.setFont(new Font(18.0));

            tfOption.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                currentOptionIndex = hboxOptionsList.indexOf(hboxOption);
                currentQuestionIndex = vboxQuestionList.indexOf(vboxCurrentQuestion);
            });

            hboxOption.getChildren().addAll(radOption, tfOption);

            hboxOptionsList.add(hboxOption);
            vboxCurrentQuestion.getChildren().add(hboxOption);
        }
    }

    @FXML
    void btnNewQuestionPressed(ActionEvent event) {
        VBox vboxQuestion = new VBox();
        vboxQuestion.setSpacing(10.0);

        Label lblQuestion = new Label("Question " + (vboxQuestionList.size() + 1) + ":");
        lblQuestion.setFont(new Font(24.0));
        TextField tfProblem = new TextField();
        tfProblem.setPromptText("Enter the problem here...");
        tfProblem.setFont(new Font(24.0));

        tfProblem.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            currentQuestionIndex = vboxQuestionList.indexOf(vboxQuestion);
        });

        vboxQuestion.getChildren().addAll(lblQuestion, tfProblem);

        vboxQuestionList.add(vboxQuestion);
        vboxQuiz.getChildren().add(vboxQuestion);
    }

    @FXML
    void btnSavePressed(ActionEvent event) {

    }

}
