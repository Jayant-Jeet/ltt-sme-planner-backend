package com.edulearnorg.ltt.smeplanner.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edulearnorg.ltt.smeplanner.config.JwtUtil;
import com.edulearnorg.ltt.smeplanner.dto.BulkScheduleCreateRequest;
import com.edulearnorg.ltt.smeplanner.dto.BulkScheduleCreateResponse;
import com.edulearnorg.ltt.smeplanner.dto.CreateScheduleRequest;
import com.edulearnorg.ltt.smeplanner.dto.ErrorResponse;
import com.edulearnorg.ltt.smeplanner.dto.ScheduleResponse;
import com.edulearnorg.ltt.smeplanner.dto.UpdateScheduleRequest;
import com.edulearnorg.ltt.smeplanner.entity.Schedule;
import com.edulearnorg.ltt.smeplanner.entity.User;
import com.edulearnorg.ltt.smeplanner.enums.UserRole;
import com.edulearnorg.ltt.smeplanner.exception.InvalidTokenException;
import com.edulearnorg.ltt.smeplanner.repository.UserRepository;
import com.edulearnorg.ltt.smeplanner.service.ScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ltt-sme-planner/v1/schedules")
@CrossOrigin(origins = "*")
@Tag(name = "Schedules", description = "Schedule management endpoints for creating, viewing, updating and deleting schedules")
@SecurityRequirement(name = "bearerAuth")
public class ScheduleController {
    
    @Autowired
    private ScheduleService scheduleService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final String DATE_ERROR = "From date must be before or equal to to date";

    private static final String TIME_ERROR = "From time must be before to time on the same date";

    /**
     * Get all schedules for the authenticated user
     */
    @GetMapping
    @Operation(
        summary = "Get User Schedules",
        description = "Retrieve all schedules for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Schedules retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ScheduleResponse.class)
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
    public ResponseEntity<Object> getUserSchedules(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            List<Schedule> schedules = scheduleService.getSchedulesByUserId(userId);
            List<ScheduleResponse> response = schedules.stream()
                    .map(this::convertToScheduleResponse)
                    .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to retrieve schedules: " + e.getMessage()));
        }
    }
    
    /**
     * Get schedules for the authenticated user within a date range
     */
    @GetMapping("/date-range")
    @Operation(
        summary = "Get User Schedules by Date Range",
        description = "Retrieve schedules for the authenticated user within a specific date range"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Schedules retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ScheduleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid date format or range",
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
    public ResponseEntity<Object> getUserSchedulesByDateRange(
            @Parameter(description = "Start date (YYYY-MM-DD)", example = "2025-07-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)", example = "2025-07-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "Start date must be before or equal to end date"));
            }
            
            List<Schedule> schedules = scheduleService.getSchedulesByUserIdAndDateRange(userId, startDate, endDate);
            List<ScheduleResponse> response = schedules.stream()
                    .map(this::convertToScheduleResponse)
                    .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to retrieve schedules: " + e.getMessage()));
        }
    }
    
    /**
     * Get a specific schedule by ID (only if it belongs to the authenticated user)
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get Schedule by ID",
        description = "Retrieve a specific schedule by its ID (only if it belongs to the authenticated user)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Schedule retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ScheduleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Schedule doesn't belong to user",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Schedule not found",
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
    public ResponseEntity<Object> getScheduleById(
            @Parameter(description = "Schedule ID", example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            Optional<Schedule> scheduleOpt = scheduleService.getScheduleById(id);
            
            if (scheduleOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Schedule schedule = scheduleOpt.get();
            if (!schedule.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(403, "You can only access your own schedules"));
            }
            
            return ResponseEntity.ok(convertToScheduleResponse(schedule));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to retrieve schedule: " + e.getMessage()));
        }
    }
    
    /**
     * Get schedules for a specific SME by ID and month (accessible by SUPERVISOR and LEAD roles)
     */
    @GetMapping("/sme/{smeId}")
    @Operation(
        summary = "Get SME Schedules",
        description = "Retrieve schedules for a specific SME by ID, optionally filtered by month. Accessible by SME, SUPERVISOR and LEAD roles."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "SME schedules retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ScheduleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions or target user is not an SME",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "SME not found",
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
    public ResponseEntity<Object> getSMESchedules(
            @Parameter(description = "SME User ID", example = "5")
            @PathVariable Long smeId,
            @Parameter(description = "Month filter (YYYY-MM format)", example = "2025-07")
            @RequestParam(required = false) String month,
            HttpServletRequest request) {
        try {
            String currentUserRole = getCurrentUserRole(request);
            
            // Check if user has permission to view SME schedules (SME, SUPERVISOR, or LEAD)
            if (!("SME".equals(currentUserRole) || "SUPERVISOR".equals(currentUserRole) || "LEAD".equals(currentUserRole))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(403, "Only SME, SUPERVISOR and LEAD roles can view SME schedules"));
            }
            
            // Verify that the SME exists and can act as SME
            Optional<com.edulearnorg.ltt.smeplanner.entity.User> smeUserOpt = getUserById(smeId);
            if (smeUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(404, "SME not found with ID: " + smeId));
            }
            
            com.edulearnorg.ltt.smeplanner.entity.User smeUser = smeUserOpt.get();
            if (!canActAsSme(smeUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(403, "User with ID " + smeId + " is not an SME"));
            }
            
            List<Schedule> schedules = getSchedulesForSme(smeId, month);
            if (schedules == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "Invalid month format. Expected YYYY-MM format."));
            }
            
            List<ScheduleResponse> response = schedules.stream()
                    .map(this::convertToScheduleResponse)
                    .toList();
                    
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to retrieve SME schedules: " + e.getMessage()));
        }
    }
    
    /**
     * Create a new schedule for the authenticated user
     */
    @PostMapping
    @Operation(
        summary = "Create Schedule",
        description = "Create a new schedule for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Schedule created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ScheduleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data or schedule conflict",
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
    public ResponseEntity<Object> createSchedule(
            @Valid @RequestBody CreateScheduleRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            
            // Validate that the user is scheduling for themselves
            if (!request.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(403, "You can only create schedules for yourself"));
            }
            
            // Validate date and time logic
            if (request.getFromDate().isAfter(request.getToDate())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, DATE_ERROR));
            }
            
            if (request.getFromDate().equals(request.getToDate()) && 
                request.getFromTime().isAfter(request.getToTime())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, TIME_ERROR));
            }
            
            Schedule schedule = convertToSchedule(request);
            schedule.setCreatedAt(LocalDateTime.now());
            schedule.setUpdatedAt(LocalDateTime.now());
            
            Schedule savedSchedule = scheduleService.createSchedule(schedule);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(convertToScheduleResponse(savedSchedule));
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to create schedule: " + e.getMessage()));
        }
    }
    
    /**
     * Update an existing schedule (only if it belongs to the authenticated user)
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update Schedule",
        description = "Update an existing schedule (only if it belongs to the authenticated user)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Schedule updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ScheduleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data or schedule conflict",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Schedule doesn't belong to user",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Schedule not found",
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
    public ResponseEntity<Object> updateSchedule(
            @Parameter(description = "Schedule ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateScheduleRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            
            // Check if schedule exists and belongs to user
            Optional<Schedule> existingScheduleOpt = scheduleService.getScheduleById(id);
            if (existingScheduleOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Schedule existingSchedule = existingScheduleOpt.get();
            if (!existingSchedule.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(403, "You can only update your own schedules"));
            }
            
            // Validate date and time logic
            if (request.getFromDate().isAfter(request.getToDate())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, DATE_ERROR));
            }
            
            if (request.getFromDate().equals(request.getToDate()) && 
                request.getFromTime().isAfter(request.getToTime())) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, TIME_ERROR));
            }
            
            Schedule updatedSchedule = convertToScheduleForUpdate(request, userId);
            updatedSchedule.setUpdatedAt(LocalDateTime.now());
            
            Schedule savedSchedule = scheduleService.updateSchedule(id, updatedSchedule);
            return ResponseEntity.ok(convertToScheduleResponse(savedSchedule));
                    
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to update schedule: " + e.getMessage()));
        }
    }
    
    /**
     * Delete a schedule (only if it belongs to the authenticated user)
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete Schedule",
        description = "Delete a schedule (only if it belongs to the authenticated user)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Schedule deleted successfully"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Schedule doesn't belong to user",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Schedule not found",
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
    public ResponseEntity<Object> deleteSchedule(
            @Parameter(description = "Schedule ID", example = "1")
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            
            // Check if schedule exists and belongs to user
            Optional<Schedule> scheduleOpt = scheduleService.getScheduleById(id);
            if (scheduleOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Schedule schedule = scheduleOpt.get();
            if (!schedule.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(403, "You can only delete your own schedules"));
            }
            
            scheduleService.deleteSchedule(id);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to delete schedule: " + e.getMessage()));
        }
    }
    
    /**
     * Check for schedule conflicts
     */
    @PostMapping("/check-conflict")
    @Operation(
        summary = "Check Schedule Conflict",
        description = "Check if a proposed schedule conflicts with existing schedules for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Conflict check completed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Boolean.class)
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
    public ResponseEntity<Object> checkScheduleConflict(
            @Valid @RequestBody CreateScheduleRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            
            boolean hasConflict = scheduleService.hasScheduleConflict(
                userId, 
                request.getFromDate(), 
                request.getToDate(),
                request.getFromTime(), 
                request.getToTime()
            );
            
            return ResponseEntity.ok(hasConflict);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to check schedule conflict: " + e.getMessage()));
        }
    }
    
    /**
     * Bulk create schedules for the authenticated user
     */
    @PostMapping("/bulk")
    @Operation(
        summary = "Bulk Create Schedules",
        description = "Create multiple schedules for the authenticated user in a single operation"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Bulk operation completed (may contain both successes and failures)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BulkScheduleCreateResponse.class)
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
    public ResponseEntity<Object> bulkCreateSchedules(
            @Valid @RequestBody BulkScheduleCreateRequest request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            
            List<CreateScheduleRequest> schedules = request.getSchedules();
            List<ScheduleResponse> createdSchedules = new ArrayList<>();
            List<BulkScheduleCreateResponse.BulkScheduleError> errors = new ArrayList<>();
            
            // Pre-validate user ownership
            validateUserOwnership(schedules, userId, errors);
            
            if (!errors.isEmpty() && !request.isSkipConflicts()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(400, "Validation failed for some schedules"));
            }
            
            // Process each schedule
            processBulkSchedules(schedules, createdSchedules, errors);
            
            BulkScheduleCreateResponse response = createBulkResponse(schedules, createdSchedules, errors);
            return ResponseEntity.ok(response);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Failed to process bulk schedule creation: " + e.getMessage()));
        }
    }
    
    private void validateUserOwnership(List<CreateScheduleRequest> schedules, Long userId, 
                                     List<BulkScheduleCreateResponse.BulkScheduleError> errors) {
        for (int i = 0; i < schedules.size(); i++) {
            CreateScheduleRequest scheduleRequest = schedules.get(i);
            if (!scheduleRequest.getUserId().equals(userId)) {
                errors.add(new BulkScheduleCreateResponse.BulkScheduleError(
                    i, "You can only create schedules for yourself", scheduleRequest
                ));
            }
        }
    }
    
    private void processBulkSchedules(List<CreateScheduleRequest> schedules,
                                    List<ScheduleResponse> createdSchedules,
                                    List<BulkScheduleCreateResponse.BulkScheduleError> errors) {
        for (int i = 0; i < schedules.size(); i++) {
            final int index = i;
            CreateScheduleRequest scheduleRequest = schedules.get(i);
            
            // Skip if this schedule already has a validation error
            if (!hasExistingError(errors, index)) {
                String validationError = validateScheduleRequest(scheduleRequest);
                if (validationError != null) {
                    errors.add(new BulkScheduleCreateResponse.BulkScheduleError(index, validationError, scheduleRequest));
                } else {
                    processIndividualSchedule(index, scheduleRequest, createdSchedules, errors);
                }
            }
        }
    }
    
    private boolean hasExistingError(List<BulkScheduleCreateResponse.BulkScheduleError> errors, int index) {
        return errors.stream().anyMatch(error -> error.getIndex() == index);
    }
    
    private String validateScheduleRequest(CreateScheduleRequest scheduleRequest) {
        if (scheduleRequest.getFromDate().isAfter(scheduleRequest.getToDate())) {
            return DATE_ERROR;
        }
        
        if (scheduleRequest.getFromDate().equals(scheduleRequest.getToDate()) && 
            scheduleRequest.getFromTime().isAfter(scheduleRequest.getToTime())) {
            return TIME_ERROR;
        }
        
        return null;
    }
    
    private void processIndividualSchedule(int index, CreateScheduleRequest scheduleRequest,
                                         List<ScheduleResponse> createdSchedules,
                                         List<BulkScheduleCreateResponse.BulkScheduleError> errors) {
        try {
            Schedule schedule = convertToSchedule(scheduleRequest);
            schedule.setCreatedAt(LocalDateTime.now());
            schedule.setUpdatedAt(LocalDateTime.now());
            
            Schedule savedSchedule = scheduleService.createSchedule(schedule);
            createdSchedules.add(convertToScheduleResponse(savedSchedule));
            
        } catch (IllegalArgumentException e) {
            errors.add(new BulkScheduleCreateResponse.BulkScheduleError(index, e.getMessage(), scheduleRequest));
        } catch (Exception e) {
            errors.add(new BulkScheduleCreateResponse.BulkScheduleError(
                index, "Unexpected error: " + e.getMessage(), scheduleRequest
            ));
        }
    }
    
    private BulkScheduleCreateResponse createBulkResponse(List<CreateScheduleRequest> schedules,
                                                        List<ScheduleResponse> createdSchedules,
                                                        List<BulkScheduleCreateResponse.BulkScheduleError> errors) {
        return new BulkScheduleCreateResponse(
            schedules.size(),
            createdSchedules.size(),
            errors.size(),
            createdSchedules,
            errors
        );
    }
    
    // Helper methods
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Authorization header missing or invalid format");
        }
        
        try {
            String token = authHeader.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired token", e);
        }
    }
    
    private Schedule convertToSchedule(CreateScheduleRequest request) {
        Schedule schedule = new Schedule();
        schedule.setUserId(request.getUserId());
        schedule.setFromDate(request.getFromDate());
        schedule.setToDate(request.getToDate());
        schedule.setFromTime(request.getFromTime());
        schedule.setToTime(request.getToTime());
        schedule.setActivityId(request.getActivityId());
        schedule.setActivityName(request.getActivityName());
        schedule.setDescription(request.getDescription());
        return schedule;
    }
    
    private Schedule convertToScheduleForUpdate(UpdateScheduleRequest request, Long userId) {
        Schedule schedule = new Schedule();
        schedule.setUserId(userId);
        schedule.setFromDate(request.getFromDate());
        schedule.setToDate(request.getToDate());
        schedule.setFromTime(request.getFromTime());
        schedule.setToTime(request.getToTime());
        schedule.setActivityId(request.getActivityId());
        schedule.setActivityName(request.getActivityName());
        schedule.setDescription(request.getDescription());
        return schedule;
    }
    
    private ScheduleResponse convertToScheduleResponse(Schedule schedule) {
        ScheduleResponse response = new ScheduleResponse();
        response.setId(schedule.getId());
        response.setUserId(schedule.getUserId());
        response.setFromDate(schedule.getFromDate());
        response.setToDate(schedule.getToDate());
        response.setFromTime(schedule.getFromTime());
        response.setToTime(schedule.getToTime());
        response.setActivityId(schedule.getActivityId());
        response.setActivityName(schedule.getActivityName());
        response.setDescription(schedule.getDescription());
        response.setCreatedAt(schedule.getCreatedAt());
        response.setUpdatedAt(schedule.getUpdatedAt());
        return response;
    }
    
    private String getCurrentUserRole(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Authorization header missing or invalid format");
        }
        
        try {
            String token = authHeader.substring(7);
            return jwtUtil.getRoleFromToken(token);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired token", e);
        }
    }
    
    private Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    private boolean canActAsSme(User user) {
        UserRole role = user.getRole();
        return role == UserRole.SME || role == UserRole.SUPERVISOR || role == UserRole.LEAD;
    }
    
    private List<Schedule> getSchedulesForSme(Long smeId, String month) {
        if (month != null && !month.trim().isEmpty()) {
            // Parse month (YYYY-MM format) and get schedules for that month
            try {
                YearMonth yearMonth = YearMonth.parse(month);
                LocalDate startDate = yearMonth.atDay(1);
                LocalDate endDate = yearMonth.atEndOfMonth();
                return scheduleService.getSchedulesByUserIdAndDateRange(smeId, startDate, endDate);
            } catch (Exception e) {
                return new ArrayList<>(); // Indicates invalid month format
            }
        } else {
            // Get all schedules for the SME
            return scheduleService.getSchedulesByUserId(smeId);
        }
    }
}
