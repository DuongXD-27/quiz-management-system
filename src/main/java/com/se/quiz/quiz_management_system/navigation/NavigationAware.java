package com.se.quiz.quiz_management_system.navigation;

import java.util.Map;

// NavigationAware interface - Controller implements this interface to receive data
// when navigated to
    
public interface NavigationAware {
    
    // Called when the screen is navigated to
    // @param data Data passed from the previous screen
    
    void onNavigatedTo(Map<String, Object> data);
}

