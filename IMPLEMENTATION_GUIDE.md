# Quiz Management System - Authentication & Class Management

## Tổng quan
Hệ thống quản lý quiz với tính năng xác thực, phân quyền và quản lý lớp học cho giảng viên và sinh viên.

## Cấu trúc dự án

### 1. Models & Entities

#### Enums
- `Role.java` - Phân quyền: LECTURER, STUDENT

#### Models
- `UserSession.java` - Lưu thông tin phiên đăng nhập
- `ImportResult.java` - Kết quả import CSV

#### Entities (JPA)
- `Teacher.java` - Bảng teacher
- `Student.java` - Bảng student  
- `ClassEntity.java` - Bảng classes
- `ClassStudent.java` - Bảng class_students (junction table)

### 2. Repositories (Spring Data JPA)
- `TeacherRepository.java` - CRUD cho teacher
- `StudentRepository.java` - CRUD cho student
- `ClassRepository.java` - CRUD cho classes
- `ClassStudentRepository.java` - CRUD cho class_students

### 3. Services

#### AuthService
```java
// Đăng ký người dùng mới
Long register(String username, String password, String fullName, Role role)

// Đăng ký sinh viên với mã sinh viên
Long registerStudent(String username, String password, String fullName, String studentCode)

// Đăng nhập
UserSession login(String username, String password)

// Đăng xuất
void logout()
```

#### ClassService
```java
// Tạo lớp mới
ClassEntity createClass(String name, String description)

// Lấy danh sách lớp của giảng viên
List<ClassEntity> getClassesOfLecturer()

// Xóa lớp
void deleteClass(Long classId)

// Lấy danh sách sinh viên trong lớp
List<Student> getStudentsOfClass(Long classId)

// Thêm sinh viên vào lớp
ClassStudent addStudentToClass(Long classId, Long studentId)
```

#### StudentImportService
```java
// Import sinh viên từ file CSV
ImportResult importStudentsFromCsv(Long classId, File csvFile)

// Import với BufferedReader (không dùng OpenCSV)
ImportResult importStudentsFromCsvManual(Long classId, File csvFile)
```

### 4. Session Management

#### SessionManager (Singleton)
```java
// Quản lý phiên đăng nhập hiện tại
static void setCurrentUserSession(UserSession session)
static UserSession getCurrentUserSession()
static void clearSession()

// Utility methods
static boolean isLoggedIn()
static boolean isLecturer()
static boolean isStudent()
static Long getCurrentUserId()
static String getCurrentUsername()
```

### 5. Authorization

#### PermissionUtil
```java
// Kiểm tra quyền
static boolean canEditQuestions()
static boolean canViewResults()
static boolean canTakeQuiz()
static boolean canManageClasses()
static boolean canImportStudents()
static boolean canCreateQuiz()
static boolean canDeleteQuiz()
static boolean canAssignQuiz()
```

### 6. JavaFX Utilities

#### JavaFXHelper
```java
// Chọn file CSV
static File chooseCSVFile(Stage stage)

// Hiển thị alert
static void showAlert(Alert.AlertType type, String title, String message)
static void showInfo(String title, String message)
static void showError(String title, String message)
static void showWarning(String title, String message)
static boolean showConfirmation(String title, String message)

// Hiển thị kết quả import
static void showImportResultDialog(ImportResult result)
```

### 7. Exceptions
- `AuthenticationException` - Lỗi xác thực
- `DuplicateUsernameException` - Username đã tồn tại
- `UnauthorizedException` - Không có quyền
- `ResourceNotFoundException` - Không tìm thấy resource

## Demo BCrypt

### Chạy BCrypt POC
```bash
# Compile và chạy
mvn compile
mvn exec:java -Dexec.mainClass="com.se.quiz.demo.BcryptDemo"
```

Demo sẽ:
1. Nhập password từ console
2. Hash password bằng BCrypt
3. Hiển thị hash
4. Nhập lại password để verify
5. Hiển thị "Đúng" hoặc "Sai"

## Cấu hình Database

### PostgreSQL
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/quiz_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Tạo database
```sql
CREATE DATABASE quiz_db;
```

### Schema sẽ tự động tạo bởi Hibernate
- JPA với `spring.jpa.hibernate.ddl-auto=update` sẽ tự động tạo/cập nhật bảng

## Import CSV Sinh viên

### Format CSV
```csv
username,full_name,student_code
student1,Nguyen Van A,SV001
student2,Tran Thi B,SV002
```

### Quy tắc Import
1. Dòng đầu là header (tự động bỏ qua nếu có "username")
2. Nếu username đã tồn tại → dùng user hiện có
3. Nếu username chưa tồn tại → tạo mới với password mặc định: `123456`
4. Kiểm tra duplicate trong lớp
5. Báo lỗi chi tiết cho từng dòng

### Sử dụng trong UI (JavaFX)
```java
// 1. Chọn file
File csvFile = JavaFXHelper.chooseCSVFile(stage);
if (csvFile != null) {
    // 2. Import
    ImportResult result = studentImportService.importStudentsFromCsv(classId, csvFile);
    
    // 3. Hiển thị kết quả
    JavaFXHelper.showImportResultDialog(result);
}
```

## Tích hợp với UI (Hoàng)

### Login Screen
```java
try {
    UserSession session = authService.login(username, password);
    // Chuyển đến màn hình chính
    navigateToMainScreen();
} catch (AuthenticationException e) {
    JavaFXHelper.showError("Lỗi đăng nhập", e.getMessage());
}
```

### Register Screen
```java
try {
    Long userId = authService.register(username, password, fullName, role);
    JavaFXHelper.showSuccess("Thành công", "Đăng ký thành công!");
} catch (DuplicateUsernameException e) {
    JavaFXHelper.showError("Lỗi", e.getMessage());
}
```

### Class Management Screen
```java
// Load danh sách lớp
List<ClassEntity> classes = classService.getClassesOfLecturer();

// Tạo lớp mới
ClassEntity newClass = classService.createClass(name, description);

// Xem sinh viên trong lớp
List<Student> students = classService.getStudentsOfClass(classId);

// Import CSV
File csvFile = JavaFXHelper.chooseCSVFile(stage);
ImportResult result = studentImportService.importStudentsFromCsv(classId, csvFile);
JavaFXHelper.showImportResultDialog(result);
```

### Phân quyền UI
```java
// Hiển thị/ẩn controls dựa trên quyền
createQuizButton.setVisible(PermissionUtil.canEditQuestions());
manageClassesButton.setVisible(PermissionUtil.canManageClasses());
takeQuizButton.setVisible(PermissionUtil.canTakeQuiz());
viewResultsButton.setVisible(PermissionUtil.canViewResults());

// Disable controls
createQuizButton.setDisable(!PermissionUtil.canEditQuestions());
```

## Bảo mật

### BCrypt
- Work factor: 12 (cân bằng giữa bảo mật và hiệu suất)
- Mỗi hash tạo ra salt ngẫu nhiên
- Password hash có độ dài 60 ký tự

### Session
- Session lưu trong memory (singleton)
- Cần implement session timeout nếu cần
- Logout xóa session hoàn toàn

### Authorization
- Mọi service method đều kiểm tra quyền
- Throw `UnauthorizedException` nếu không có quyền
- UI sử dụng `PermissionUtil` để ẩn/hiện controls

## Testing

### Test BCrypt POC
```bash
mvn exec:java -Dexec.mainClass="com.se.quiz.demo.BcryptDemo"
```

### Test với sample CSV
File `sample_students.csv` đã được tạo sẵn để test import.

### Test Sequence
1. Chạy BCrypt demo
2. Start ứng dụng Spring Boot
3. Test đăng ký lecturer
4. Test đăng nhập
5. Test tạo lớp
6. Test import CSV
7. Test xem danh sách sinh viên

## Dependencies

### Maven (pom.xml)
```xml
<!-- BCrypt -->
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>

<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.1</version>
</dependency>

<!-- OpenCSV -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.9</version>
</dependency>
```

## Lưu ý

1. **Database**: Cần tạo database `quiz_db` trước khi chạy
2. **Password mặc định**: Sinh viên import từ CSV có password: `123456`
3. **Session**: Chỉ cho phép 1 user đăng nhập cùng lúc (desktop app)
4. **CSV Format**: Đảm bảo file CSV đúng format, UTF-8 encoding
5. **Transaction**: Các operation quan trọng đều được wrap trong transaction

## Liên hệ & Hỗ trợ

Nếu có vấn đề khi tích hợp, liên hệ team để được hỗ trợ.

