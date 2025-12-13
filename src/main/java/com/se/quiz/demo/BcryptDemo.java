package com.se.quiz.demo;

import org.mindrot.jbcrypt.BCrypt;
import java.util.Scanner;

/**
 * BCrypt POC - Demonstration of password hashing and verification
 * This is a standalone console application to test BCrypt functionality
 */
public class BcryptDemo {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== BCrypt Password Hashing Demo ===\n");
        
        // Step 1: Input password and hash it
        System.out.print("Nhập password: ");
        String password = scanner.nextLine();
        
        // Hash the password using BCrypt (default work factor: 12)
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        
        System.out.println("\n--- Kết quả Hash ---");
        System.out.println("Password gốc: " + password);
        System.out.println("Password đã hash: " + hashedPassword);
        System.out.println("Độ dài hash: " + hashedPassword.length() + " ký tự");
        
        // Step 2: Verify password
        System.out.print("\nNhập lại password để kiểm tra: ");
        String passwordToVerify = scanner.nextLine();
        
        // Verify the password
        boolean isMatch = BCrypt.checkpw(passwordToVerify, hashedPassword);
        
        System.out.println("\n--- Kết quả Verify ---");
        if (isMatch) {
            System.out.println("✓ Đúng - Password khớp!");
        } else {
            System.out.println("✗ Sai - Password không khớp!");
        }
        
        // Additional demo: Show that same password produces different hashes
        System.out.println("\n--- Demo: Cùng password tạo hash khác nhau ---");
        String hash1 = BCrypt.hashpw(password, BCrypt.gensalt(12));
        String hash2 = BCrypt.hashpw(password, BCrypt.gensalt(12));
        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);
        System.out.println("Hash giống nhau? " + hash1.equals(hash2));
        System.out.println("Nhưng cả 2 đều verify đúng với password gốc!");
        System.out.println("Verify hash1: " + BCrypt.checkpw(password, hash1));
        System.out.println("Verify hash2: " + BCrypt.checkpw(password, hash2));
        
        scanner.close();
    }
}

