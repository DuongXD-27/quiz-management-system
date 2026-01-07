# Navigation System - Usage Guide

## Overview
Hệ thống điều hướng sử dụng **Finite State Machine (FSM)** pattern để quản lý việc chuyển đổi giữa các màn hình trong ứng dụng Quiz Management System.

---

## Core Components

### 1. AppScreen (Enum)
Định nghĩa tất cả các màn hình trong ứng dụng và mapping tới file FXML.

```java
public enum AppScreen {
    LOGIN("/view/Login.fxml", "Quiz Management System - Đăng nhập"),
    TEACHER_DASHBOARD("/view/TeacherDashboard.fxml", "Teacher Dashboard"),
    // ... other screens
}
```

### 2. NavigationManager (Singleton)
Class trung tâm quản lý việc chuyển đổi màn hình, hỗ trợ:
- Chuyển màn hình với/không dữ liệu
- Navigation history (back navigation)
- Tự động inject Spring beans vào Controller

### 3. NavigationAware (Interface)
Controller implement interface này để nhận dữ liệu khi được navigate tới.

```java
public interface NavigationAware {
    void onNavigatedTo(Map<String, Object> data);
}
```

---

## Usage Examples

### 1. Basic Navigation (Không truyền dữ liệu)

```java
// Trong Controller
@FXML
private void handleButtonClick() {
    NavigationManager.getInstance().navigateTo(AppScreen.TEACHER_DASHBOARD);
}
```

**Ví dụ thực tế:** `LoginController` -> `TeacherDashboard`
```java
// LoginController.java
NavigationManager.getInstance().navigateToDashboard(role);
```

### 2. Navigation với Data Passing

**Step 1:** Chuẩn bị dữ liệu và navigate
```java
// AvailableQuizzesController.java
private void handleJoinQuiz(QuizData quiz, Stage stage) {
    Map<String, Object> data = new HashMap<>();
    data.put("quizId", quiz.getQuizId());
    data.put("quizTitle", quiz.getSubject());
    
    NavigationManager.getInstance().navigateTo(AppScreen.TAKE_QUIZ, data);
}
```

**Step 2:** Controller đích implement `NavigationAware` để nhận dữ liệu
```java
// TakeQuizController.java
public class TakeQuizController implements Initializable, NavigationAware {
    
    private Long quizId;
    
    @Override
    public void onNavigatedTo(Map<String, Object> data) {
        if (data != null && data.containsKey("quizId")) {
            this.quizId = (Long) data.get("quizId");
            // Load quiz từ database sử dụng quizId
            loadQuiz(quizId);
        }
    }
}
```

### 3. Logout Navigation

```java
@FXML
private void handleLogout() {
    // Clear session
    authService.logout();
    
    // Navigate về Login và clear history
    NavigationManager.getInstance().navigateToLogin();
}
```

### 4. Back Navigation

```java
@FXML
private void handleBack() {
    // Option 1: Navigate về màn hình cụ thể
    NavigationManager.getInstance().navigateTo(AppScreen.STUDENT_DASHBOARD);
    
    // Option 2: Sử dụng history stack (nếu đã implement)
    if (NavigationManager.getInstance().canGoBack()) {
        NavigationManager.getInstance().goBack();
    }
}
```

---

## Transition Logic Map (FSM)

### Login Flow
```
Login Screen
  ├─ (Role = Teacher) → Teacher Dashboard
  └─ (Role = Student) → Student Dashboard
```

### Teacher Flow
```
Teacher Dashboard
  ├─ "Create Quiz" → Create Question Screen
  ├─ "Choose Quizzes" → Quiz List Screen
  │   ├─ "Assign" → Add Student to Quiz Screen
  │   └─ "View Result" → Quiz Result Screen
  └─ "Logout" → Login Screen
```

### Student Flow
```
Student Dashboard
  ├─ "Take Quiz" → Available Quizzes Screen
  │   └─ "Join Quiz" → Take Quiz Screen
  │       └─ "Submit" → Result Screen (QuizResult)
  ├─ "My Results" → Student My Results Screen
  └─ "Logout" → Login Screen
```

---

## Advanced Features

### 1. Pass Multiple Data Types
```java
Map<String, Object> data = new HashMap<>();
data.put("userId", 123L);
data.put("userName", "John Doe");
data.put("quizList", quizList); // List<Quiz>
data.put("options", options);   // Custom object

NavigationManager.getInstance().navigateTo(AppScreen.TARGET_SCREEN, data);
```

### 2. Generic Data Retrieval
```java
@Override
public void onNavigatedTo(Map<String, Object> data) {
    Long userId = (Long) data.get("userId");
    String userName = (String) data.get("userName");
    
    @SuppressWarnings("unchecked")
    List<Quiz> quizList = (List<Quiz>) data.get("quizList");
}
```

### 3. Conditional Navigation
```java
private void navigateBasedOnRole() {
    UserSession session = SessionManager.getCurrentUserSession();
    
    if (session.getRole() == Role.LECTURER) {
        NavigationManager.getInstance().navigateTo(AppScreen.TEACHER_DASHBOARD);
    } else {
        NavigationManager.getInstance().navigateTo(AppScreen.STUDENT_DASHBOARD);
    }
}
```

---

## Best Practices

### ✅ DO
1. **Sử dụng NavigationManager cho mọi navigation** thay vì load FXML manual
2. **Clear session trước khi logout**
3. **Validate dữ liệu nhận được** trong `onNavigatedTo()`
4. **Sử dụng AppScreen enum** thay vì hardcode path

### ❌ DON'T
1. **Không load FXML trực tiếp** trong controller
2. **Không truyền quá nhiều dữ liệu phức tạp** - nên query từ DB nếu cần
3. **Không giữ reference tới Stage** trong controller (NavigationManager quản lý)

---

## Troubleshooting

### Lỗi: "Cannot find FXML file"
- Kiểm tra path trong `AppScreen` enum
- Đảm bảo file FXML tồn tại trong `src/main/resources/view/`

### Lỗi: "Controller không nhận được dữ liệu"
- Đảm bảo controller implement `NavigationAware`
- Kiểm tra key name trong Map trùng khớp
- Verify dữ liệu được put trước khi navigate

### Lỗi: "Spring Bean không được inject"
- Đảm bảo `NavigationManager.initialize()` được gọi với `ApplicationContext`
- Check controller có annotation `@Component` hoặc được managed bởi Spring

---

## Integration với Spring Boot

NavigationManager tự động inject Spring beans vào controller thông qua `setControllerFactory`:

```java
// Trong NavigationManager.navigateTo()
if (springContext != null) {
    loader.setControllerFactory(springContext::getBean);
}
```

Controller cần được đăng ký là Spring Bean:
```java
@Component
public class MyController implements Initializable, NavigationAware {
    // Spring sẽ tự động inject dependencies
}
```

---

## Future Enhancements

1. **Animation Transitions**: Thêm hiệu ứng chuyển cảnh
2. **Screen Cache**: Cache controller để tăng performance
3. **Permission Guard**: Kiểm tra quyền trước khi navigate
4. **Deep Linking**: Support URL-based navigation

