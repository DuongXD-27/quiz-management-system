package com.se.quiz.quiz_management_system.navigation;

import java.util.Map;

/**
 * NavigationAware interface - Controller implement interface này để nhận dữ liệu
 * khi được navigate tới
 */
public interface NavigationAware {
    
    /**
     * Được gọi khi màn hình được navigate tới
     * @param data Dữ liệu được truyền từ màn hình trước
     */
    void onNavigatedTo(Map<String, Object> data);
}

