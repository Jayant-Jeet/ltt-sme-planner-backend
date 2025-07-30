package com.edulearnorg.ltt.smeplanner.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class BulkActivityRequest {
    
    @NotEmpty(message = "Activities list cannot be empty")
    @Valid
    private List<CreateActivityRequest> activities;
    
    // Constructors
    public BulkActivityRequest() {}
    
    public BulkActivityRequest(List<CreateActivityRequest> activities) {
        this.activities = activities;
    }
    
    // Getters and Setters
    public List<CreateActivityRequest> getActivities() {
        return activities;
    }
    
    public void setActivities(List<CreateActivityRequest> activities) {
        this.activities = activities;
    }
}
