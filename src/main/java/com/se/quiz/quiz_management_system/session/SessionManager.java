package com.se.quiz.quiz_management_system.session;

import com.se.quiz.quiz_management_system.model.Role;
import com.se.quiz.quiz_management_system.model.UserSession;

/**
 * SessionManager - Singleton pattern to manage current user session
 * Stores the currently logged-in user information
 */
public class SessionManager {
    
    private static UserSession currentUserSession;
    
    // Private constructor to prevent instantiation
    private SessionManager() {
    }
    
    /**
     * Set the current user session (called after successful login)
     * @param session the UserSession to set
     */
    public static void setCurrentUserSession(UserSession session) {
        currentUserSession = session;
    }
    
    /**
     * Get the current user session
     * @return the current UserSession, or null if no user is logged in
     */
    public static UserSession getCurrentUserSession() {
        return currentUserSession;
    }
    
    /**
     * Clear the current session (called on logout)
     */
    public static void clearSession() {
        currentUserSession = null;
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return currentUserSession != null;
    }
    
    /**
     * Check if the current user is a lecturer
     * @return true if current user is a lecturer, false otherwise
     */
    public static boolean isLecturer() {
        return isLoggedIn() && currentUserSession.getRole() == Role.LECTURER;
    }
    
    /**
     * Check if the current user is a student
     * @return true if current user is a student, false otherwise
     */
    public static boolean isStudent() {
        return isLoggedIn() && currentUserSession.getRole() == Role.STUDENT;
    }
    
    /**
     * Get the current user's ID
     * @return the user ID, or null if not logged in
     */
    public static Long getCurrentUserId() {
        return isLoggedIn() ? currentUserSession.getUserId() : null;
    }
    
    /**
     * Get the current user's username
     * @return the username, or null if not logged in
     */
    public static String getCurrentUsername() {
        return isLoggedIn() ? currentUserSession.getUsername() : null;
    }
    
    /**
     * Get the current user's full name
     * @return the full name, or null if not logged in
     */
    public static String getCurrentUserFullName() {
        return isLoggedIn() ? currentUserSession.getFullName() : null;
    }
}

