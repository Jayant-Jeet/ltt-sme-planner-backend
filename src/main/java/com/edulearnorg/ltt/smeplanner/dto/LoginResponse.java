package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login response containing authentication token and user details")
public class LoginResponse {
    
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Token type", example = "Bearer", defaultValue = "Bearer")
    private String type = "Bearer";
    
    @Schema(description = "User unique identifier", example = "1")
    private Long userId;
    
    @Schema(description = "User full name", example = "John Doe")
    private String userName;
    
    @Schema(description = "User email address", example = "john.doe@edulearnorg.com")
    private String userEmail;
    
    @Schema(description = "User role", example = "SME")
    private String userRole;
    
    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(String token, Long userId, String userName, String userEmail) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = "SME"; // Default for backward compatibility
    }
    
    public LoginResponse(String token, Long userId, String userName, String userEmail, String userRole) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    // Backward compatibility methods
    public Long getSmeId() {
        return userId;
    }
    
    public void setSmeId(Long smeId) {
        this.userId = smeId;
    }
    
    public String getSmeName() {
        return userName;
    }
    
    public void setSmeName(String smeName) {
        this.userName = smeName;
    }
    
    public String getSmeEmail() {
        return userEmail;
    }
    
    public void setSmeEmail(String smeEmail) {
        this.userEmail = smeEmail;
    }
}
