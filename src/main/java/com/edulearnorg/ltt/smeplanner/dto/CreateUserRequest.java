package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to create a new user")
public class CreateUserRequest {
    
    @NotBlank(message = "Name is required")
    @Schema(description = "User full name", example = "John Doe")
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Schema(description = "User email address", example = "john.doe@edulearnorg.com")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "password123")
    private String password;
    
    @Pattern(regexp = "SME|SUPERVISOR|LEAD", message = "Role must be SME, SUPERVISOR, or LEAD")
    @Schema(description = "User role", example = "SME", allowableValues = {"SME", "SUPERVISOR", "LEAD"})
    private String role;
    
    // Constructors
    public CreateUserRequest() {}
    
    public CreateUserRequest(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}
