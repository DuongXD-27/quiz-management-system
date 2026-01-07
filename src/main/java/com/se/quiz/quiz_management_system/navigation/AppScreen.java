package com.se.quiz.quiz_management_system.navigation;

    // AppScreen Enum - Định nghĩa tất cả các màn hình trong ứng dụng
    // Mapping từ FSM State sang FXML file
    
public enum AppScreen {
    // Authentication Screens
    LOGIN("/view/Login.fxml", "Quiz Management System - Đăng nhập"),
    REGISTER("/view/Register.fxml", "Quiz Management System - Đăng ký"),
    
    // Teacher Screens
    TEACHER_DASHBOARD("/view/TeacherDashboard.fxml", "Teacher Dashboard"),
    QUIZ_LIST("/view/QuizList.fxml", "Danh sách Quiz"),
    CREATE_QUESTION("/view/CreateQuestion.fxml", "Tạo câu hỏi mới"),
    ADD_STUDENT_TO_QUIZ("/view/AddStudentToQuiz.fxml", "Thêm sinh viên vào Quiz"),
    QUIZ_RESULT("/view/QuizResult.fxml", "Kết quả Quiz"),
    QUIZ_RESULTS_LIST("/view/QuizResultsListView.fxml", "Quiz Results Overview"),
    STUDENT_RESULTS("/view/StudentResults.fxml", "Kết quả sinh viên"),
    
    // Student Screens
    STUDENT_DASHBOARD("/view/StudentDashboard.fxml", "Student Dashboard"),
    AVAILABLE_QUIZZES("/view/AvailableQuizzes.fxml", "Quiz có thể làm"),
    TAKE_QUIZ("/view/TakeQuiz.fxml", "Làm bài Quiz"),
    STUDENT_MY_RESULTS("/view/StudentMyResults.fxml", "Kết quả của tôi");
    
    private final String fxmlPath;
    private final String title;
    
    AppScreen(String fxmlPath, String title) {
        this.fxmlPath = fxmlPath;
        this.title = title;
    }
    
    public String getFxmlPath() {
        return fxmlPath;
    }
    
    public String getTitle() {
        return title;
    }
}

