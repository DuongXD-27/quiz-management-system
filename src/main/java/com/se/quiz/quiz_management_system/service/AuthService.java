package com.se.quiz.quiz_management_system.service;

import com.se.quiz.quiz_management_system.entity.Student;
import com.se.quiz.quiz_management_system.entity.Teacher;
import com.se.quiz.quiz_management_system.exception.AuthenticationException;
import com.se.quiz.quiz_management_system.exception.DuplicateUsernameException;
import com.se.quiz.quiz_management_system.model.Role;
import com.se.quiz.quiz_management_system.model.UserSession;
import com.se.quiz.quiz_management_system.repository.StudentRepository;
import com.se.quiz.quiz_management_system.repository.TeacherRepository;
import com.se.quiz.quiz_management_system.session.SessionManager;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * AuthService - Handles user authentication and registration
 */
@Service
public class AuthService {
    
    private static final int BCRYPT_WORK_FACTOR = 12;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    /**
     * Register a new user
     * @param username the username
     * @param password the plain text password
     * @param fullName the full name
     * @param role the role (LECTURER or STUDENT)
     * @return the created user's ID
     * @throws DuplicateUsernameException if username already exists
     */
    @Transactional
    public Long register(String username, String password, String fullName, Role role) {
        // Check for duplicate username in both tables
        if (teacherRepository.existsByUsername(username) || studentRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("Username '" + username + "' đã tồn tại trong hệ thống");
        }
        
        // Hash the password using BCrypt
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
        
        // Create user based on role
        if (role == Role.LECTURER) {
            Teacher teacher = new Teacher(username, passwordHash, fullName);
            teacher = teacherRepository.save(teacher);
            return teacher.getTeacherId();
        } else if (role == Role.STUDENT) {
            Student student = new Student(username, passwordHash, fullName, null);
            student = studentRepository.save(student);
            return student.getStudentId();
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    /**
 * Register a new teacher
 * @param username the username
 * @param password the plain text password
 * @param fullName the full name
 * @return the created teacher's ID
 * @throws DuplicateUsernameException if username already exists
 */
    @Transactional
    public Long registerTeacher(String username, String password, String fullName) {
        // Check for duplicate username in teacher table
        if (teacherRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("Username '" + username + "' đã tồn tại trong hệ thống");
        }
        
        // Hash the password using BCrypt
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
        
        // Create teacher
        Teacher teacher = new Teacher(username, passwordHash, fullName);
        teacher = teacherRepository.save(teacher);
        return teacher.getTeacherId();
    }
    
    /**
     * Register a new student with student code
     * @param username the username
     * @param password the plain text password
     * @param fullName the full name
     * @param studentCode the student code
     * @return the created student's ID
     * @throws DuplicateUsernameException if username already exists
     */
    @Transactional
    public Long registerStudent(String username, String password, String fullName, String studentCode) {
        // Check for duplicate username in both tables
        if (teacherRepository.existsByUsername(username) || studentRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("Username '" + username + "' đã tồn tại trong hệ thống");
        }
        
        // Hash the password using BCrypt
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
        
        // Create student
        Student student = new Student(username, passwordHash, fullName, studentCode);
        student = studentRepository.save(student);
        return student.getStudentId();
    }
    
    /**
     * Login a user
     * @param username the username
     * @param password the plain text password
     * @return UserSession if login successful
     * @throws AuthenticationException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public UserSession login(String username, String password) {
        // Try to find user in teacher table first
        Optional<Teacher> teacherOpt = teacherRepository.findByUsername(username);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            
            // Verify password
            if (BCrypt.checkpw(password, teacher.getPasswordHash())) {
                // Create session
                UserSession session = new UserSession(
                    teacher.getTeacherId(),
                    teacher.getUsername(),
                    Role.LECTURER,
                    teacher.getFullName()
                );
                
                // Set session in SessionManager
                SessionManager.setCurrentUserSession(session);
                
                return session;
            } else {
                throw new AuthenticationException("Sai mật khẩu");
            }
        }
        
        // Try to find user in student table
        Optional<Student> studentOpt = studentRepository.findByUsername(username);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            
            // Verify password
            if (BCrypt.checkpw(password, student.getPasswordHash())) {
                // Create session
                UserSession session = new UserSession(
                    student.getStudentId(),
                    student.getUsername(),
                    Role.STUDENT,
                    student.getFullName()
                );
                
                // Set session in SessionManager
                SessionManager.setCurrentUserSession(session);
                
                return session;
            } else {
                throw new AuthenticationException("Sai mật khẩu");
            }
        }
        
        // User not found in either table
        throw new AuthenticationException("Không tìm thấy username '" + username + "'");
    }
    
    /**
     * Get the current logged-in user session
     * @return UserSession of the current user, or null if not logged in
     */
    public UserSession getCurrentUser() {
        return SessionManager.getCurrentUserSession();
    }
    
    /**
     * Logout the current user
     */
    public void logout() {
        SessionManager.clearSession();
    }
}

