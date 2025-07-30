package com.edulearnorg.ltt.smeplanner.controller;

import com.edulearnorg.ltt.smeplanner.dto.UserAvailabilitySearchRequest;
import com.edulearnorg.ltt.smeplanner.dto.UserAvailabilityResponse;
import com.edulearnorg.ltt.smeplanner.dto.ErrorResponse;
import com.edulearnorg.ltt.smeplanner.enums.UserRole;
import com.edulearnorg.ltt.smeplanner.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ltt-sme-planner/v1/availability")
@CrossOrigin(origins = "*")
@Tag(name = "User Availability", description = "User availability search endpoints for finding available users based on date and time")
@SecurityRequirement(name = "bearerAuth")
public class UserAvailabilityController {
    
    @Autowired
    private ScheduleService scheduleService;
    
    /**
     * Search for user availability using GET request with query parameters
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search User Availability (GET)",
        description = "Search for user availability based on specific date and time using query parameters. Returns all users with their availability status."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User availability search completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserAvailabilityResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<?> searchUserAvailabilityByParams(
            @Parameter(description = "Date to search for availability (YYYY-MM-DD)", example = "2025-07-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Start time (HH:mm:ss)", example = "09:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime fromTime,
            @Parameter(description = "End time (HH:mm:ss)", example = "11:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime toTime,
            @Parameter(description = "User roles to filter by (comma-separated)", example = "SME,SUPERVISOR")
            @RequestParam(required = false) String roles,
            @Parameter(description = "User IDs to search within (comma-separated)", example = "1,2,3")
            @RequestParam(required = false) String userIds) {
        try {
            // Validate date and time logic
            if (fromTime.isAfter(toTime)) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "From time must be before to time"));
            }
            
            // Create search request object
            UserAvailabilitySearchRequest searchRequest = new UserAvailabilitySearchRequest();
            searchRequest.setDate(date);
            searchRequest.setFromTime(fromTime);
            searchRequest.setToTime(toTime);
            
            // Parse roles if provided
            if (roles != null && !roles.trim().isEmpty()) {
                try {
                    List<UserRole> roleList = Arrays.stream(roles.split(","))
                            .map(String::trim)
                            .map(UserRole::valueOf)
                            .collect(Collectors.toList());
                    searchRequest.setRoles(roleList);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                            .body(new ErrorResponse(400, "Invalid role specified. Valid roles are: " + 
                                    Arrays.toString(UserRole.values())));
                }
            }
            
            // Parse user IDs if provided
            if (userIds != null && !userIds.trim().isEmpty()) {
                try {
                    List<Long> userIdList = Arrays.stream(userIds.split(","))
                            .map(String::trim)
                            .map(Long::valueOf)
                            .collect(Collectors.toList());
                    searchRequest.setUserIds(userIdList);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(new ErrorResponse(400, "Invalid user ID format. User IDs must be numbers."));
                }
            }
            
            List<UserAvailabilityResponse> availabilityResults = scheduleService.searchUserAvailability(searchRequest);
            return ResponseEntity.ok(availabilityResults);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to search user availability: " + e.getMessage()));
        }
    }
    
    /**
     * Search for available users only using GET request with query parameters
     */
    @GetMapping("/search/available")
    @Operation(
        summary = "Search Available Users Only (GET)",
        description = "Search for users who are available for the specified date and time using query parameters. Returns only available users."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Available users search completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserAvailabilityResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<?> searchAvailableUsersByParams(
            @Parameter(description = "Date to search for availability (YYYY-MM-DD)", example = "2025-07-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Start time (HH:mm:ss)", example = "09:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime fromTime,
            @Parameter(description = "End time (HH:mm:ss)", example = "11:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime toTime,
            @Parameter(description = "User roles to filter by (comma-separated)", example = "SME,SUPERVISOR")
            @RequestParam(required = false) String roles,
            @Parameter(description = "User IDs to search within (comma-separated)", example = "1,2,3")
            @RequestParam(required = false) String userIds) {
        try {
            // Validate date and time logic
            if (fromTime.isAfter(toTime)) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "From time must be before to time"));
            }
            
            // Create search request object
            UserAvailabilitySearchRequest searchRequest = new UserAvailabilitySearchRequest();
            searchRequest.setDate(date);
            searchRequest.setFromTime(fromTime);
            searchRequest.setToTime(toTime);
            
            // Parse roles if provided
            if (roles != null && !roles.trim().isEmpty()) {
                try {
                    List<UserRole> roleList = Arrays.stream(roles.split(","))
                            .map(String::trim)
                            .map(UserRole::valueOf)
                            .collect(Collectors.toList());
                    searchRequest.setRoles(roleList);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                            .body(new ErrorResponse(400, "Invalid role specified. Valid roles are: " + 
                                    Arrays.toString(UserRole.values())));
                }
            }
            
            // Parse user IDs if provided
            if (userIds != null && !userIds.trim().isEmpty()) {
                try {
                    List<Long> userIdList = Arrays.stream(userIds.split(","))
                            .map(String::trim)
                            .map(Long::valueOf)
                            .collect(Collectors.toList());
                    searchRequest.setUserIds(userIdList);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest()
                            .body(new ErrorResponse(400, "Invalid user ID format. User IDs must be numbers."));
                }
            }
            
            List<UserAvailabilityResponse> availableUsers = scheduleService.searchAvailableUsers(searchRequest);
            return ResponseEntity.ok(availableUsers);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to search available users: " + e.getMessage()));
        }
    }
    
    /**
     * Search for user availability based on specific date and time
     */
    @PostMapping("/search")
    @Operation(
        summary = "Search User Availability",
        description = "Search for user availability based on specific date and time. Returns all users with their availability status."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User availability search completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserAvailabilityResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<?> searchUserAvailability(@Valid @RequestBody UserAvailabilitySearchRequest searchRequest) {
        try {
            // Validate date and time logic
            if (searchRequest.getFromTime().isAfter(searchRequest.getToTime())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "From time must be before to time"));
            }
            
            List<UserAvailabilityResponse> availabilityResults = scheduleService.searchUserAvailability(searchRequest);
            return ResponseEntity.ok(availabilityResults);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to search user availability: " + e.getMessage()));
        }
    }
    
    /**
     * Search for available users only (filtered results)
     */
    @PostMapping("/search/available")
    @Operation(
        summary = "Search Available Users Only",
        description = "Search for users who are available for the specified date and time. Returns only available users."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Available users search completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserAvailabilityResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<?> searchAvailableUsers(@Valid @RequestBody UserAvailabilitySearchRequest searchRequest) {
        try {
            // Validate date and time logic
            if (searchRequest.getFromTime().isAfter(searchRequest.getToTime())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "From time must be before to time"));
            }
            
            List<UserAvailabilityResponse> availableUsers = scheduleService.searchAvailableUsers(searchRequest);
            return ResponseEntity.ok(availableUsers);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to search available users: " + e.getMessage()));
        }
    }
}
