package com.se.quiz.quiz_management_system.navigation;

import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.*;

/**
 * NavigationManager - Singleton quản lý điều hướng giữa các màn hình
 * Sử dụng Finite State Machine (FSM) pattern với Window State Management
 */
public class NavigationManager {
    
    private static NavigationManager instance;
    private Stage primaryStage;
    private ApplicationContext springContext;
    
    // Navigation history để hỗ trợ back navigation
    private final Stack<NavigationState> navigationHistory = new Stack<>();
    
    // Cache controllers để tránh reload không cần thiết (optional)
    private final Map<AppScreen, Object> controllerCache = new HashMap<>();
    
    // Data transfer object để truyền dữ liệu giữa các màn hình
    private final Map<String, Object> transferData = new HashMap<>();
    
    // Private constructor (Singleton)
    private NavigationManager() {
    }
    
    /**
     * Lấy instance của NavigationManager (Singleton)
     */
    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }
    
    /**
     * Khởi tạo NavigationManager với primary stage và Spring context
     * @param primaryStage Stage chính của ứng dụng
     * @param springContext Spring ApplicationContext để inject dependencies
     */
    public void initialize(Stage primaryStage, ApplicationContext springContext) {
        this.primaryStage = primaryStage;
        this.springContext = springContext;
    }
    
    /**
     * Chuyển sang màn hình mới (Simple version without data)
     * @param screen Màn hình đích
     */
    public void switchScene(AppScreen screen) {
        navigateTo(screen, null, true);
    }
    
    /**
     * Chuyển sang màn hình mới
     * @param screen Màn hình đích
     */
    public void navigateTo(AppScreen screen) {
        navigateTo(screen, null);
    }
    
    /**
     * Chuyển sang màn hình mới với dữ liệu truyền vào
     * @param screen Màn hình đích
     * @param data Dữ liệu cần truyền (Map<String, Object>)
     */
    public void navigateTo(AppScreen screen, Map<String, Object> data) {
        navigateTo(screen, data, true);
    }
    
    /**
     * Chuyển sang màn hình mới với tùy chọn lưu history
     * @param screen Màn hình đích
     * @param data Dữ liệu cần truyền
     * @param addToHistory Có lưu vào history hay không
     */
    public void navigateTo(AppScreen screen, Map<String, Object> data, boolean addToHistory) {
        try {
            // Lưu màn hình hiện tại vào history nếu cần
            if (addToHistory && primaryStage.getScene() != null) {
                AppScreen currentScreen = getCurrentScreen();
                if (currentScreen != null) {
                    navigationHistory.push(new NavigationState(currentScreen, new HashMap<>(transferData)));
                }
            }
            
            // Clear và set data mới
            transferData.clear();
            if (data != null) {
                transferData.putAll(data);
            }
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screen.getFxmlPath()));
            Parent root = loader.load();
            
            // Lấy controller và inject dependencies
            Object controller = loader.getController();
            
            // Inject Spring services nếu controller có setter methods
            if (springContext != null) {
                injectAuthService(controller);
                injectQuizService(controller);
                injectResultService(controller);
            }
            
            // Inject data nếu implements NavigationAware
            if (controller instanceof NavigationAware) {
                ((NavigationAware) controller).onNavigatedTo(transferData);
            }
            
            // ═══════════════════════════════════════════════════════════
            // CRITICAL: HARD LAYOUT RESET PROTOCOL
            // ═══════════════════════════════════════════════════════════
            
            // SNAPSHOT STATE
            final boolean wasMaximized = primaryStage.isMaximized();
            final boolean wasShowing = primaryStage.isShowing();
            
            // CREATE NEW SCENE
            Scene newScene = new Scene(root);
            
            // STEP 1: AGGRESSIVE BINDING (Before setting scene)
            if (root instanceof Region) {
                Region region = (Region) root;
                region.prefWidthProperty().bind(newScene.widthProperty());
                region.prefHeightProperty().bind(newScene.heightProperty());
                region.setMinWidth(0);
                region.setMinHeight(0);
                region.setMaxWidth(Double.MAX_VALUE);
                region.setMaxHeight(Double.MAX_VALUE);
            }
            
            // STEP 2: SET SCENE
            primaryStage.setScene(newScene);
            primaryStage.setTitle(screen.getTitle());
            
            // Show stage if not showing
            if (!wasShowing) {
                primaryStage.show();
            }
            
            // STEP 3: THE "WINDOW SHAKE" (Force OS Event)
            forceLayoutRefresh(primaryStage, root, wasMaximized);
            
        } catch (IOException e) {
            e.printStackTrace();
            JavaFXHelper.showError("Lỗi điều hướng", 
                "Không thể chuyển đến màn hình: " + screen.getTitle() + "\n" + e.getMessage());
        }
    }
    
    /**
     * FORCE LAYOUT REFRESH using "Window Shake" technique
     * 
     * This method implements a HARD RESET of the layout system by:
     * 1. Toggling maximized state (false->true) to force OS resize event
     * 2. Forcing CSS application and layout recalculation
     * 3. Setting absolute position as fail-safe
     * 
     * @param stage The Stage to refresh
     * @param root The root Parent node
     * @param wasMaximized Whether the stage was maximized before
     */
    private void forceLayoutRefresh(Stage stage, Parent root, boolean wasMaximized) {
        final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        
        Platform.runLater(() -> {
            if (wasMaximized) {
                // ═══════════════════════════════════════════════════════════
                // THE "WINDOW SHAKE" - Force OS Resize Event
                // ═══════════════════════════════════════════════════════════
                
                // A. Toggle Maximized State (false -> true)
                // This forces Windows/macOS to dispatch resize events
                stage.setMaximized(false);
                stage.setMaximized(true);
                
                // B. Force Layout Engine Wake-Up
                root.applyCss();
                root.layout();
                
                // C. Hard Reset Position (Fail-safe)
                stage.setX(screenBounds.getMinX());
                stage.setY(screenBounds.getMinY());
                stage.setWidth(screenBounds.getWidth());
                stage.setHeight(screenBounds.getHeight());
                
                // Double-check enforcement (nested runLater for stubborn platforms)
                Platform.runLater(() -> {
                    // Force another layout pulse after OS processes maximize
                    root.applyCss();
                    root.layout();
                    
                    // Ensure maximized state is maintained
                    if (!stage.isMaximized()) {
                        stage.setMaximized(false);
                        stage.setMaximized(true);
                    }
                    
                    // Final position enforcement
                    stage.setX(screenBounds.getMinX());
                    stage.setY(screenBounds.getMinY());
                    stage.setWidth(screenBounds.getWidth());
                    stage.setHeight(screenBounds.getHeight());
                    
                    // Final layout pulse
                    root.applyCss();
                    root.layout();
                });
            } else {
                // If not maximized, still force layout refresh
                root.applyCss();
                root.layout();
            }
        });
    }
    
    /**
     * Quay lại màn hình trước đó
     * @return true nếu có thể quay lại, false nếu không có history
     */
    public boolean goBack() {
        if (navigationHistory.isEmpty()) {
            return false;
        }
        
        NavigationState previousState = navigationHistory.pop();
        navigateTo(previousState.getScreen(), previousState.getData(), false);
        return true;
    }
    
    /**
     * Chuyển về màn hình Login và clear toàn bộ history
     */
    public void navigateToLogin() {
        navigationHistory.clear();
        controllerCache.clear();
        transferData.clear();
        navigateTo(AppScreen.LOGIN, null, false);
    }
    
    /**
     * Chuyển về màn hình Dashboard phù hợp dựa trên role
     * @param role Role của user ("TEACHER" hoặc "STUDENT")
     */
    public void navigateToDashboard(String role) {
        navigationHistory.clear(); // Clear history khi vào dashboard
        
        if ("LECTURER".equalsIgnoreCase(role) || "TEACHER".equalsIgnoreCase(role)) {
            navigateTo(AppScreen.TEACHER_DASHBOARD, null, false);
        } else if ("STUDENT".equalsIgnoreCase(role)) {
            navigateTo(AppScreen.STUDENT_DASHBOARD, null, false);
        } else {
            JavaFXHelper.showError("Lỗi", "Role không hợp lệ: " + role);
            navigateToLogin();
        }
    }
    
    /**
     * Thêm dữ liệu vào transfer data
     * @param key Key của dữ liệu
     * @param value Giá trị dữ liệu
     */
    public void putData(String key, Object value) {
        transferData.put(key, value);
    }
    
    /**
     * Lấy dữ liệu từ transfer data
     * @param key Key của dữ liệu
     * @return Giá trị hoặc null nếu không tồn tại
     */
    public Object getData(String key) {
        return transferData.get(key);
    }
    
    /**
     * Lấy dữ liệu với kiểu generic
     * @param key Key của dữ liệu
     * @param type Class type để cast
     * @return Giá trị đã cast hoặc null
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> type) {
        Object value = transferData.get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * Clear toàn bộ transfer data
     */
    public void clearData() {
        transferData.clear();
    }
    
    /**
     * Lấy primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Inject AuthService vào controller (nếu có setAuthService method)
     */
    private void injectAuthService(Object controller) {
        try {
            // Lấy AuthService từ Spring context
            Object authService = springContext.getBean("authService");
            
            // Tìm setAuthService method trong controller
            for (java.lang.reflect.Method method : controller.getClass().getMethods()) {
                if (method.getName().equals("setAuthService") && 
                    method.getParameterCount() == 1) {
                    method.invoke(controller, authService);
                    return;
                }
            }
            
        } catch (Exception e) {
            // Controller không có setAuthService method hoặc lỗi inject - OK, bỏ qua
            // Không log để tránh spam console
        }
    }
    
    /**
     * Inject QuizService vào controller (nếu có setQuizService method)
     */
    private void injectQuizService(Object controller) {
        try {
            // Lấy QuizService từ Spring context
            Object quizService = springContext.getBean("quizService");
            
            // Tìm setQuizService method trong controller
            for (java.lang.reflect.Method method : controller.getClass().getMethods()) {
                if (method.getName().equals("setQuizService") && 
                    method.getParameterCount() == 1) {
                    method.invoke(controller, quizService);
                    return;
                }
            }
            
        } catch (Exception e) {
            // Controller không có setQuizService method hoặc lỗi inject - OK, bỏ qua
            // Không log để tránh spam console
        }
    }
    
    /**
     * Inject ResultService vào controller (nếu có setResultService method)
     */
    private void injectResultService(Object controller) {
        try {
            // Lấy ResultService từ Spring context
            Object resultService = springContext.getBean("resultService");
            
            // Tìm setResultService method trong controller
            for (java.lang.reflect.Method method : controller.getClass().getMethods()) {
                if (method.getName().equals("setResultService") && 
                    method.getParameterCount() == 1) {
                    method.invoke(controller, resultService);
                    return;
                }
            }
            
        } catch (Exception e) {
            // Controller không có setResultService method hoặc lỗi inject - OK, bỏ qua
            // Không log để tránh spam console
        }
    }
    
    /**
     * Xác định màn hình hiện tại (helper method)
     */
    private AppScreen getCurrentScreen() {
        // Logic để detect current screen từ scene
        // Có thể implement bằng cách lưu current screen vào biến
        return null; // Simplified version
    }
    
    /**
     * Kiểm tra có thể quay lại không
     */
    public boolean canGoBack() {
        return !navigationHistory.isEmpty();
    }
    
    /**
     * Clear navigation history
     */
    public void clearHistory() {
        navigationHistory.clear();
    }
    
    /**
     * Inner class để lưu trạng thái navigation
     */
    private static class NavigationState {
        private final AppScreen screen;
        private final Map<String, Object> data;
        
        public NavigationState(AppScreen screen, Map<String, Object> data) {
            this.screen = screen;
            this.data = data;
        }
        
        public AppScreen getScreen() {
            return screen;
        }
        
        public Map<String, Object> getData() {
            return data;
        }
    }
}
