package com.se.quiz.quiz_management_system.repository;

import com.se.quiz.quiz_management_system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Student entity
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    // Find student by username
    // @param username the username to search for
    // @return Optional containing the student if found
    
    Optional<Student> findByUsername(String username);
    
    // Check if a student with the given username exists
    // @param username the username to check
    // @return true if exists, false otherwise
    
    boolean existsByUsername(String username);
}

