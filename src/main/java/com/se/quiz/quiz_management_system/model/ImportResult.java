package com.se.quiz.quiz_management_system.model;

import java.util.ArrayList;
import java.util.List;

    // ImportResult - Represents the result of a CSV import operation
    
public class ImportResult {
    
    private int successCount;
    private int errorCount;
    private List<String> errorMessages;
    
    public ImportResult() {
        this.successCount = 0;
        this.errorCount = 0;
        this.errorMessages = new ArrayList<>();
    }
    
    public void incrementSuccess() {
        this.successCount++;
    }
    
    public void incrementError() {
        this.errorCount++;
    }
    
    public void addErrorMessage(String message) {
        this.errorMessages.add(message);
        this.errorCount++;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
    
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
    
    public int getTotalProcessed() {
        return successCount + errorCount;
    }
    
    public boolean hasErrors() {
        return errorCount > 0;
    }
    
    @Override
    public String toString() {
        return "ImportResult{" +
                "successCount=" + successCount +
                ", errorCount=" + errorCount +
                ", totalProcessed=" + getTotalProcessed() +
                '}';
    }
}

