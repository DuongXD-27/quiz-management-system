package com.se.quiz.quiz_management_system.entity;

import jakarta.persistence.*;

// temporary

/**
 * ClassStudent entity - Maps to class_students table
 * Junction table for many-to-many relationship between classes and students
 */
@Entity
@Table(name = "class_students", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "student_id"}))
public class ClassStudent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "class_id", nullable = false)
    private Long classId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    public ClassStudent() {
    }
    
    public ClassStudent(Long classId, Long studentId) {
        this.classId = classId;
        this.studentId = studentId;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClassId() {
        return classId;
    }
    
    public void setClassId(Long classId) {
        this.classId = classId;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    @Override
    public String toString() {
        return "ClassStudent{" +
                "id=" + id +
                ", classId=" + classId +
                ", studentId=" + studentId +
                '}';
    }
}

