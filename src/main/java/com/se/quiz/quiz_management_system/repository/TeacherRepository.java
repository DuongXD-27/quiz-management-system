package com.se.quiz.quiz_management_system.repository;

import com.se.quiz.quiz_management_system.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Teacher entity
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    
    /**
     * Find teacher by username
     * @param username the username to search for
     * @return Optional containing the teacher if found
     */
    Optional<Teacher> findByUsername(String username);
    
    /**
     * Check if a teacher with the given username exists
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);
}

