package com.se.quiz.demo;

import org.mindrot.jbcrypt.BCrypt;
import java.util.Scanner;

// Test BCrypt functionality

public class BcryptDemo {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== BCrypt Password Hashing Demo ===\n");
        
        // Step 1: Input password and hash it
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        // Hash the password using BCrypt (default work factor: 12)
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        
        System.out.println("\n--- Hash Result ---");
        System.out.println("Original Password: " + password);
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("Hash length: " + hashedPassword.length() + " chars");
        
        // Step 2: Verify password
        System.out.print("\nEnter password to verify: ");
        String passwordToVerify = scanner.nextLine();
        
        // Verify the password
        boolean isMatch = BCrypt.checkpw(passwordToVerify, hashedPassword);
        
        System.out.println("\n--- Verify Result ---");
        if (isMatch) {
            System.out.println("✓ Correct - Password matches!");
        } else {
            System.out.println("✗ Incorrect - Password does not match!");
        }
        
        // Additional demo: Show that same password produces different hashes
        System.out.println("\n--- Demo: Same password produces different hashes ---");
        String hash1 = BCrypt.hashpw(password, BCrypt.gensalt(12));
        String hash2 = BCrypt.hashpw(password, BCrypt.gensalt(12));
        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);
        System.out.println("Are hashes the same? " + hash1.equals(hash2));
        System.out.println("Both verify correctly with original password!");
        System.out.println("Verify hash1: " + BCrypt.checkpw(password, hash1));
        System.out.println("Verify hash2: " + BCrypt.checkpw(password, hash2));
        
        scanner.close();
    }
}

