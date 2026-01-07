package com.se.quiz.quiz_management_system.util;

import com.se.quiz.quiz_management_system.model.ImportResult;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.File;

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
        // Route all alerts through modern styled dialog
        showModernDialog(alertType, title, header, content, false);
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
        return showModernDialog(Alert.AlertType.CONFIRMATION, title, null, message, true);
    }
    
    /**
     * Show a modern, styled dialog that matches the application's look & feel.
     * For CONFIRMATION dialogs, returns true if user confirms, otherwise false.
     */
    private static boolean showModernDialog(Alert.AlertType type,
                                            String title,
                                            String header,
                                            String content,
                                            boolean isConfirmation) {
        // Ensure runs on FX thread
        if (!Platform.isFxApplicationThread()) {
            final boolean[] resultHolder = new boolean[]{false};
            try {
                Platform.runLater(() -> resultHolder[0] = showModernDialog(type, title, header, content, isConfirmation));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultHolder[0];
        }
        
        // Determine owner window (if any)
        Window owner = null;
        for (Window w : Window.getWindows()) {
            if (w.isShowing()) {
                owner = w;
                break;
            }
        }
        
        Stage dialogStage = new Stage(StageStyle.TRANSPARENT);
        if (owner != null) {
            dialogStage.initOwner(owner);
        }
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        
        // Root overlay
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(15,23,42,0.45);");
        
        // Card container
        VBox card = new VBox(16);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(24, 28, 24, 28));
        card.setMaxWidth(420);
        card.setStyle("-fx-background-color: #FFFFFF;"
                    + "-fx-background-radius: 14;"
                    + "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.30), 24, 0, 0, 10);");
        
        // Title (no extra icon, clean style)
        Label titleLabel = new Label(title != null ? title : "Notification");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #111827;");
        
        // Optional header
        if (header != null && !header.isEmpty()) {
            Label headerLabel = new Label(header);
            headerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #4B5563;");
            headerLabel.setWrapText(true);
            card.getChildren().addAll(titleLabel, headerLabel);
        } else {
            card.getChildren().add(titleLabel);
        }
        
        // Content
        if (content != null && !content.isEmpty()) {
            Label contentLabel = new Label(content);
            contentLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6B7280;");
            contentLabel.setWrapText(true);
            contentLabel.setPadding(new Insets(4, 0, 0, 0));
            card.getChildren().add(contentLabel);
        }
        
        // Buttons
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(18, 0, 0, 0));
        
        Button primaryButton = new Button();
        primaryButton.setStyle("-fx-background-color: linear-gradient(to right, #4C1D95, #7C3AED);"
                             + "-fx-text-fill: white; -fx-font-weight: 700; -fx-padding: 8 18;"
                             + "-fx-background-radius: 999; -fx-cursor: hand;");
        
        Button secondaryButton = null;
        
        final boolean[] result = new boolean[]{false};
        
        if (isConfirmation) {
            primaryButton.setText("OK");
            secondaryButton = new Button("Cancel");
            secondaryButton.setStyle("-fx-background-color: #E5E7EB; -fx-text-fill: #374151;"
                                   + "-fx-font-weight: 600; -fx-padding: 8 18;"
                                   + "-fx-background-radius: 999; -fx-cursor: hand;");
            
            primaryButton.setOnAction(e -> {
                result[0] = true;
                dialogStage.close();
            });
            secondaryButton.setOnAction(e -> {
                result[0] = false;
                dialogStage.close();
            });
            
            buttons.getChildren().addAll(secondaryButton, primaryButton);
        } else {
            primaryButton.setText("Close");
            primaryButton.setOnAction(e -> {
                result[0] = true;
                dialogStage.close();
            });
            buttons.getChildren().add(primaryButton);
        }
        
        card.getChildren().add(buttons);
        root.getChildren().add(card);
        
        Scene scene = new Scene(root);
        scene.setFill(null);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
        
        return result[0];
    }
    
    /**
     * Show import result dialog with detailed statistics
     * @param result the ImportResult to display
     */
    public static void showImportResultDialog(ImportResult result) {
        // Build summary message
        StringBuilder summary = new StringBuilder();
        summary.append("Tổng số: ").append(result.getTotalProcessed()).append("\n");
        summary.append("Thành công: ").append(result.getSuccessCount()).append("\n");
        summary.append("Lỗi: ").append(result.getErrorCount()).append("\n");
        
        if (result.hasErrors()) {
            summary.append("\n\nChi tiết lỗi:\n");
            for (String errorMsg : result.getErrorMessages()) {
                summary.append("• ").append(errorMsg).append("\n");
            }
        }
        
        showModernDialog(Alert.AlertType.INFORMATION, "Kết quả Import",
            "Hoàn thành import sinh viên", summary.toString(), false);
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
        StringBuilder stackTrace = new StringBuilder();
        stackTrace.append(exception.toString()).append("\n");
        for (StackTraceElement element : exception.getStackTrace()) {
            stackTrace.append("    at ").append(element.toString()).append("\n");
        }
        
        String fullMessage = (message != null ? message + "\n\n" : "")
            + "Chi tiết:\n" + exception.getMessage() + "\n\n"
            + "Stacktrace:\n" + stackTrace;
        
        showModernDialog(Alert.AlertType.ERROR, title, null, fullMessage, false);
    }
}

