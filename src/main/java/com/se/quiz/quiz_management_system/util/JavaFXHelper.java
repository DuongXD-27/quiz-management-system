package com.se.quiz.quiz_management_system.util;

import com.se.quiz.quiz_management_system.model.ImportResult;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

/**
 * JavaFXHelper - Utility class for JavaFX UI operations
 */
public class JavaFXHelper {
    
    // Private constructor to prevent instantiation
    private JavaFXHelper() {
    }
    
    /**
     * Open a file chooser dialog to select a CSV file
     * @param stage the parent stage
     * @return the selected File, or null if cancelled
     */
    public static File chooseCSVFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file CSV");
        
        // Set extension filter for CSV files
        FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter(
            "CSV files (*.csv)", "*.csv"
        );
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter(
            "All files (*.*)", "*.*"
        );
        
        fileChooser.getExtensionFilters().addAll(csvFilter, allFilter);
        fileChooser.setSelectedExtensionFilter(csvFilter);
        
        // Show open file dialog
        return fileChooser.showOpenDialog(stage);
    }
    
    /**
     * Show an alert dialog
     * @param alertType the type of alert (ERROR, WARNING, INFORMATION, CONFIRMATION)
     * @param title the alert title
     * @param header the alert header text (can be null)
     * @param content the alert content text
     */
    public static void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Show a simple alert with just title and message (no header)
     * @param alertType the type of alert
     * @param title the alert title
     * @param message the alert message
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        showAlert(alertType, title, null, message);
    }
    
    /**
     * Show an information alert
     * @param title the alert title
     * @param message the alert message
     */
    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }
    
    /**
     * Show an error alert
     * @param title the alert title
     * @param message the alert message
     */
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }
    
    /**
     * Show a warning alert
     * @param title the alert title
     * @param message the alert message
     */
    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }
    
    /**
     * Show a confirmation dialog
     * @param title the alert title
     * @param message the alert message
     * @return true if user clicked OK, false otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Show import result dialog with detailed statistics
     * @param result the ImportResult to display
     */
    public static void showImportResultDialog(ImportResult result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Kết quả Import");
        alert.setHeaderText("Hoàn thành import sinh viên");
        
        // Build summary message
        StringBuilder summary = new StringBuilder();
        summary.append("Tổng số: ").append(result.getTotalProcessed()).append("\n");
        summary.append("Thành công: ").append(result.getSuccessCount()).append("\n");
        summary.append("Lỗi: ").append(result.getErrorCount()).append("\n");
        
        if (result.hasErrors()) {
            summary.append("\nChi tiết lỗi:");
            alert.setContentText(summary.toString());
            
            // Add error details in expandable area
            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            
            StringBuilder errorDetails = new StringBuilder();
            for (String errorMsg : result.getErrorMessages()) {
                errorDetails.append(errorMsg).append("\n");
            }
            textArea.setText(errorDetails.toString());
            
            alert.getDialogPane().setExpandableContent(textArea);
        } else {
            alert.setContentText(summary.toString());
        }
        
        alert.showAndWait();
    }
    
    /**
     * Show a success message with custom content
     * @param title the alert title
     * @param message the success message
     */
    public static void showSuccess(String title, String message) {
        showInfo(title, message);
    }
    
    /**
     * Show an error dialog with exception details
     * @param title the alert title
     * @param message the error message
     * @param exception the exception
     */
    public static void showErrorWithException(String title, String message, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(exception.getMessage());
        
        // Add stack trace in expandable area
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        
        StringBuilder stackTrace = new StringBuilder();
        stackTrace.append(exception.toString()).append("\n");
        for (StackTraceElement element : exception.getStackTrace()) {
            stackTrace.append("    at ").append(element.toString()).append("\n");
        }
        textArea.setText(stackTrace.toString());
        
        alert.getDialogPane().setExpandableContent(textArea);
        alert.showAndWait();
    }
}

