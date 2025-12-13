package com.se.quiz.quiz_management_system.repository;

import com.se.quiz.quiz_management_system.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ClassEntity
 */
@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    
    /**
     * Find all classes belonging to a specific lecturer
     * @param lecturerId the lecturer's ID
     * @return List of classes
     */
    List<ClassEntity> findByLecturerId(Long lecturerId);
}

