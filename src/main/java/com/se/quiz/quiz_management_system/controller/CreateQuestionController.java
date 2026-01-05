package com.se.quiz.quiz_management_system.controller;

import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Create Question view
 */
public class CreateQuestionController implements Initializable {
    
    @FXML
    private TextArea txtQuestionProblem;
    
    @FXML
    private RadioButton rbOptionA;
    
    @FXML
    private RadioButton rbOptionB;
    
    @FXML
    private RadioButton rbOptionC;
    
    @FXML
    private RadioButton rbOptionD;
    
    @FXML
    private TextField txtOptionA;
    
    @FXML
    private TextField txtOptionB;
    
    @FXML
    private TextField txtOptionC;
    
    @FXML
    private TextField txtOptionD;
    
    @FXML
    private Button btnSave;
    
    @FXML
    private Button btnCancel;
    
    private ToggleGroup answerGroup;
    private TeacherDashboardController parentController;
    
    /**
     * Set the parent controller (TeacherDashboardController) to close modal
     * @param parentController the parent controller
     */
    public void setParentController(TeacherDashboardController parentController) {
        this.parentController = parentController;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ToggleGroup for RadioButtons
        answerGroup = new ToggleGroup();
        rbOptionA.setToggleGroup(answerGroup);
        rbOptionB.setToggleGroup(answerGroup);
        rbOptionC.setToggleGroup(answerGroup);
        rbOptionD.setToggleGroup(answerGroup);
    }
    
    /**
     * Handle Save button click
     */
    @FXML
    private void handleSave() {
        // Validate input
        String problem = txtQuestionProblem.getText().trim();
        if (problem.isEmpty()) {
            JavaFXHelper.showError("Validation Error", "Please enter the question problem");
            return;
        }
        
        String optionA = txtOptionA.getText().trim();
        String optionB = txtOptionB.getText().trim();
        String optionC = txtOptionC.getText().trim();
        String optionD = txtOptionD.getText().trim();
        
        if (optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()) {
            JavaFXHelper.showError("Validation Error", "Please fill in all answer options (A, B, C, D)");
            return;
        }
        
        // Check if correct answer is selected
        RadioButton selectedRadio = (RadioButton) answerGroup.getSelectedToggle();
        if (selectedRadio == null) {
            JavaFXHelper.showError("Validation Error", "Please select the correct answer");
            return;
        }
        
        // Get selected answer
        String correctAnswer = "";
        String solution = "";
        if (selectedRadio == rbOptionA) {
            correctAnswer = "A";
            solution = optionA;
        } else if (selectedRadio == rbOptionB) {
            correctAnswer = "B";
            solution = optionB;
        } else if (selectedRadio == rbOptionC) {
            correctAnswer = "C";
            solution = optionC;
        } else if (selectedRadio == rbOptionD) {
            correctAnswer = "D";
            solution = optionD;
        }
        
        // TODO: Save question to database
        // For now, just show success message
        System.out.println("Question Problem: " + problem);
        System.out.println("Option A: " + optionA);
        System.out.println("Option B: " + optionB);
        System.out.println("Option C: " + optionC);
        System.out.println("Option D: " + optionD);
        System.out.println("Correct Answer: " + correctAnswer);
        System.out.println("Solution: " + solution);
        
        JavaFXHelper.showInfo("Success", "Question saved successfully!");
        
        // Clear form after saving
        clearForm();
        
        // Close modal
        if (parentController != null) {
            parentController.closeModal();
        }
    }
    
    /**
     * Handle Cancel button click
     */
    @FXML
    private void handleCancel() {
        // Close modal
        if (parentController != null) {
            parentController.closeModal();
        }
    }
    
    /**
     * Clear the form
     */
    private void clearForm() {
        txtQuestionProblem.clear();
        txtOptionA.clear();
        txtOptionB.clear();
        txtOptionC.clear();
        txtOptionD.clear();
        answerGroup.selectToggle(null);
    }
}

