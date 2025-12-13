package com.se.quiz.quiz_management_system.service;

import com.se.quiz.quiz_management_system.entity.ClassEntity;
import com.se.quiz.quiz_management_system.entity.ClassStudent;
import com.se.quiz.quiz_management_system.entity.Student;
import com.se.quiz.quiz_management_system.exception.ResourceNotFoundException;
import com.se.quiz.quiz_management_system.exception.UnauthorizedException;
import com.se.quiz.quiz_management_system.repository.ClassRepository;
import com.se.quiz.quiz_management_system.repository.ClassStudentRepository;
import com.se.quiz.quiz_management_system.repository.StudentRepository;
import com.se.quiz.quiz_management_system.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassService - Handles class management operations
 */
@Service
public class ClassService {
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    /**
     * Create a new class
     * @param name the class name
     * @param description the class description
     * @return the created ClassEntity
     * @throws UnauthorizedException if user is not a lecturer or not logged in
     */
    @Transactional
    public ClassEntity createClass(String name, String description) {
        // Validate lecturer is logged in
        if (!SessionManager.isLecturer()) {
            throw new UnauthorizedException("Chỉ giảng viên mới có thể tạo lớp học");
        }
        
        Long lecturerId = SessionManager.getCurrentUserId();
        
        ClassEntity classEntity = new ClassEntity(name, description, lecturerId);
        return classRepository.save(classEntity);
    }
    
    /**
     * Get all classes of the current lecturer
     * @return List of ClassEntity
     * @throws UnauthorizedException if user is not a lecturer or not logged in
     */
    @Transactional(readOnly = true)
    public List<ClassEntity> getClassesOfLecturer() {
        // Validate lecturer is logged in
        if (!SessionManager.isLecturer()) {
            throw new UnauthorizedException("Chỉ giảng viên mới có thể xem danh sách lớp");
        }
        
        Long lecturerId = SessionManager.getCurrentUserId();
        return classRepository.findByLecturerId(lecturerId);
    }
    
    /**
     * Get all classes (for a specific lecturer if lecturerId is provided)
     * @param lecturerId the lecturer ID (optional)
     * @return List of ClassEntity
     */
    @Transactional(readOnly = true)
    public List<ClassEntity> getClassesByLecturer(Long lecturerId) {
        return classRepository.findByLecturerId(lecturerId);
    }
    
    /**
     * Delete a class
     * @param classId the class ID
     * @throws UnauthorizedException if user doesn't own the class
     * @throws ResourceNotFoundException if class not found
     */
    @Transactional
    public void deleteClass(Long classId) {
        // Validate lecturer is logged in
        if (!SessionManager.isLecturer()) {
            throw new UnauthorizedException("Chỉ giảng viên mới có thể xóa lớp học");
        }
        
        // Get the class
        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp với ID: " + classId));
        
        // Verify ownership
        Long currentLecturerId = SessionManager.getCurrentUserId();
        if (!classEntity.getLecturerId().equals(currentLecturerId)) {
            throw new UnauthorizedException("Bạn không có quyền xóa lớp này");
        }
        
        // Delete all students in the class first (cascade)
        classStudentRepository.deleteByClassId(classId);
        
        // Delete the class
        classRepository.delete(classEntity);
    }
    
    /**
     * Get all students in a class
     * @param classId the class ID
     * @return List of Student entities
     * @throws UnauthorizedException if user doesn't own the class
     * @throws ResourceNotFoundException if class not found
     */
    @Transactional(readOnly = true)
    public List<Student> getStudentsOfClass(Long classId) {
        // Validate lecturer is logged in
        if (!SessionManager.isLecturer()) {
            throw new UnauthorizedException("Chỉ giảng viên mới có thể xem danh sách sinh viên");
        }
        
        // Get the class
        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp với ID: " + classId));
        
        // Verify ownership
        Long currentLecturerId = SessionManager.getCurrentUserId();
        if (!classEntity.getLecturerId().equals(currentLecturerId)) {
            throw new UnauthorizedException("Bạn không có quyền xem sinh viên của lớp này");
        }
        
        // Get all class-student relationships
        List<ClassStudent> classStudents = classStudentRepository.findByClassId(classId);
        
        // Fetch the actual student entities
        List<Student> students = new ArrayList<>();
        for (ClassStudent cs : classStudents) {
            studentRepository.findById(cs.getStudentId()).ifPresent(students::add);
        }
        
        return students;
    }
    
    /**
     * Add a student to a class
     * @param classId the class ID
     * @param studentId the student ID
     * @return the created ClassStudent record
     * @throws UnauthorizedException if user doesn't own the class
     * @throws ResourceNotFoundException if class or student not found
     * @throws IllegalStateException if student is already in the class
     */
    @Transactional
    public ClassStudent addStudentToClass(Long classId, Long studentId) {
        // Validate lecturer is logged in
        if (!SessionManager.isLecturer()) {
            throw new UnauthorizedException("Chỉ giảng viên mới có thể thêm sinh viên vào lớp");
        }
        
        // Get the class
        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp với ID: " + classId));
        
        // Verify ownership
        Long currentLecturerId = SessionManager.getCurrentUserId();
        if (!classEntity.getLecturerId().equals(currentLecturerId)) {
            throw new UnauthorizedException("Bạn không có quyền thêm sinh viên vào lớp này");
        }
        
        // Check if student exists
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + studentId);
        }
        
        // Check if student is already in the class
        if (classStudentRepository.existsByClassIdAndStudentId(classId, studentId)) {
            throw new IllegalStateException("Sinh viên đã có trong lớp này");
        }
        
        // Add student to class
        ClassStudent classStudent = new ClassStudent(classId, studentId);
        return classStudentRepository.save(classStudent);
    }
    
    /**
     * Get a class by ID
     * @param classId the class ID
     * @return the ClassEntity
     * @throws ResourceNotFoundException if class not found
     */
    @Transactional(readOnly = true)
    public ClassEntity getClassById(Long classId) {
        return classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp với ID: " + classId));
    }
}

