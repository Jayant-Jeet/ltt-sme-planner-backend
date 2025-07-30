package com.edulearnorg.ltt.smeplanner.entity;

import com.edulearnorg.ltt.smeplanner.enums.ActivityCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;

@Entity
@Table(name = "activities")
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Activity name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityCategory category;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Duration must be greater than 0")
    @Column(nullable = true)
    private Double durationInHours;
    
    @Column(nullable = false)
    private Boolean isVariableDuration = false;
    
    // Constructors
    public Activity() {}
    
    public Activity(String name, String description) {
        this.name = name;
        this.description = description;
        this.isVariableDuration = false;
    }
    
    public Activity(String name, String description, ActivityCategory category, Double durationInHours) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.durationInHours = durationInHours;
        this.isVariableDuration = (durationInHours == null);
    }
    
    public Activity(String name, String description, ActivityCategory category, Double durationInHours, Boolean isVariableDuration) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.durationInHours = durationInHours;
        this.isVariableDuration = isVariableDuration;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ActivityCategory getCategory() {
        return category;
    }
    
    public void setCategory(ActivityCategory category) {
        this.category = category;
    }
    
    public Double getDurationInHours() {
        return durationInHours;
    }
    
    public void setDurationInHours(Double durationInHours) {
        this.durationInHours = durationInHours;
        this.isVariableDuration = (durationInHours == null);
    }
    
    public Boolean getIsVariableDuration() {
        return isVariableDuration;
    }
    
    public void setIsVariableDuration(Boolean isVariableDuration) {
        this.isVariableDuration = isVariableDuration;
    }
}
