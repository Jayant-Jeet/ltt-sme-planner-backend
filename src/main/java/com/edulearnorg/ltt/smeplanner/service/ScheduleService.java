package com.edulearnorg.ltt.smeplanner.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edulearnorg.ltt.smeplanner.entity.Schedule;
import com.edulearnorg.ltt.smeplanner.entity.User;
import com.edulearnorg.ltt.smeplanner.enums.UserRole;
import com.edulearnorg.ltt.smeplanner.repository.ScheduleRepository;
import com.edulearnorg.ltt.smeplanner.repository.UserRepository;
import com.edulearnorg.ltt.smeplanner.dto.UserAvailabilityResponse;
import com.edulearnorg.ltt.smeplanner.dto.UserAvailabilitySearchRequest;

@Service
public class ScheduleService {
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SmeActivityGroupService smeActivityGroupService;
    
    /**
     * Get all schedules
     */
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }
    
    /**
     * Get schedule by ID
     */
    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }
    
    /**
     * Get schedules by user ID
     */
    public List<Schedule> getSchedulesByUserId(Long userId) {
        return scheduleRepository.findByUserId(userId);
    }
    
    /**
     * Get schedules by activity ID
     */
    public List<Schedule> getSchedulesByActivityId(Long activityId) {
        return scheduleRepository.findByActivityId(activityId);
    }
    
    /**
     * Get schedules within date range
     */
    public List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Get schedules for a user within date range
     */
    public List<Schedule> getSchedulesByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }
    
    /**
     * Create a new schedule
     */
    public Schedule createSchedule(Schedule schedule) {
        // Check for overlapping schedules
        List<Schedule> overlapping = scheduleRepository.findOverlappingSchedules(
            schedule.getUserId(),
            schedule.getFromDate(),
            schedule.getToDate(),
            schedule.getFromTime(),
            schedule.getToTime()
        );
        
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Schedule conflicts with existing schedule(s)");
        }
        
        Schedule savedSchedule = scheduleRepository.save(schedule);
        
        // Process for SME activity grouping if the user can act as an SME (SME, SUPERVISOR, or LEAD)
        User user = userRepository.findById(schedule.getUserId()).orElse(null);
        if (user != null && canActAsSme(user)) {
            smeActivityGroupService.processScheduleForGrouping(savedSchedule);
        }
        
        return savedSchedule;
    }
    
    /**
     * Update an existing schedule
     */
    public Schedule updateSchedule(Long id, Schedule updatedSchedule) {
        Optional<Schedule> existingSchedule = scheduleRepository.findById(id);
        
        if (existingSchedule.isPresent()) {
            Schedule schedule = existingSchedule.get();
            
            // Check for overlapping schedules (excluding current schedule)
            List<Schedule> overlapping = scheduleRepository.findOverlappingSchedules(
                updatedSchedule.getUserId(),
                updatedSchedule.getFromDate(),
                updatedSchedule.getToDate(),
                updatedSchedule.getFromTime(),
                updatedSchedule.getToTime()
            );
            
            // Remove current schedule from overlapping list
            overlapping.removeIf(s -> s.getId().equals(id));
            
            if (!overlapping.isEmpty()) {
                throw new IllegalArgumentException("Updated schedule conflicts with existing schedule(s)");
            }
            
            // Update fields
            schedule.setUserId(updatedSchedule.getUserId());
            schedule.setFromDate(updatedSchedule.getFromDate());
            schedule.setToDate(updatedSchedule.getToDate());
            schedule.setFromTime(updatedSchedule.getFromTime());
            schedule.setToTime(updatedSchedule.getToTime());
            schedule.setActivityId(updatedSchedule.getActivityId());
            schedule.setActivityName(updatedSchedule.getActivityName());
            schedule.setDescription(updatedSchedule.getDescription());
            
            return scheduleRepository.save(schedule);
        } else {
            throw new IllegalArgumentException("Schedule not found with id: " + id);
        }
    }
    
    /**
     * Delete a schedule
     */
    public void deleteSchedule(Long id) {
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Schedule not found with id: " + id);
        }
    }
    
    /**
     * Check if a user has any schedule conflicts
     */
    public boolean hasScheduleConflict(Long userId, LocalDate fromDate, LocalDate toDate, 
                                     LocalTime fromTime, LocalTime toTime) {
        List<Schedule> overlapping = scheduleRepository.findOverlappingSchedules(
            userId, fromDate, toDate, fromTime, toTime
        );
        return !overlapping.isEmpty();
    }
    
    /**
     * Search for user availability based on specific date and time
     */
    public List<UserAvailabilityResponse> searchUserAvailability(UserAvailabilitySearchRequest searchRequest) {
        List<User> usersToCheck = getUsersToCheck(searchRequest);
        List<UserAvailabilityResponse> availabilityResponses = new ArrayList<>();
        
        for (User user : usersToCheck) {
            UserAvailabilityResponse availability = checkUserAvailability(
                user, searchRequest.getDate(), searchRequest.getFromTime(), searchRequest.getToTime()
            );
            availabilityResponses.add(availability);
        }
        
        return availabilityResponses;
    }
    
    /**
     * Search for available users only (filtered results)
     */
    public List<UserAvailabilityResponse> searchAvailableUsers(UserAvailabilitySearchRequest searchRequest) {
        return searchUserAvailability(searchRequest).stream()
                .filter(UserAvailabilityResponse::isAvailable)
                .collect(Collectors.toList());
    }
    
    /**
     * Get list of users to check based on search criteria
     */
    private List<User> getUsersToCheck(UserAvailabilitySearchRequest searchRequest) {
        List<User> users;
        
        // If specific user IDs are provided, filter by them
        if (searchRequest.getUserIds() != null && !searchRequest.getUserIds().isEmpty()) {
            users = userRepository.findAllById(searchRequest.getUserIds());
        } 
        // If roles are specified, filter by roles
        else if (searchRequest.getRoles() != null && !searchRequest.getRoles().isEmpty()) {
            users = new ArrayList<>();
            for (UserRole role : searchRequest.getRoles()) {
                users.addAll(userRepository.findByRole(role));
            }
        } 
        // Otherwise, get all users
        else {
            users = userRepository.findAll();
        }
        
        return users;
    }
    
    /**
     * Check availability for a specific user
     */
    private UserAvailabilityResponse checkUserAvailability(User user, LocalDate date, 
                                                         LocalTime fromTime, LocalTime toTime) {
        // Find conflicting schedules
        List<Schedule> conflictingSchedules = scheduleRepository.findOverlappingSchedules(
            user.getId(), date, date, fromTime, toTime
        );
        
        boolean isAvailable = conflictingSchedules.isEmpty();
        
        // Convert conflicting schedules to response format
        List<UserAvailabilityResponse.ConflictingSchedule> conflicts = conflictingSchedules.stream()
                .map(this::convertToConflictingSchedule)
                .collect(Collectors.toList());
        
        return new UserAvailabilityResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            isAvailable,
            conflicts
        );
    }
    
    /**
     * Convert Schedule entity to ConflictingSchedule DTO
     */
    private UserAvailabilityResponse.ConflictingSchedule convertToConflictingSchedule(Schedule schedule) {
        return new UserAvailabilityResponse.ConflictingSchedule(
            schedule.getId(),
            schedule.getFromDate(),
            schedule.getToDate(),
            schedule.getFromTime(),
            schedule.getToTime(),
            schedule.getActivityName(),
            schedule.getDescription()
        );
    }
    
    /**
     * Helper method to check if a user can act as an SME
     * Since all supervisors and leads are also SMEs, this method checks for all three roles
     */
    private boolean canActAsSme(User user) {
        UserRole role = user.getRole();
        return role == UserRole.SME || role == UserRole.SUPERVISOR || role == UserRole.LEAD;
    }
}
