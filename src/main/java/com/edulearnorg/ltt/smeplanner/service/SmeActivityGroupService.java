package com.edulearnorg.ltt.smeplanner.service;

import com.edulearnorg.ltt.smeplanner.entity.Activity;
import com.edulearnorg.ltt.smeplanner.entity.Schedule;
import com.edulearnorg.ltt.smeplanner.entity.SmeActivityGroup;
import com.edulearnorg.ltt.smeplanner.enums.ActivityCategory;
import com.edulearnorg.ltt.smeplanner.repository.ActivityRepository;
import com.edulearnorg.ltt.smeplanner.repository.SmeActivityGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing SME activity groupings and analytics
 */
@Service
@Transactional
public class SmeActivityGroupService {
    
    @Autowired
    private SmeActivityGroupRepository smeActivityGroupRepository;
    
    @Autowired
    private ActivityRepository activityRepository;
    
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    
    /**
     * Process a schedule and update SME activity groupings
     */
    public void processScheduleForGrouping(Schedule schedule) {
        String monthYear = schedule.getFromDate().format(MONTH_YEAR_FORMATTER);
        
        // Find or create activity group
        SmeActivityGroup activityGroup = findOrCreateActivityGroup(
            schedule.getUserId(), 
            schedule.getActivityId(), 
            monthYear
        );
        
        // Calculate duration in hours
        Double durationHours = calculateScheduleDuration(schedule);
        
        // Update the activity group
        activityGroup.incrementSession();
        activityGroup.addHours(durationHours);
        
        smeActivityGroupRepository.save(activityGroup);
    }
    
    /**
     * Get all activity groups for a specific SME
     */
    public List<SmeActivityGroup> getActivityGroupsBySme(Long smeUserId) {
        return smeActivityGroupRepository.findBySmeUserId(smeUserId);
    }
    
    /**
     * Get activity groups for a specific SME and month
     */
    public List<SmeActivityGroup> getActivityGroupsBySmeAndMonth(Long smeUserId, String monthYear) {
        return smeActivityGroupRepository.findBySmeUserIdAndMonthYear(smeUserId, monthYear);
    }
    
    /**
     * Get activity distribution by category for a specific SME and month
     */
    public Map<ActivityCategory, ActivitySummary> getActivityDistribution(Long smeUserId, String monthYear) {
        List<Object[]> results = smeActivityGroupRepository.getActivityDistributionBySmeAndMonth(smeUserId, monthYear);
        
        return results.stream().collect(Collectors.toMap(
            result -> (ActivityCategory) result[0],
            result -> new ActivitySummary(
                (Long) result[1],      // count
                (Double) result[2],    // total hours
                (Long) result[3]       // total sessions
            )
        ));
    }
    
    /**
     * Get total hours allocated by SME for a specific month
     */
    public Double getTotalHoursBySmeAndMonth(Long smeUserId, String monthYear) {
        return smeActivityGroupRepository.getTotalHoursBySmeAndMonth(smeUserId, monthYear);
    }
    
    /**
     * Get total hours by category for a specific SME and month
     */
    public Double getTotalHoursByCategory(Long smeUserId, ActivityCategory category, String monthYear) {
        return smeActivityGroupRepository.getTotalHoursBySmeAndCategoryAndMonth(smeUserId, category, monthYear);
    }
    
    /**
     * Get all activities by category
     */
    public List<Activity> getActivitiesByCategory(ActivityCategory category) {
        return activityRepository.findByCategory(category);
    }
    
    /**
     * Get activities with fixed vs variable duration
     */
    public List<Activity> getActivitiesWithFixedDuration() {
        return activityRepository.findByIsVariableDurationFalse();
    }
    
    public List<Activity> getActivitiesWithVariableDuration() {
        return activityRepository.findByIsVariableDurationTrue();
    }
    
    /**
     * Get all SMEs active in a specific month
     */
    public List<Long> getActiveSmesByMonth(String monthYear) {
        return smeActivityGroupRepository.getActiveSmesByMonth(monthYear);
    }

    // Private helper methods
    
    private SmeActivityGroup findOrCreateActivityGroup(Long smeUserId, Long activityId, String monthYear) {
        Optional<SmeActivityGroup> existing = smeActivityGroupRepository
            .findBySmeUserIdAndActivityIdAndMonthYear(smeUserId, activityId, monthYear);
        
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Create new activity group
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("Activity not found: " + activityId));
        
        return new SmeActivityGroup(smeUserId, activityId, activity.getCategory(), monthYear);
    }
    
    private Double calculateScheduleDuration(Schedule schedule) {
        Duration duration = Duration.between(
            schedule.getFromTime().atDate(schedule.getFromDate()),
            schedule.getToTime().atDate(schedule.getToDate())
        );
        return duration.toMinutes() / 60.0; // Convert to hours
    }
    
    /**
     * Inner class to represent activity summary
     */
    public static class ActivitySummary {
        private final Long activityCount;
        private final Double totalHours;
        private final Long totalSessions;
        
        public ActivitySummary(Long activityCount, Double totalHours, Long totalSessions) {
            this.activityCount = activityCount;
            this.totalHours = totalHours != null ? totalHours : 0.0;
            this.totalSessions = totalSessions != null ? totalSessions : 0L;
        }
        
        // Getters
        public Long getActivityCount() { return activityCount; }
        public Double getTotalHours() { return totalHours; }
        public Long getTotalSessions() { return totalSessions; }
        
        public Double getAverageHoursPerSession() {
            return totalSessions > 0 ? totalHours / totalSessions : 0.0;
        }
    }
}
