package com.se.quiz.quiz_management_system.navigation;

import com.se.quiz.quiz_management_system.util.JavaFXHelper;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.*;

    // NavigationManager - Singleton manages navigation between screens
    // Uses Finite State Machine (FSM) pattern with Window State Management
    
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
    
    // Get the instance of NavigationManager (Singleton)
    
    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }
    
    // Initialize NavigationManager with primary stage and Spring context
    // @param primaryStage The main stage of the application
    // @param springContext Spring ApplicationContext to inject dependencies
    
    public void initialize(Stage primaryStage, ApplicationContext springContext) {
        this.primaryStage = primaryStage;
        this.springContext = springContext;
    }
    
    // Navigate to a new screen (Simple version without data)
    // @param screen The destination screen
    
    public void switchScene(AppScreen screen) {
        navigateTo(screen, null, true);
    }
    
    // Navigate to a new screen
    // @param screen The destination screen
    
    public void navigateTo(AppScreen screen) {
        navigateTo(screen, null);
    }
    
    // Navigate to a new screen with data
    // @param screen The destination screen
    // @param data The data to pass (Map<String, Object>)
    
    public void navigateTo(AppScreen screen, Map<String, Object> data) {
        navigateTo(screen, data, true);
    }
    
    // Navigate to a new screen with data and option to add to history
    // @param screen The destination screen
    // @param data The data to pass (Map<String, Object>)
    // @param addToHistory Whether to add to history or not
    
    public void navigateTo(AppScreen screen, Map<String, Object> data, boolean addToHistory) {
        try {
            // Save current screen to history if needed
            if (addToHistory && primaryStage.getScene() != null) {
                AppScreen currentScreen = getCurrentScreen();
                if (currentScreen != null) {
                    navigationHistory.push(new NavigationState(currentScreen, new HashMap<>(transferData)));
                }
            }
            
            // Clear and set new data
            transferData.clear();
            if (data != null) {
                transferData.putAll(data);
            }
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screen.getFxmlPath()));
            Parent root = loader.load();
            
            // Get controller and inject dependencies
            Object controller = loader.getController();
            
            // Inject Spring services if controller has setter methods
            if (springContext != null) {
                injectAuthService(controller);
                injectQuizService(controller);
                injectResultService(controller);
            }
            
            // Inject data if implements NavigationAware
            if (controller instanceof NavigationAware) {
                ((NavigationAware) controller).onNavigatedTo(transferData);
            }
            
            // CRITICAL: HARD LAYOUT RESET PROTOCOL
            
            // SNAPSHOT STATE
            final boolean wasMaximized = primaryStage.isMaximized();
            final boolean wasShowing = primaryStage.isShowing();
            
            // CREATE NEW SCENE
            Scene newScene = new Scene(root);
            enhanceScrollSpeed(newScene);
            
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
    
    // FORCE LAYOUT REFRESH using "Window Shake" technique
    
    // This method implements a HARD RESET of the layout system by:
    // 1. Toggling maximized state (false->true) to force OS resize event
    // 2. Forcing CSS application and layout recalculation
    // 3. Setting absolute position as fail-safe
    // @param stage The Stage to refresh
    // @param root The root Parent node
    // @param wasMaximized Whether the stage was maximized before
    
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
    
    // Increase scroll speed for all ScrollPane in scene
    
    private void enhanceScrollSpeed(Scene scene) {
        final double speedMultiplier = 2.5; // higher = faster scroll
        
        scene.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() == 0) {
                return;
            }
            
            Node target = (Node) event.getTarget();
            ScrollPane scrollPane = null;
            
            // Tìm ScrollPane cha gần nhất của target
            while (target != null) {
                if (target instanceof ScrollPane) {
                    scrollPane = (ScrollPane) target;
                    break;
                }
                target = target.getParent();
            }
            
            if (scrollPane == null || scrollPane.getContent() == null) {
                return;
            }
            
            double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
            if (contentHeight <= 0) {
                return;
            }
            
            // JavaFX: deltaY dương = cuộn lên → vvalue giảm
            double delta = (event.getDeltaY() / contentHeight) * speedMultiplier;
            double newValue = scrollPane.getVvalue() - delta;
            newValue = Math.min(1.0, Math.max(0.0, newValue));
            
            scrollPane.setVvalue(newValue);
            event.consume();
        });
    }
    
    // Go back to previous screen
    // @return true if can go back, false if no history
    
    public boolean goBack() {
        if (navigationHistory.isEmpty()) {
            return false;
        }
        
        NavigationState previousState = navigationHistory.pop();
        navigateTo(previousState.getScreen(), previousState.getData(), false);
        return true;
    }
    
    // Navigate to Login screen and clear all history
    
    public void navigateToLogin() {
        navigationHistory.clear();
        controllerCache.clear();
        transferData.clear();
        navigateTo(AppScreen.LOGIN, null, false);
    }
    
    // Navigate to Dashboard screen based on role
    // @param role Role of user ("TEACHER" or "STUDENT")
    
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
    
    // Add data to transfer data
    // @param key Key of data
    // @param value Value of data
    
    public void putData(String key, Object value) {
        transferData.put(key, value);
    }
    
    // Get data from transfer data
    // @param key Key of data
    // @return Value or null if not exists
    
    public Object getData(String key) {
        return transferData.get(key);
    }
    
    // Get data with generic type
    // @param key Key of data
    // @param type Class type to cast
    // @return Value or null if not exists
    
    @SuppressWarnings("unchecked")
    public <T> T getData(String key, Class<T> type) {
        Object value = transferData.get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    // Clear all transfer data
    
    public void clearData() {
        transferData.clear();
    }
    
    // Get primary stage
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    // Inject AuthService into controller (if has setAuthService method)
    
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

        }
    }
    
    // Inject QuizService into controller (if has setQuizService method)
    
    private void injectQuizService(Object controller) {
        try {
            // Get QuizService from Spring context
            Object quizService = springContext.getBean("quizService");
            
            // Find setQuizService method in controller
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
    
    // Inject ResultService into controller (if has setResultService method)
    
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
        }
    }
    
    // Determine current screen (helper method)
    
    private AppScreen getCurrentScreen() {
        // Logic to detect current screen from scene
        // Can implement by saving current screen into variable
        return null; // Simplified version
    }
    
    // Check if can go back
    
    public boolean canGoBack() {
        return !navigationHistory.isEmpty();
    }
    
    // Clear navigation history
    
    public void clearHistory() {
        navigationHistory.clear();
    }
    
    // Inner class to save navigation state
    
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
