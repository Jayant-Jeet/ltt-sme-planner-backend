package com.edulearnorg.ltt.smeplanner.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edulearnorg.ltt.smeplanner.entity.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    // Find schedules by user ID
    List<Schedule> findByUserId(Long userId);
    
    // Find schedules by activity ID
    List<Schedule> findByActivityId(Long activityId);
    
    // Find schedules by date range
    @Query("SELECT s FROM Schedule s WHERE s.fromDate >= :startDate AND s.toDate <= :endDate")
    List<Schedule> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find schedules by user and date range
    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId AND s.fromDate >= :startDate AND s.toDate <= :endDate")
    List<Schedule> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                           @Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    // Find overlapping schedules for a user
    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId AND " +
           "((s.fromDate <= :toDate AND s.toDate >= :fromDate) AND " +
           "(s.fromTime <= :toTime AND s.toTime >= :fromTime))")
    List<Schedule> findOverlappingSchedules(@Param("userId") Long userId,
                                           @Param("fromDate") LocalDate fromDate,
                                           @Param("toDate") LocalDate toDate,
                                           @Param("fromTime") LocalTime fromTime,
                                           @Param("toTime") LocalTime toTime);
    
    // Find all schedules for multiple users on a specific date
    @Query("SELECT s FROM Schedule s WHERE s.userId IN :userIds AND s.fromDate <= :date AND s.toDate >= :date")
    List<Schedule> findByUserIdsAndDate(@Param("userIds") List<Long> userIds, @Param("date") LocalDate date);
    
    // Find schedules for multiple users within a date and time range
    @Query("SELECT s FROM Schedule s WHERE s.userId IN :userIds AND " +
           "((s.fromDate <= :toDate AND s.toDate >= :fromDate) AND " +
           "(s.fromTime <= :toTime AND s.toTime >= :fromTime))")
    List<Schedule> findOverlappingSchedulesForUsers(@Param("userIds") List<Long> userIds,
                                                   @Param("fromDate") LocalDate fromDate,
                                                   @Param("toDate") LocalDate toDate,
                                                   @Param("fromTime") LocalTime fromTime,
                                                   @Param("toTime") LocalTime toTime);
}
