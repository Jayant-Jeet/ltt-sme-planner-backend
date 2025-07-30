package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User information response")
public class UserResponse {
    
    @Schema(description = "User unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "User full name", example = "John Doe")
    private String name;
    
    @Schema(description = "User email address", example = "john.doe@edulearnorg.com")
    private String email;
    
    @Schema(description = "User role", example = "SME")
    private String role;
    
    // Constructors
    public UserResponse() {}
    
    public UserResponse(Long id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}
