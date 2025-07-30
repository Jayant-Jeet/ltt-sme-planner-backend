package com.edulearnorg.ltt.smeplanner.entity;

import com.edulearnorg.ltt.smeplanner.enums.ActivityCategory;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to track SME activity groupings and performance metrics
 */
@Entity
@Table(name = "sme_activity_groups")
public class SmeActivityGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sme_user_id", nullable = false)
    private Long smeUserId;
    
    @Column(name = "activity_id", nullable = false)
    private Long activityId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityCategory category;
    
    @Column(name = "total_hours_allocated")
    private Double totalHoursAllocated = 0.0;
    
    @Column(name = "total_sessions")
    private Integer totalSessions = 0;
    
    @Column(name = "month_year", nullable = false)
    private String monthYear; // Format: "YYYY-MM"
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sme_user_id", insertable = false, updatable = false)
    private User smeUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", insertable = false, updatable = false)
    private Activity activity;
    
    // Constructors
    public SmeActivityGroup() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public SmeActivityGroup(Long smeUserId, Long activityId, ActivityCategory category, String monthYear) {
        this.smeUserId = smeUserId;
        this.activityId = activityId;
        this.category = category;
        this.monthYear = monthYear;
        this.totalHoursAllocated = 0.0;
        this.totalSessions = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSmeUserId() {
        return smeUserId;
    }
    
    public void setSmeUserId(Long smeUserId) {
        this.smeUserId = smeUserId;
    }
    
    public Long getActivityId() {
        return activityId;
    }
    
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    
    public ActivityCategory getCategory() {
        return category;
    }
    
    public void setCategory(ActivityCategory category) {
        this.category = category;
    }
    
    public Double getTotalHoursAllocated() {
        return totalHoursAllocated;
    }
    
    public void setTotalHoursAllocated(Double totalHoursAllocated) {
        this.totalHoursAllocated = totalHoursAllocated;
    }
    
    public Integer getTotalSessions() {
        return totalSessions;
    }
    
    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }
    
    public String getMonthYear() {
        return monthYear;
    }
    
    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
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
    
    public User getSmeUser() {
        return smeUser;
    }
    
    public void setSmeUser(User smeUser) {
        this.smeUser = smeUser;
    }
    
    public Activity getActivity() {
        return activity;
    }
    
    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    
    // Utility methods
    public void incrementSession() {
        this.totalSessions++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addHours(Double hours) {
        if (hours != null && hours > 0) {
            this.totalHoursAllocated += hours;
            this.updatedAt = LocalDateTime.now();
        }
    }
}
