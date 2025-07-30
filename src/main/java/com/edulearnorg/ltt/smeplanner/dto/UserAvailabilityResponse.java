package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.edulearnorg.ltt.smeplanner.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO for user availability information
 */
@Schema(description = "User availability information")
public class UserAvailabilityResponse {
    
    @Schema(description = "User ID", example = "1")
    private Long userId;
    
    @Schema(description = "User name", example = "John Doe")
    private String userName;
    
    @Schema(description = "User email", example = "john.doe@edulearnorg.com")
    private String userEmail;
    
    @Schema(description = "User role", example = "SME")
    private UserRole userRole;
    
    @Schema(description = "Whether the user is available for the requested time", example = "true")
    private boolean isAvailable;
    
    @Schema(description = "List of conflicting schedules if user is not available")
    private List<ConflictingSchedule> conflictingSchedules;
    
    // Constructors
    public UserAvailabilityResponse() {}
    
    public UserAvailabilityResponse(Long userId, String userName, String userEmail, 
                                  UserRole userRole, boolean isAvailable) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.isAvailable = isAvailable;
    }
    
    public UserAvailabilityResponse(Long userId, String userName, String userEmail, 
                                  UserRole userRole, boolean isAvailable, 
                                  List<ConflictingSchedule> conflictingSchedules) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.isAvailable = isAvailable;
        this.conflictingSchedules = conflictingSchedules;
    }
    
    // Getters and Setters
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
    
    public UserRole getUserRole() {
        return userRole;
    }
    
    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    
    public List<ConflictingSchedule> getConflictingSchedules() {
        return conflictingSchedules;
    }
    
    public void setConflictingSchedules(List<ConflictingSchedule> conflictingSchedules) {
        this.conflictingSchedules = conflictingSchedules;
    }
    
    /**
     * Inner class representing a conflicting schedule
     */
    @Schema(description = "Information about a conflicting schedule")
    public static class ConflictingSchedule {
        
        @Schema(description = "Schedule ID", example = "1")
        private Long scheduleId;
        
        @Schema(description = "From date", example = "2025-07-15")
        private LocalDate fromDate;
        
        @Schema(description = "To date", example = "2025-07-15")
        private LocalDate toDate;
        
        @Schema(description = "From time", example = "09:00:00")
        private LocalTime fromTime;
        
        @Schema(description = "To time", example = "11:00:00")
        private LocalTime toTime;
        
        @Schema(description = "Activity name", example = "Training Session")
        private String activityName;
        
        @Schema(description = "Schedule description", example = "Java Spring Boot Training")
        private String description;
        
        // Constructors
        public ConflictingSchedule() {}
        
        public ConflictingSchedule(Long scheduleId, LocalDate fromDate, LocalDate toDate,
                                 LocalTime fromTime, LocalTime toTime, String activityName, String description) {
            this.scheduleId = scheduleId;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.activityName = activityName;
            this.description = description;
        }
        
        // Getters and Setters
        public Long getScheduleId() {
            return scheduleId;
        }
        
        public void setScheduleId(Long scheduleId) {
            this.scheduleId = scheduleId;
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
    }
    
    @Override
    public String toString() {
        return "UserAvailabilityResponse{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userRole=" + userRole +
                ", isAvailable=" + isAvailable +
                ", conflictingSchedules=" + conflictingSchedules +
                '}';
    }
}
