package com.edulearnorg.ltt.smeplanner.dto;

import com.edulearnorg.ltt.smeplanner.enums.ActivityCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateActivityRequest {
    
    @NotBlank(message = "Activity name is required")
    @Size(max = 255, message = "Activity name must not exceed 255 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotNull(message = "Activity category is required")
    private ActivityCategory category;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Duration must be greater than 0")
    private Double durationInHours;
    
    private Boolean isVariableDuration = false;
    
    // Constructors
    public CreateActivityRequest() {}
    
    public CreateActivityRequest(String name, String description) {
        this.name = name;
        this.description = description;
        this.isVariableDuration = false;
    }
    
    public CreateActivityRequest(String name, String description, ActivityCategory category, 
                               Double durationInHours, Boolean isVariableDuration) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.durationInHours = durationInHours;
        this.isVariableDuration = isVariableDuration;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ActivityCategory getCategory() {
        return category;
    }
    
    public void setCategory(ActivityCategory category) {
        this.category = category;
    }
    
    public Double getDurationInHours() {
        return durationInHours;
    }
    
    public void setDurationInHours(Double durationInHours) {
        this.durationInHours = durationInHours;
        this.isVariableDuration = (durationInHours == null);
    }
    
    public Boolean getIsVariableDuration() {
        return isVariableDuration;
    }
    
    public void setIsVariableDuration(Boolean isVariableDuration) {
        this.isVariableDuration = isVariableDuration;
    }
}
