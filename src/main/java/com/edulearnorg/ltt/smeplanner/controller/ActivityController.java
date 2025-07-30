package com.edulearnorg.ltt.smeplanner.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edulearnorg.ltt.smeplanner.dto.BulkActivityRequest;
import com.edulearnorg.ltt.smeplanner.dto.CreateActivityRequest;
import com.edulearnorg.ltt.smeplanner.dto.UpdateActivityRequest;
import com.edulearnorg.ltt.smeplanner.entity.Activity;
import com.edulearnorg.ltt.smeplanner.service.ActivityService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/ltt-sme-planner/v1/activities")
@CrossOrigin(origins = "*")
@Tag(name = "Activity Management", description = "Activity management endpoints for CRUD operations and bulk export/import")
@SecurityRequirement(name = "bearerAuth")
public class ActivityController {
    
    private final ActivityService activityService;
    
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }
    
    @GetMapping
    @Operation(
        summary = "Get All Activities",
        description = "Retrieve a list of all available activities in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of all activities",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Activity.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid JWT token")
    })
    public ResponseEntity<List<Activity>> getActivities() {
        List<Activity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get Activity by ID",
        description = "Retrieve a specific activity by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Activity.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Activity not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid JWT token")
    })
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        if (activity != null) {
            return ResponseEntity.ok(activity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    @Operation(
        summary = "Create New Activity",
        description = "Create a new activity in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Activity created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Activity.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid JWT token")
    })
    public ResponseEntity<Activity> createActivity(@Valid @RequestBody CreateActivityRequest request) {
        Activity activity = new Activity(
            request.getName(), 
            request.getDescription(), 
            request.getCategory(),
            request.getDurationInHours(),
            request.getIsVariableDuration()
        );
        Activity createdActivity = activityService.createActivity(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActivity);
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Update Activity",
        description = "Update an existing activity"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Activity.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Activity not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid JWT token")
    })
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @Valid @RequestBody UpdateActivityRequest request) {
        try {
            Activity activityToUpdate = new Activity(
                request.getName(), 
                request.getDescription(),
                request.getCategory(),
                request.getDurationInHours(),
                request.getIsVariableDuration()
            );
            Activity updatedActivity = activityService.updateActivity(id, activityToUpdate);
            return ResponseEntity.ok(updatedActivity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete Activity",
        description = "Delete an activity from the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Activity deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Activity not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid JWT token")
    })
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        try {
            activityService.deleteActivity(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/bulk")
    @Operation(
        summary = "Create Activities in Bulk",
        description = "Create multiple activities at once - useful for importing activities"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Activities created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Activity.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid JWT token")
    })
    public ResponseEntity<List<Activity>> createActivitiesInBulk(@Valid @RequestBody BulkActivityRequest request) {
        List<Activity> activities = request.getActivities().stream()
                .map(activityRequest -> new Activity(activityRequest.getName(), activityRequest.getDescription()))
                .collect(Collectors.toList());
        
        List<Activity> createdActivities = activityService.createActivitiesInBulk(activities);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActivities);
    }
    
    @GetMapping("/export")
    @Operation(
        summary = "Export All Activities",
        description = "Export all activities from the database - useful for backup or data migration"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activities exported successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Activity.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid JWT token")
    })
    public ResponseEntity<List<Activity>> exportActivities() {
        List<Activity> activities = activityService.exportAllActivities();
        return ResponseEntity.ok(activities);
    }
}
