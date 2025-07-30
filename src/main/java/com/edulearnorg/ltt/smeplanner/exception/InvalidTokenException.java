package com.edulearnorg.ltt.smeplanner.exception;

/**
 * Custom exception for invalid or missing authentication tokens.
 * This exception is thrown when JWT token validation fails or when
 * authorization headers are missing or malformed.
 */
public class InvalidTokenException extends RuntimeException {
    
    /**
     * Constructs a new InvalidTokenException with the specified detail message.
     * 
     * @param message the detail message explaining the token validation failure
     */
    public InvalidTokenException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new InvalidTokenException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the token validation failure
     * @param cause the cause of the token validation failure
     */
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
