package com.se.quiz.quiz_management_system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// temporary

/**
 * ClassEntity - Maps to classes table
 * Named ClassEntity to avoid conflict with java.lang.Class
 */
@Entity
@Table(name = "classes")
public class ClassEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long classId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "lecturer_id", nullable = false)
    private Long lecturerId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public ClassEntity() {
    }
    
    public ClassEntity(String name, String description, Long lecturerId) {
        this.name = name;
        this.description = description;
        this.lecturerId = lecturerId;
    }
    
    // Getters and Setters
    
    public Long getClassId() {
        return classId;
    }
    
    public void setClassId(Long classId) {
        this.classId = classId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getLecturerId() {
        return lecturerId;
    }
    
    public void setLecturerId(Long lecturerId) {
        this.lecturerId = lecturerId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "ClassEntity{" +
                "classId=" + classId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", lecturerId=" + lecturerId +
                ", createdAt=" + createdAt +
                '}';
    }
}

