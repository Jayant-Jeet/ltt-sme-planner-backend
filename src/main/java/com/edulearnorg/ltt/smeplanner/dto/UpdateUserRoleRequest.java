package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to update user role")
public class UpdateUserRoleRequest {
    
    @Pattern(regexp = "SME|SUPERVISOR|LEAD", message = "Role must be SME, SUPERVISOR, or LEAD")
    @Schema(description = "New user role", example = "SUPERVISOR", allowableValues = {"SME", "SUPERVISOR", "LEAD"})
    private String role;
    
    // Constructors
    public UpdateUserRoleRequest() {}
    
    public UpdateUserRoleRequest(String role) {
        this.role = role;
    }
    
    // Getters and Setters
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}
