# Authentication & Class Management - Implementation Complete

## ✅ Hoàn thành tất cả các yêu cầu

### 1. BCrypt POC ✅
- **File**: `src/main/java/com/se/quiz/demo/BcryptDemo.java`
- **Chạy**: `mvn exec:java -Dexec.mainClass="com.se.quiz.demo.BcryptDemo"`
- **Chức năng**:
  - Nhập password từ console
  - Hash bằng BCrypt và hiển thị
  - Nhập lại để verify → in "Đúng/Sai"
  - Demo: cùng password tạo hash khác nhau

### 2. Dependencies ✅
Đã thêm vào `pom.xml`:
- `jbcrypt:0.4` - Password hashing
- `javafx-controls:21.0.1` - JavaFX UI
- `opencsv:5.9` - CSV parsing

### 3. Models ✅
- `Role` enum: LECTURER, STUDENT
- `UserSession`: userId, username, role, fullName
- `ImportResult`: successCount, errorCount, errorMessages

### 4. JPA Entities ✅
- `Teacher`: teacherId, username, passwordHash, fullName
- `Student`: studentId, username, passwordHash, fullName, studentCode
- `ClassEntity`: classId, name, description, lecturerId, createdAt
- `ClassStudent`: id, classId, studentId (junction table)

### 5. Repositories ✅
- `TeacherRepository`: findByUsername(), existsByUsername()
- `StudentRepository`: findByUsername(), existsByUsername()
- `ClassRepository`: findByLecturerId()
- `ClassStudentRepository`: findByClassId(), existsByClassIdAndStudentId(), deleteByClassId()

### 6. SessionManager ✅
**File**: `src/main/java/com/se/quiz/quiz_management_system/session/SessionManager.java`

Singleton pattern với các methods:
- `setCurrentUserSession(UserSession)`
- `getCurrentUserSession()`
- `clearSession()`
- `isLecturer()`, `isStudent()`, `isLoggedIn()`
- `getCurrentUserId()`, `getCurrentUsername()`, `getCurrentUserFullName()`

### 7. AuthService ✅
**File**: `src/main/java/com/se/quiz/quiz_management_system/service/AuthService.java`

Methods:
- `register(username, password, fullName, role)` - Đăng ký user mới
- `registerStudent(username, password, fullName, studentCode)` - Đăng ký sinh viên
- `login(username, password)` - Đăng nhập với BCrypt verify
- `logout()` - Xóa session

**Tính năng**:
- Check duplicate username trong cả 2 bảng
- Hash password với BCrypt (work factor: 12)
- Tự động tạo UserSession khi login thành công
- Throw exception khi lỗi (AuthenticationException, DuplicateUsernameException)

### 8. PermissionUtil ✅
**File**: `src/main/java/com/se/quiz/quiz_management_system/util/PermissionUtil.java`

Static methods cho UI:
- `canEditQuestions()` - Chỉ LECTURER
- `canViewResults()` - Chỉ LECTURER
- `canTakeQuiz()` - Chỉ STUDENT
- `canManageClasses()` - Chỉ LECTURER
- `canImportStudents()` - Chỉ LECTURER
- Thêm: `canCreateQuiz()`, `canDeleteQuiz()`, `canAssignQuiz()`

### 9. ClassService ✅
**File**: `src/main/java/com/se/quiz/quiz_management_system/service/ClassService.java`

Methods:
- `createClass(name, description)` - Tạo lớp mới
- `getClassesOfLecturer()` - Lấy lớp của giảng viên hiện tại
- `deleteClass(classId)` - Xóa lớp (verify ownership)
- `getStudentsOfClass(classId)` - Lấy danh sách sinh viên
- `addStudentToClass(classId, studentId)` - Thêm sinh viên vào lớp
- `getClassById(classId)` - Lấy thông tin lớp

**Bảo mật**:
- Verify lecturer ownership
- Check permissions
- Cascade delete class_students khi xóa class

### 10. StudentImportService ✅
**File**: `src/main/java/com/se/quiz/quiz_management_system/service/StudentImportService.java`

Methods:
- `importStudentsFromCsv(classId, csvFile)` - Import với OpenCSV
- `importStudentsFromCsvManual(classId, csvFile)` - Import với BufferedReader

**CSV Format**:
```csv
username,full_name,student_code
student1,Nguyen Van A,SV001
```

**Tính năng**:
- Tự động bỏ qua header row
- Check duplicate username
- Tạo student mới nếu chưa tồn tại (password mặc định: "123456")
- Check duplicate trong lớp
- Báo lỗi chi tiết cho từng dòng
- Return ImportResult với statistics

### 11. JavaFXHelper ✅
**File**: `src/main/java/com/se/quiz/quiz_management_system/util/JavaFXHelper.java`

Methods:
- `chooseCSVFile(Stage)` - FileChooser cho CSV
- `showAlert()`, `showInfo()`, `showError()`, `showWarning()`, `showSuccess()`
- `showConfirmation()` - Confirmation dialog
- `showImportResultDialog(ImportResult)` - Hiển thị kết quả import với details
- `showErrorWithException()` - Error với stack trace

### 12. Database Config ✅
**File**: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/quiz_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 13. Exceptions ✅
Custom exceptions:
- `AuthenticationException` - Lỗi xác thực
- `DuplicateUsernameException` - Username trùng
- `UnauthorizedException` - Không có quyền
- `ResourceNotFoundException` - Không tìm thấy

## 📁 File Structure

```
src/main/java/com/se/quiz/quiz_management_system/
├── demo/
│   └── BcryptDemo.java                    # POC console app
├── entity/
│   ├── Teacher.java
│   ├── Student.java
│   ├── ClassEntity.java
│   └── ClassStudent.java
├── model/
│   ├── Role.java
│   ├── UserSession.java
│   └── ImportResult.java
├── repository/
│   ├── TeacherRepository.java
│   ├── StudentRepository.java
│   ├── ClassRepository.java
│   └── ClassStudentRepository.java
├── service/
│   ├── AuthService.java
│   ├── ClassService.java
│   └── StudentImportService.java
├── session/
│   └── SessionManager.java
├── util/
│   ├── PermissionUtil.java
│   └── JavaFXHelper.java
└── exception/
    ├── AuthenticationException.java
    ├── DuplicateUsernameException.java
    ├── UnauthorizedException.java
    └── ResourceNotFoundException.java
```

## 🚀 Cách sử dụng

### Test BCrypt POC
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.se.quiz.demo.BcryptDemo"
```

### Setup Database
```sql
CREATE DATABASE quiz_db;
```

### Chạy ứng dụng
```bash
mvn spring-boot:run
```

### Integration với UI của Hoàng

#### Login
```java
UserSession session = authService.login(username, password);
// Session tự động set vào SessionManager
```

#### Create Class
```java
ClassEntity newClass = classService.createClass(name, description);
```

#### Import Students
```java
File csvFile = JavaFXHelper.chooseCSVFile(stage);
ImportResult result = studentImportService.importStudentsFromCsv(classId, csvFile);
JavaFXHelper.showImportResultDialog(result);
```

#### Permission Checks
```java
if (PermissionUtil.canManageClasses()) {
    // Show class management UI
}
```

## 📋 Sample CSV
File `sample_students.csv` đã được tạo sẵn để test.

## 📖 Documentation
Xem `IMPLEMENTATION_GUIDE.md` để biết chi tiết đầy đủ.

## ⚠️ Lưu ý

1. **Database**: Cần PostgreSQL đang chạy và đã tạo database `quiz_db`
2. **Password**: Import CSV dùng password mặc định "123456"
3. **Session**: Desktop app - chỉ 1 user đăng nhập tại 1 thời điểm
4. **Permissions**: Tất cả service methods đều check quyền

## ✨ Hoàn thành 100%

Tất cả 12 tasks đã được implement đầy đủ theo plan!

