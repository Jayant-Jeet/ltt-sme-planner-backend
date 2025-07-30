package com.edulearnorg.ltt.smeplanner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.edulearnorg.ltt.smeplanner.entity.Activity;
import com.edulearnorg.ltt.smeplanner.repository.ActivityRepository;

@Service
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }
    
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }
    
    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElse(null);
    }
    
    /**
     * Create a new activity
     */
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }
    
    /**
     * Update an existing activity
     */
    public Activity updateActivity(Long id, Activity updatedActivity) {
        Optional<Activity> existingActivity = activityRepository.findById(id);
        if (existingActivity.isPresent()) {
            Activity activity = existingActivity.get();
            activity.setName(updatedActivity.getName());
            activity.setDescription(updatedActivity.getDescription());
            if (updatedActivity.getCategory() != null) {
                activity.setCategory(updatedActivity.getCategory());
            }
            if (updatedActivity.getDurationInHours() != null) {
                activity.setDurationInHours(updatedActivity.getDurationInHours());
            }
            if (updatedActivity.getIsVariableDuration() != null) {
                activity.setIsVariableDuration(updatedActivity.getIsVariableDuration());
            }
            return activityRepository.save(activity);
        } else {
            throw new IllegalArgumentException("Activity not found with id: " + id);
        }
    }
    
    /**
     * Delete an activity
     */
    public void deleteActivity(Long id) {
        if (activityRepository.existsById(id)) {
            activityRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Activity not found with id: " + id);
        }
    }
    
    /**
     * Create activities in bulk
     */
    public List<Activity> createActivitiesInBulk(List<Activity> activities) {
        return activityRepository.saveAll(activities);
    }
    
    /**
     * Export all activities
     */
    public List<Activity> exportAllActivities() {
        return activityRepository.findAll();
    }
    
    /**
     * Check if activity exists by ID
     */
    public boolean existsById(Long id) {
        return activityRepository.existsById(id);
    }
}
