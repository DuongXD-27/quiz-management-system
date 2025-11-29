package com.se.quiz.quiz_management_system.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.se.quiz.quiz_management_system.entity.ClassEntity;
import com.se.quiz.quiz_management_system.entity.Student;
import com.se.quiz.quiz_management_system.exception.ResourceNotFoundException;
import com.se.quiz.quiz_management_system.exception.UnauthorizedException;
import com.se.quiz.quiz_management_system.model.ImportResult;
import com.se.quiz.quiz_management_system.repository.ClassRepository;
import com.se.quiz.quiz_management_system.repository.ClassStudentRepository;
import com.se.quiz.quiz_management_system.repository.StudentRepository;
import com.se.quiz.quiz_management_system.session.SessionManager;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * StudentImportService - Handles CSV import of students
 */
@Service
public class StudentImportService {
    
    private static final int BCRYPT_WORK_FACTOR = 12;
    private static final String DEFAULT_PASSWORD = "123456"; // Default password for imported students
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Autowired
    private ClassService classService;
    
    /**
     * Import students from CSV file
     * CSV Format: username,full_name,student_code
     * @param classId the class ID to import students into
     * @param csvFile the CSV file
     * @return ImportResult with success/error counts and messages
     * @throws UnauthorizedException if user doesn't own the class
     * @throws ResourceNotFoundException if class not found
     */
    @Transactional
    public ImportResult importStudentsFromCsv(Long classId, File csvFile) {
        ImportResult result = new ImportResult();
        
        // Validate lecturer is logged in
        if (!SessionManager.isLecturer()) {
            throw new UnauthorizedException("Chỉ giảng viên mới có thể import sinh viên");
        }
        
        // Get the class and verify ownership
        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp với ID: " + classId));
        
        Long currentLecturerId = SessionManager.getCurrentUserId();
        if (!classEntity.getLecturerId().equals(currentLecturerId)) {
            throw new UnauthorizedException("Bạn không có quyền import sinh viên vào lớp này");
        }
        
        // Parse CSV file
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> rows = reader.readAll();
            
            if (rows.isEmpty()) {
                result.addErrorMessage("File CSV rỗng");
                return result;
            }
            
            // Skip header row if it exists (check if first row contains "username")
            int startRow = 0;
            if (rows.get(0).length > 0 && rows.get(0)[0].trim().equalsIgnoreCase("username")) {
                startRow = 1;
            }
            
            // Process each row
            for (int i = startRow; i < rows.size(); i++) {
                String[] row = rows.get(i);
                int rowNumber = i + 1;
                
                try {
                    // Validate row has 3 columns
                    if (row.length < 3) {
                        result.addErrorMessage("Dòng " + rowNumber + ": Thiếu dữ liệu (cần 3 cột: username,full_name,student_code)");
                        continue;
                    }
                    
                    // Extract and trim data
                    String username = row[0].trim();
                    String fullName = row[1].trim();
                    String studentCode = row[2].trim();
                    
                    // Validate data
                    if (username.isEmpty()) {
                        result.addErrorMessage("Dòng " + rowNumber + ": Username không được để trống");
                        continue;
                    }
                    
                    if (fullName.isEmpty()) {
                        result.addErrorMessage("Dòng " + rowNumber + ": Full name không được để trống");
                        continue;
                    }
                    
                    // Check if student already exists
                    Optional<Student> existingStudent = studentRepository.findByUsername(username);
                    Student student;
                    
                    if (existingStudent.isPresent()) {
                        // Student already exists, use existing record
                        student = existingStudent.get();
                    } else {
                        // Create new student with default password
                        String passwordHash = BCrypt.hashpw(DEFAULT_PASSWORD, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
                        student = new Student(username, passwordHash, fullName, studentCode);
                        student = studentRepository.save(student);
                    }
                    
                    // Add student to class (check for duplicates)
                    if (classStudentRepository.existsByClassIdAndStudentId(classId, student.getStudentId())) {
                        result.addErrorMessage("Dòng " + rowNumber + ": Sinh viên '" + username + "' đã có trong lớp");
                        continue;
                    }
                    
                    // Add to class
                    classService.addStudentToClass(classId, student.getStudentId());
                    result.incrementSuccess();
                    
                } catch (Exception e) {
                    result.addErrorMessage("Dòng " + rowNumber + ": Lỗi - " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            result.addErrorMessage("Lỗi đọc file: " + e.getMessage());
        } catch (CsvException e) {
            result.addErrorMessage("Lỗi parse CSV: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Import students from CSV file using manual parsing (without OpenCSV)
     * CSV Format: username,full_name,student_code
     * @param classId the class ID to import students into
     * @param csvFile the CSV file
     * @return ImportResult with success/error counts and messages
     */
    @Transactional
    public ImportResult importStudentsFromCsvManual(Long classId, File csvFile) {
        ImportResult result = new ImportResult();
        
        // Validate lecturer is logged in
        if (!SessionManager.isLecturer()) {
            throw new UnauthorizedException("Chỉ giảng viên mới có thể import sinh viên");
        }
        
        // Get the class and verify ownership
        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp với ID: " + classId));
        
        Long currentLecturerId = SessionManager.getCurrentUserId();
        if (!classEntity.getLecturerId().equals(currentLecturerId)) {
            throw new UnauthorizedException("Bạn không có quyền import sinh viên vào lớp này");
        }
        
        // Parse CSV file manually
        try (java.io.BufferedReader br = new java.io.BufferedReader(new FileReader(csvFile))) {
            String line;
            int rowNumber = 0;
            boolean isFirstRow = true;
            
            while ((line = br.readLine()) != null) {
                rowNumber++;
                
                // Skip header row if it exists
                if (isFirstRow && line.trim().toLowerCase().startsWith("username")) {
                    isFirstRow = false;
                    continue;
                }
                isFirstRow = false;
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    // Split by comma
                    String[] parts = line.split(",", -1);
                    
                    // Validate row has 3 columns
                    if (parts.length < 3) {
                        result.addErrorMessage("Dòng " + rowNumber + ": Thiếu dữ liệu (cần 3 cột: username,full_name,student_code)");
                        continue;
                    }
                    
                    // Extract and trim data
                    String username = parts[0].trim();
                    String fullName = parts[1].trim();
                    String studentCode = parts[2].trim();
                    
                    // Validate data
                    if (username.isEmpty()) {
                        result.addErrorMessage("Dòng " + rowNumber + ": Username không được để trống");
                        continue;
                    }
                    
                    if (fullName.isEmpty()) {
                        result.addErrorMessage("Dòng " + rowNumber + ": Full name không được để trống");
                        continue;
                    }
                    
                    // Check if student already exists
                    Optional<Student> existingStudent = studentRepository.findByUsername(username);
                    Student student;
                    
                    if (existingStudent.isPresent()) {
                        // Student already exists, use existing record
                        student = existingStudent.get();
                    } else {
                        // Create new student with default password
                        String passwordHash = BCrypt.hashpw(DEFAULT_PASSWORD, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
                        student = new Student(username, passwordHash, fullName, studentCode);
                        student = studentRepository.save(student);
                    }
                    
                    // Add student to class (check for duplicates)
                    if (classStudentRepository.existsByClassIdAndStudentId(classId, student.getStudentId())) {
                        result.addErrorMessage("Dòng " + rowNumber + ": Sinh viên '" + username + "' đã có trong lớp");
                        continue;
                    }
                    
                    // Add to class
                    classService.addStudentToClass(classId, student.getStudentId());
                    result.incrementSuccess();
                    
                } catch (Exception e) {
                    result.addErrorMessage("Dòng " + rowNumber + ": Lỗi - " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            result.addErrorMessage("Lỗi đọc file: " + e.getMessage());
        }
        
        return result;
    }
}

