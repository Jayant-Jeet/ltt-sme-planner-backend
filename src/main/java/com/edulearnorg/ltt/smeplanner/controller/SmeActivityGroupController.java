package com.edulearnorg.ltt.smeplanner.controller;

import com.edulearnorg.ltt.smeplanner.entity.Activity;
import com.edulearnorg.ltt.smeplanner.entity.SmeActivityGroup;
import com.edulearnorg.ltt.smeplanner.enums.ActivityCategory;
import com.edulearnorg.ltt.smeplanner.service.SmeActivityGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for managing SME activity groupings and analytics
 */
@RestController
@RequestMapping("/ltt-sme-planner/v1/sme-activities")
@Tag(name = "SME Activity Grouping", description = "APIs for managing SME activity groupings and analytics")
public class SmeActivityGroupController {
    
    @Autowired
    private SmeActivityGroupService smeActivityGroupService;
    
    @GetMapping("/sme/{smeUserId}")
    @Operation(summary = "Get all activity groups for a specific SME")
    public ResponseEntity<List<SmeActivityGroup>> getActivityGroupsBySme(
            @Parameter(description = "SME User ID") @PathVariable Long smeUserId) {
        List<SmeActivityGroup> activityGroups = smeActivityGroupService.getActivityGroupsBySme(smeUserId);
        return ResponseEntity.ok(activityGroups);
    }
    
    @GetMapping("/sme/{smeUserId}/month/{monthYear}")
    @Operation(summary = "Get activity groups for a specific SME and month")
    public ResponseEntity<List<SmeActivityGroup>> getActivityGroupsBySmeAndMonth(
            @Parameter(description = "SME User ID") @PathVariable Long smeUserId,
            @Parameter(description = "Month-Year in YYYY-MM format") @PathVariable String monthYear) {
        List<SmeActivityGroup> activityGroups = smeActivityGroupService.getActivityGroupsBySmeAndMonth(smeUserId, monthYear);
        return ResponseEntity.ok(activityGroups);
    }
    
    @GetMapping("/sme/{smeUserId}/distribution/{monthYear}")
    @Operation(summary = "Get activity distribution by category for a specific SME and month")
    public ResponseEntity<Map<ActivityCategory, SmeActivityGroupService.ActivitySummary>> getActivityDistribution(
            @Parameter(description = "SME User ID") @PathVariable Long smeUserId,
            @Parameter(description = "Month-Year in YYYY-MM format") @PathVariable String monthYear) {
        Map<ActivityCategory, SmeActivityGroupService.ActivitySummary> distribution = 
            smeActivityGroupService.getActivityDistribution(smeUserId, monthYear);
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/sme/{smeUserId}/total-hours/{monthYear}")
    @Operation(summary = "Get total hours allocated by SME for a specific month")
    public ResponseEntity<Map<String, Double>> getTotalHoursBySmeAndMonth(
            @Parameter(description = "SME User ID") @PathVariable Long smeUserId,
            @Parameter(description = "Month-Year in YYYY-MM format") @PathVariable String monthYear) {
        Double totalHours = smeActivityGroupService.getTotalHoursBySmeAndMonth(smeUserId, monthYear);
        return ResponseEntity.ok(Map.of("totalHours", totalHours));
    }
    
    @GetMapping("/sme/{smeUserId}/category/{category}/hours/{monthYear}")
    @Operation(summary = "Get total hours by category for a specific SME and month")
    public ResponseEntity<Map<String, Object>> getTotalHoursByCategory(
            @Parameter(description = "SME User ID") @PathVariable Long smeUserId,
            @Parameter(description = "Activity Category") @PathVariable ActivityCategory category,
            @Parameter(description = "Month-Year in YYYY-MM format") @PathVariable String monthYear) {
        Double totalHours = smeActivityGroupService.getTotalHoursByCategory(smeUserId, category, monthYear);
        Map<String, Object> response = Map.of(
            "totalHours", totalHours, 
            "category", category.getDisplayName()
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/activities/category/{category}")
    @Operation(summary = "Get all activities by category")
    public ResponseEntity<List<Activity>> getActivitiesByCategory(
            @Parameter(description = "Activity Category") @PathVariable ActivityCategory category) {
        List<Activity> activities = smeActivityGroupService.getActivitiesByCategory(category);
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/activities/fixed-duration")
    @Operation(summary = "Get activities with fixed duration")
    public ResponseEntity<List<Activity>> getActivitiesWithFixedDuration() {
        List<Activity> activities = smeActivityGroupService.getActivitiesWithFixedDuration();
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/activities/variable-duration")
    @Operation(summary = "Get activities with variable duration")
    public ResponseEntity<List<Activity>> getActivitiesWithVariableDuration() {
        List<Activity> activities = smeActivityGroupService.getActivitiesWithVariableDuration();
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/month/{monthYear}/active-smes")
    @Operation(summary = "Get all SMEs active in a specific month")
    public ResponseEntity<List<Long>> getActiveSmesByMonth(
            @Parameter(description = "Month-Year in YYYY-MM format") @PathVariable String monthYear) {
        List<Long> activeSmeIds = smeActivityGroupService.getActiveSmesByMonth(monthYear);
        return ResponseEntity.ok(activeSmeIds);
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Get all available activity categories")
    public ResponseEntity<List<Map<String, String>>> getActivityCategories() {
        List<Map<String, String>> categories = List.of(
            Map.of("code", "CALENDAR_TRAINING", "displayName", "Calendar Training"),
            Map.of("code", "BLENDED", "displayName", "Blended"),
            Map.of("code", "ADHOC_TRAINING", "displayName", "Adhoc Training"),
            Map.of("code", "BYTE_SIZED", "displayName", "Byte Sized"),
            Map.of("code", "CONTENT_DEVELOPMENT", "displayName", "Content Development"),
            Map.of("code", "EVALUATION", "displayName", "Evaluation"),
            Map.of("code", "SKILL_UPGRADE", "displayName", "Skill Upgrade"),
            Map.of("code", "MANAGEMENT", "displayName", "Management"),
            Map.of("code", "TIME_OFF", "displayName", "Time Off"),
            Map.of("code", "MISCELLANEOUS", "displayName", "Miscellaneous")
        );
        return ResponseEntity.ok(categories);
    }
}
