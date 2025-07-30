package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import com.edulearnorg.ltt.smeplanner.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Request DTO for searching user availability
 */
@Schema(description = "Request for searching user availability based on date and time")
public class UserAvailabilitySearchRequest {
    
    @NotNull(message = "Date is required")
    @Schema(description = "Date to search for availability", example = "2025-07-15")
    private LocalDate date;
    
    @NotNull(message = "From time is required")
    @Schema(description = "Start time of the requested period", example = "09:00:00")
    private LocalTime fromTime;
    
    @NotNull(message = "To time is required")
    @Schema(description = "End time of the requested period", example = "11:00:00")
    private LocalTime toTime;
    
    @Schema(description = "List of user roles to filter by (optional)", example = "[\"SME\", \"SUPERVISOR\"]")
    private List<UserRole> roles;
    
    @Schema(description = "List of specific user IDs to search within (optional)", example = "[1, 2, 3]")
    private List<Long> userIds;
    
    // Constructors
    public UserAvailabilitySearchRequest() {}
    
    public UserAvailabilitySearchRequest(LocalDate date, LocalTime fromTime, LocalTime toTime) {
        this.date = date;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }
    
    public UserAvailabilitySearchRequest(LocalDate date, LocalTime fromTime, LocalTime toTime, 
                                       List<UserRole> roles, List<Long> userIds) {
        this.date = date;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.roles = roles;
        this.userIds = userIds;
    }
    
    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
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
    
    public List<UserRole> getRoles() {
        return roles;
    }
    
    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }
    
    public List<Long> getUserIds() {
        return userIds;
    }
    
    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
    
    @Override
    public String toString() {
        return "UserAvailabilitySearchRequest{" +
                "date=" + date +
                ", fromTime=" + fromTime +
                ", toTime=" + toTime +
                ", roles=" + roles +
                ", userIds=" + userIds +
                '}';
    }
}
