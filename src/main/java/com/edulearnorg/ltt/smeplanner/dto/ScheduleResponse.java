package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Response DTO for schedule data
 */
@Schema(description = "Schedule response containing schedule details")
public class ScheduleResponse {
    
    @Schema(description = "Schedule ID", example = "1")
    private Long id;
    
    @Schema(description = "User ID who is scheduled", example = "1")
    private Long userId;
    
    @Schema(description = "Start date of the schedule", example = "2025-07-15")
    private LocalDate fromDate;
    
    @Schema(description = "End date of the schedule", example = "2025-07-15")
    private LocalDate toDate;
    
    @Schema(description = "Start time of the schedule", example = "09:00:00")
    private LocalTime fromTime;
    
    @Schema(description = "End time of the schedule", example = "11:00:00")
    private LocalTime toTime;
    
    @Schema(description = "Activity ID for the schedule", example = "1")
    private Long activityId;
    
    @Schema(description = "Activity name", example = "Training Session")
    private String activityName;
    
    @Schema(description = "Description of the schedule", example = "Java Spring Boot Training")
    private String description;
    
    @Schema(description = "When the schedule was created", example = "2025-07-12T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "When the schedule was last updated", example = "2025-07-12T10:30:00")
    private LocalDateTime updatedAt;
    
    // User details
    @Schema(description = "User name", example = "John Doe")
    private String userName;
    
    @Schema(description = "User email", example = "john.doe@edulearnorg.com")
    private String userEmail;
    
    // Activity details  
    @Schema(description = "Activity description", example = "Conducting training sessions for employees")
    private String activityDescription;
    
    // Constructors
    public ScheduleResponse() {}
    
    public ScheduleResponse(Long id, Long userId, LocalDate fromDate, LocalDate toDate, 
                          LocalTime fromTime, LocalTime toTime, Long activityId, 
                          String activityName, String description, LocalDateTime createdAt, 
                          LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.activityId = activityId;
        this.activityName = activityName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
    
    public String getActivityDescription() {
        return activityDescription;
    }
    
    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }
}
