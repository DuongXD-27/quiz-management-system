package com.se.quiz.quiz_management_system.util;

import com.se.quiz.quiz_management_system.session.SessionManager;

/**
 * PermissionUtil - Utility class for checking user permissions
 * Used primarily by UI layer to show/hide or enable/disable controls
 */
public class PermissionUtil {
    
    // Private constructor to prevent instantiation
    private PermissionUtil() {
    }
    
    /**
     * Check if current user can edit questions
     * Only lecturers can edit questions
     * @return true if user has permission, false otherwise
     */
    public static boolean canEditQuestions() {
        return SessionManager.isLecturer();
    }
    
    /**
     * Check if current user can view results
     * Only lecturers can view results
     * @return true if user has permission, false otherwise
     */
    public static boolean canViewResults() {
        return SessionManager.isLecturer();
    }
    
    /**
     * Check if current user can take a quiz
     * Only students can take quizzes
     * @return true if user has permission, false otherwise
     */
    public static boolean canTakeQuiz() {
        return SessionManager.isStudent();
    }
    
    /**
     * Check if current user can manage classes
     * Only lecturers can manage classes
     * @return true if user has permission, false otherwise
     */
    public static boolean canManageClasses() {
        return SessionManager.isLecturer();
    }
    
    /**
     * Check if current user can import students
     * Only lecturers can import students
     * @return true if user has permission, false otherwise
     */
    public static boolean canImportStudents() {
        return SessionManager.isLecturer();
    }
    
    /**
     * Check if current user can create quizzes
     * Only lecturers can create quizzes
     * @return true if user has permission, false otherwise
     */
    public static boolean canCreateQuiz() {
        return SessionManager.isLecturer();
    }
    
    /**
     * Check if current user can delete quizzes
     * Only lecturers can delete quizzes
     * @return true if user has permission, false otherwise
     */
    public static boolean canDeleteQuiz() {
        return SessionManager.isLecturer();
    }
    
    /**
     * Check if current user can assign quizzes to classes
     * Only lecturers can assign quizzes
     * @return true if user has permission, false otherwise
     */
    public static boolean canAssignQuiz() {
        return SessionManager.isLecturer();
    }
}

