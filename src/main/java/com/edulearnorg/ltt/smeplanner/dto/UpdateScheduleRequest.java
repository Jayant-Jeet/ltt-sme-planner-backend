package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for updating an existing schedule
 */
@Schema(description = "Request for updating an existing schedule")
public class UpdateScheduleRequest {
    
    @NotNull(message = "From date is required")
    @Schema(description = "Start date of the schedule", example = "2025-07-15")
    private LocalDate fromDate;
    
    @NotNull(message = "To date is required")
    @Schema(description = "End date of the schedule", example = "2025-07-15")
    private LocalDate toDate;
    
    @NotNull(message = "From time is required")
    @Schema(description = "Start time of the schedule", example = "09:00:00")
    private LocalTime fromTime;
    
    @NotNull(message = "To time is required")
    @Schema(description = "End time of the schedule", example = "11:00:00")
    private LocalTime toTime;
    
    @NotNull(message = "Activity ID is required")
    @Schema(description = "Activity ID for the schedule", example = "1")
    private Long activityId;
    
    @Schema(description = "Activity name", example = "Training Session")
    private String activityName;
    
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Description of the schedule", example = "Java Spring Boot Training")
    private String description;
    
    // Constructors
    public UpdateScheduleRequest() {}
    
    public UpdateScheduleRequest(LocalDate fromDate, LocalDate toDate, LocalTime fromTime, 
                               LocalTime toTime, Long activityId, String activityName, String description) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.activityId = activityId;
        this.activityName = activityName;
        this.description = description;
    }
    
    // Getters and Setters
    public LocalDate getFromDate() {
        return fromDate;
    }
    
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }
    
    public LocalDate getToDate() {
        return toDate;
    }
    
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
    
    public LocalTime getFromTime() {
        return fromTime;
    }
    
    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }
    
    public LocalTime getToTime() {
        return toTime;
    }
    
    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }
    
    public Long getActivityId() {
        return activityId;
    }
    
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    
    public String getActivityName() {
        return activityName;
    }
    
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "UpdateScheduleRequest{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", fromTime=" + fromTime +
                ", toTime=" + toTime +
                ", activityId=" + activityId +
                ", activityName='" + activityName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
