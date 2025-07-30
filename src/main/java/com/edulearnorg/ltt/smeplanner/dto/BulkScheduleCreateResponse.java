package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response DTO for bulk schedule creation
 */
@Schema(description = "Response for bulk schedule creation operation")
public class BulkScheduleCreateResponse {
    
    @Schema(description = "Total number of schedules attempted to create", example = "10")
    private int totalRequested;
    
    @Schema(description = "Number of schedules successfully created", example = "8")
    private int successfullyCreated;
    
    @Schema(description = "Number of schedules that failed to create", example = "2")
    private int failed;
    
    @Schema(description = "List of successfully created schedules")
    private List<ScheduleResponse> createdSchedules;
    
    @Schema(description = "List of errors for failed schedule creations")
    private List<BulkScheduleError> errors;
    
    // Constructors
    public BulkScheduleCreateResponse() {}
    
    public BulkScheduleCreateResponse(int totalRequested, int successfullyCreated, int failed,
                                    List<ScheduleResponse> createdSchedules, List<BulkScheduleError> errors) {
        this.totalRequested = totalRequested;
        this.successfullyCreated = successfullyCreated;
        this.failed = failed;
        this.createdSchedules = createdSchedules;
        this.errors = errors;
    }
    
    // Getters and Setters
    public int getTotalRequested() {
        return totalRequested;
    }
    
    public void setTotalRequested(int totalRequested) {
        this.totalRequested = totalRequested;
    }
    
    public int getSuccessfullyCreated() {
        return successfullyCreated;
    }
    
    public void setSuccessfullyCreated(int successfullyCreated) {
        this.successfullyCreated = successfullyCreated;
    }
    
    public int getFailed() {
        return failed;
    }
    
    public void setFailed(int failed) {
        this.failed = failed;
    }
    
    public List<ScheduleResponse> getCreatedSchedules() {
        return createdSchedules;
    }
    
    public void setCreatedSchedules(List<ScheduleResponse> createdSchedules) {
        this.createdSchedules = createdSchedules;
    }
    
    public List<BulkScheduleError> getErrors() {
        return errors;
    }
    
    public void setErrors(List<BulkScheduleError> errors) {
        this.errors = errors;
    }
    
    /**
     * Inner class for representing errors in bulk operations
     */
    @Schema(description = "Error details for a failed schedule creation")
    public static class BulkScheduleError {
        
        @Schema(description = "Index of the schedule in the request array", example = "2")
        private int index;
        
        @Schema(description = "Error message", example = "Schedule conflicts with existing schedule")
        private String message;
        
        @Schema(description = "The schedule request that failed")
        private CreateScheduleRequest failedSchedule;
        
        public BulkScheduleError() {}
        
        public BulkScheduleError(int index, String message, CreateScheduleRequest failedSchedule) {
            this.index = index;
            this.message = message;
            this.failedSchedule = failedSchedule;
        }
        
        public int getIndex() {
            return index;
        }
        
        public void setIndex(int index) {
            this.index = index;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public CreateScheduleRequest getFailedSchedule() {
            return failedSchedule;
        }
        
        public void setFailedSchedule(CreateScheduleRequest failedSchedule) {
            this.failedSchedule = failedSchedule;
        }
    }
    
    @Override
    public String toString() {
        return "BulkScheduleCreateResponse{" +
                "totalRequested=" + totalRequested +
                ", successfullyCreated=" + successfullyCreated +
                ", failed=" + failed +
                ", createdSchedules=" + createdSchedules +
                ", errors=" + errors +
                '}';
    }
}
