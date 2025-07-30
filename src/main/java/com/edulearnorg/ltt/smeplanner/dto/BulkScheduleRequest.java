package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for bulk creating schedules
 */
@Schema(description = "Request for bulk creating multiple schedules")
public class BulkScheduleRequest {
    
    @NotNull(message = "Schedules list is required")
    @NotEmpty(message = "At least one schedule must be provided")
    @Size(max = 50, message = "Cannot create more than 50 schedules at once")
    @Valid
    @Schema(description = "List of schedules to create", example = "[{...}]")
    private List<CreateScheduleRequest> schedules;
    
    @Schema(description = "Skip conflicting schedules instead of failing the entire operation", 
            example = "false", defaultValue = "false")
    private boolean skipConflicts = false;
    
    // Constructors
    public BulkScheduleRequest() {}
    
    public BulkScheduleRequest(List<CreateScheduleRequest> schedules) {
        this.schedules = schedules;
    }
    
    public BulkScheduleRequest(List<CreateScheduleRequest> schedules, boolean skipConflicts) {
        this.schedules = schedules;
        this.skipConflicts = skipConflicts;
    }
    
    // Getters and Setters
    public List<CreateScheduleRequest> getSchedules() {
        return schedules;
    }
    
    public void setSchedules(List<CreateScheduleRequest> schedules) {
        this.schedules = schedules;
    }
    
    public boolean isSkipConflicts() {
        return skipConflicts;
    }
    
    public void setSkipConflicts(boolean skipConflicts) {
        this.skipConflicts = skipConflicts;
    }
    
    @Override
    public String toString() {
        return "BulkScheduleRequest{" +
                "schedules=" + schedules +
                ", skipConflicts=" + skipConflicts +
                '}';
    }
}
