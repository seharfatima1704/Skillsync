package util;

import java.util.regex.Pattern;

/**
 * Email Validation Utility for SkillSync Platform
 * 
 * This class provides strict Gmail email validation for user registration.
 * Only valid Gmail addresses following proper format are allowed.
 */
public class EmailValidator {
    
    // Gmail validation regex
    // Allows: letters, numbers, dots, underscores, percentage, plus, hyphen
    // Must end with @gmail.com
    private static final String GMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
    
    private static final Pattern GMAIL_PATTERN = Pattern.compile(GMAIL_REGEX);
    
    /**
     * Validates if the given email is a valid Gmail address
     * 
     * @param email The email address to validate
     * @return true if valid Gmail address, false otherwise
     */
    public static boolean isValidGmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Remove leading/trailing whitespace
        email = email.trim();
        
        // Basic format check
        if (!email.contains("@") || !email.endsWith("@gmail.com")) {
            return false;
        }
        
        // Extract username part (before @gmail.com)
        String username = email.substring(0, email.indexOf("@"));
        
        // Username validation rules
        if (username.length() < 6 || username.length() > 30) {
            return false; // Gmail usernames are 6-30 characters
        }
        
        // Cannot start or end with dot or hyphen
        if (username.startsWith(".") || username.startsWith("-") || 
            username.endsWith(".") || username.endsWith("-")) {
            return false;
        }
        
        // No consecutive dots
        if (username.contains("..")) {
            return false;
        }
        
        // Apply regex pattern for final validation
        return GMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Gets detailed validation error message
     * 
     * @param email The email address to validate
     * @return Error message explaining why validation failed
     */
    public static String getValidationErrorMessage(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email address is required";
        }
        
        email = email.trim();
        
        if (!email.endsWith("@gmail.com")) {
            return "Only Gmail addresses are allowed (must end with @gmail.com)";
        }
        
        if (!email.contains("@")) {
            return "Invalid email format";
        }
        
        String username = email.substring(0, email.indexOf("@"));
        
        if (username.length() < 6) {
            return "Gmail username must be at least 6 characters long";
        }
        
        if (username.length() > 30) {
            return "Gmail username must be no more than 30 characters long";
        }
        
        if (username.startsWith(".") || username.startsWith("-")) {
            return "Gmail username cannot start with dot or hyphen";
        }
        
        if (username.endsWith(".") || username.endsWith("-")) {
            return "Gmail username cannot end with dot or hyphen";
        }
        
        if (username.contains("..")) {
            return "Gmail username cannot contain consecutive dots";
        }
        
        if (!GMAIL_PATTERN.matcher(email).matches()) {
            return "Invalid Gmail address format";
        }
        
        return "Invalid email format";
    }
    
    /**
     * Normalizes email for comparison (lowercase, trim)
     * 
     * @param email The email to normalize
     * @return Normalized email address
     */
    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.toLowerCase().trim();
    }
}
