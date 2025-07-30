package com.edulearnorg.ltt.smeplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edulearnorg.ltt.smeplanner.entity.Activity;
import com.edulearnorg.ltt.smeplanner.enums.ActivityCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    /**
     * Find activities by category
     */
    List<Activity> findByCategory(ActivityCategory category);
    
    /**
     * Find activities by category and variable duration flag
     */
    List<Activity> findByCategoryAndIsVariableDuration(ActivityCategory category, Boolean isVariableDuration);
    
    /**
     * Find activity by name (case insensitive)
     */
    Optional<Activity> findByNameIgnoreCase(String name);
    
    /**
     * Find activities with fixed duration
     */
    List<Activity> findByIsVariableDurationFalse();
    
    /**
     * Find activities with variable duration
     */
    List<Activity> findByIsVariableDurationTrue();
}
