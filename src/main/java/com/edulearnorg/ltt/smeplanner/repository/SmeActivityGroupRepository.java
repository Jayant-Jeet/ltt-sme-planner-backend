package com.edulearnorg.ltt.smeplanner.repository;

import com.edulearnorg.ltt.smeplanner.entity.SmeActivityGroup;
import com.edulearnorg.ltt.smeplanner.enums.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmeActivityGroupRepository extends JpaRepository<SmeActivityGroup, Long> {
    
    /**
     * Find SME activity group by SME user ID, activity ID, and month-year
     */
    Optional<SmeActivityGroup> findBySmeUserIdAndActivityIdAndMonthYear(
            Long smeUserId, Long activityId, String monthYear);
    
    /**
     * Find all activity groups for a specific SME
     */
    List<SmeActivityGroup> findBySmeUserId(Long smeUserId);
    
    /**
     * Find all activity groups for a specific SME in a given month
     */
    List<SmeActivityGroup> findBySmeUserIdAndMonthYear(Long smeUserId, String monthYear);
    
    /**
     * Find all activity groups by category
     */
    List<SmeActivityGroup> findByCategory(ActivityCategory category);
    
    /**
     * Find all activity groups by category for a specific SME
     */
    List<SmeActivityGroup> findBySmeUserIdAndCategory(Long smeUserId, ActivityCategory category);
    
    /**
     * Get total hours allocated by SME for a specific month
     */
    @Query("SELECT COALESCE(SUM(sag.totalHoursAllocated), 0) FROM SmeActivityGroup sag " +
           "WHERE sag.smeUserId = :smeUserId AND sag.monthYear = :monthYear")
    Double getTotalHoursBySmeAndMonth(@Param("smeUserId") Long smeUserId, 
                                     @Param("monthYear") String monthYear);
    
    /**
     * Get total hours allocated by category for a specific SME and month
     */
    @Query("SELECT COALESCE(SUM(sag.totalHoursAllocated), 0) FROM SmeActivityGroup sag " +
           "WHERE sag.smeUserId = :smeUserId AND sag.category = :category AND sag.monthYear = :monthYear")
    Double getTotalHoursBySmeAndCategoryAndMonth(@Param("smeUserId") Long smeUserId, 
                                               @Param("category") ActivityCategory category,
                                               @Param("monthYear") String monthYear);
    
    /**
     * Get activity distribution by category for a specific SME
     */
    @Query("SELECT sag.category, COUNT(sag), SUM(sag.totalHoursAllocated), SUM(sag.totalSessions) " +
           "FROM SmeActivityGroup sag " +
           "WHERE sag.smeUserId = :smeUserId AND sag.monthYear = :monthYear " +
           "GROUP BY sag.category")
    List<Object[]> getActivityDistributionBySmeAndMonth(@Param("smeUserId") Long smeUserId, 
                                                       @Param("monthYear") String monthYear);
    
    /**
     * Get all SMEs with activity groupings for a specific month
     */
    @Query("SELECT DISTINCT sag.smeUserId FROM SmeActivityGroup sag WHERE sag.monthYear = :monthYear")
    List<Long> getActiveSmesByMonth(@Param("monthYear") String monthYear);
}
