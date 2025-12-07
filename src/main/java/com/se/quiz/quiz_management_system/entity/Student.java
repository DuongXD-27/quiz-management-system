package com.se.quiz.quiz_management_system.entity;

import jakarta.persistence.*;
import java.util.List;

// temporary

/**
 * Student entity - Maps to student table
 */
@Entity
@Table(name = "student")
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;
    
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;
    
    @Column(name = "full_name", length = 100)
    private String fullName;
    
    @Column(name = "student_code", length = 20)
    private String studentCode;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentQuiz> studentQuizzes;
    
    public Student() {
    }
    
    public Student(String username, String passwordHash, String fullName, String studentCode) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.studentCode = studentCode;
    }
    
    // Getters and Setters
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getStudentCode() {
        return studentCode;
    }
    
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }
    
    public List<StudentQuiz> getStudentQuizzes() {
        return studentQuizzes;
    }
    
    public void setStudentQuizzes(List<StudentQuiz> studentQuizzes) {
        this.studentQuizzes = studentQuizzes;
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", studentCode='" + studentCode + '\'' +
                '}';
    }
}

