package com.edulearnorg.ltt.smeplanner.service;

import com.edulearnorg.ltt.smeplanner.dto.ConsolidatedMonthlyEffortResponse;
import com.edulearnorg.ltt.smeplanner.dto.MonthlyEffortDetailsResponse;
import com.edulearnorg.ltt.smeplanner.entity.SmeActivityGroup;
import com.edulearnorg.ltt.smeplanner.entity.User;
import com.edulearnorg.ltt.smeplanner.enums.ActivityCategory;
import com.edulearnorg.ltt.smeplanner.enums.UserRole;
import com.edulearnorg.ltt.smeplanner.repository.SmeActivityGroupRepository;
import com.edulearnorg.ltt.smeplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing monthly effort details and supervisor-reportee relationships
 */
@Service
public class MonthlyEffortService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SmeActivityGroupRepository smeActivityGroupRepository;
    
    /**
     * Get monthly effort details for an SME by supervisor
     */
    public MonthlyEffortDetailsResponse getMonthlyEffortDetailsForSupervisor(Long supervisorId, Long smeId, String monthYear) {
        // Check if the supervisor is trying to view their own data
        if (supervisorId.equals(smeId)) {
            // Allow supervisors to view their own effort data
            User supervisor = userRepository.findById(supervisorId)
                .orElseThrow(() -> new RuntimeException("Supervisor not found"));
            
            if (!canActAsSme(supervisor)) {
                throw new RuntimeException("User is not an SME");
            }
            
            return getMonthlyEffortDetails(supervisor, monthYear);
        }
        
        // Check if the SME is a reportee of the supervisor
        if (!userRepository.existsByIdAndSupervisorId(smeId, supervisorId)) {
            throw new RuntimeException("Cannot view the report");
        }
        
        // Verify the user can act as an SME (SME, SUPERVISOR, or LEAD can all be SMEs)
        User sme = userRepository.findById(smeId)
            .orElseThrow(() -> new RuntimeException("SME not found"));
        
        if (!canActAsSme(sme)) {
            throw new RuntimeException("User is not an SME");
        }
        
        return getMonthlyEffortDetails(sme, monthYear);
    }
    
    /**
     * Get monthly effort details for an SME by lead (no access restrictions)
     */
    public MonthlyEffortDetailsResponse getMonthlyEffortDetailsForLead(Long smeId, String monthYear) {
        // Verify the user can act as an SME (SME, SUPERVISOR, or LEAD can all be SMEs)
        User sme = userRepository.findById(smeId)
            .orElseThrow(() -> new RuntimeException("SME not found with ID: " + smeId));
        
        if (!canActAsSme(sme)) {
            throw new RuntimeException("User is not an SME");
        }
        
        return getMonthlyEffortDetails(sme, monthYear);
    }
    
    /**
     * Get monthly effort details for an SME
     */
    public MonthlyEffortDetailsResponse getMonthlyEffortDetails(User sme, String monthYear) {
        // Get activity groups for the SME and month
        List<SmeActivityGroup> activityGroups = smeActivityGroupRepository
            .findBySmeUserIdAndMonthYear(sme.getId(), monthYear);
        
        // Calculate counts and totals based on activity categories
        Long smeConnectCount = getTotalSessionsByCategory(activityGroups, ActivityCategory.MANAGEMENT);
        Long byteSizedCount = getTotalSessionsByCategory(activityGroups, ActivityCategory.BYTE_SIZED);
        Long lateralTrainingCount = getTotalSessionsByCategory(activityGroups, ActivityCategory.CALENDAR_TRAINING);
        Long questionContributionCount = getTotalSessionsByCategory(activityGroups, ActivityCategory.EVALUATION);
        
        Double totalHoursAllocated = activityGroups.stream()
            .mapToDouble(SmeActivityGroup::getTotalHoursAllocated)
            .sum();
        
        Long totalSessions = activityGroups.stream()
            .mapToLong(SmeActivityGroup::getTotalSessions)
            .sum();
        
        return new MonthlyEffortDetailsResponse(
            sme.getId(),
            sme.getName(),
            sme.getEmail(),
            monthYear,
            smeConnectCount,
            byteSizedCount,
            lateralTrainingCount,
            questionContributionCount,
            totalHoursAllocated,
            totalSessions
        );
    }
    
    /**
     * Get consolidated monthly effort details for all reportees of a supervisor
     */
    public ConsolidatedMonthlyEffortResponse getConsolidatedMonthlyEffortDetails(Long supervisorId, String monthYear) {
        // Get supervisor details
        User supervisor = userRepository.findById(supervisorId)
            .orElseThrow(() -> new RuntimeException("Supervisor not found"));
        
        if (supervisor.getRole() != UserRole.SUPERVISOR) {
            throw new RuntimeException("User is not a supervisor");
        }
        
        // Get all reportees of the supervisor
        List<User> reportees = userRepository.findBySupervisorIdAndRole(supervisorId, UserRole.SME);
        
        if (reportees.isEmpty()) {
            // Return empty response if no reportees found
            return new ConsolidatedMonthlyEffortResponse(
                supervisorId,
                supervisor.getName(),
                supervisor.getEmail(),
                monthYear,
                new ArrayList<>(),
                new ConsolidatedMonthlyEffortResponse.EffortTotals(0L, 0L, 0L, 0L, 0L, 0.0, 0L)
            );
        }
        
        // Collect effort details for each reportee
        List<ConsolidatedMonthlyEffortResponse.ReporteeEffortSummary> reporteeSummaries = new ArrayList<>();
        
        // Totals for consolidation
        long totalSmeConnectCount = 0;
        long totalByteSizedCount = 0;
        long totalLateralTrainingCount = 0;
        long totalQuestionContributionCount = 0;
        double totalHoursAllocated = 0.0;
        long totalSessions = 0;
        
        for (User reportee : reportees) {
            // Get activity groups for this reportee and month
            List<SmeActivityGroup> reporteeActivityGroups = smeActivityGroupRepository
                .findBySmeUserIdAndMonthYear(reportee.getId(), monthYear);
            
            // Calculate metrics for this reportee
            Long smeConnectCount = getTotalSessionsByCategory(reporteeActivityGroups, ActivityCategory.CALENDAR_TRAINING);
            Long byteSizedCount = getTotalSessionsByCategory(reporteeActivityGroups, ActivityCategory.BYTE_SIZED);
            Long lateralTrainingCount = getTotalSessionsByCategory(reporteeActivityGroups, ActivityCategory.CALENDAR_TRAINING);
            Long questionContributionCount = 0L; // Placeholder for future implementation
            
            Double reporteeHours = reporteeActivityGroups.stream()
                .mapToDouble(SmeActivityGroup::getTotalHoursAllocated)
                .sum();
            
            Long reporteeSessions = reporteeActivityGroups.stream()
                .mapToLong(SmeActivityGroup::getTotalSessions)
                .sum();
            
            // Create reportee summary
            ConsolidatedMonthlyEffortResponse.ReporteeEffortSummary summary = 
                new ConsolidatedMonthlyEffortResponse.ReporteeEffortSummary(
                    reportee.getId(),
                    reportee.getName(),
                    reportee.getEmail(),
                    smeConnectCount,
                    byteSizedCount,
                    lateralTrainingCount,
                    questionContributionCount,
                    reporteeHours,
                    reporteeSessions
                );
            
            reporteeSummaries.add(summary);
            
            // Add to totals
            totalSmeConnectCount += smeConnectCount;
            totalByteSizedCount += byteSizedCount;
            totalLateralTrainingCount += lateralTrainingCount;
            totalQuestionContributionCount += questionContributionCount;
            totalHoursAllocated += reporteeHours;
            totalSessions += reporteeSessions;
        }
        
        // Create totals object
        ConsolidatedMonthlyEffortResponse.EffortTotals totals = 
            new ConsolidatedMonthlyEffortResponse.EffortTotals(
                (long) reportees.size(),
                totalSmeConnectCount,
                totalByteSizedCount,
                totalLateralTrainingCount,
                totalQuestionContributionCount,
                totalHoursAllocated,
                totalSessions
            );
        
        return new ConsolidatedMonthlyEffortResponse(
            supervisorId,
            supervisor.getName(),
            supervisor.getEmail(),
            monthYear,
            reporteeSummaries,
            totals
        );
    }
    
    /**
     * Get consolidated monthly effort details for all SMEs by lead (no access restrictions)
     */
    public ConsolidatedMonthlyEffortResponse getConsolidatedMonthlyEffortDetailsForLead(String monthYear) {
        // Get all SMEs in the system
        List<User> allSmes = userRepository.findByRole(UserRole.SME);
        
        if (allSmes.isEmpty()) {
            // Return empty response if no SMEs found
            return new ConsolidatedMonthlyEffortResponse(
                null,
                "Lead",
                "lead@edulearnorg.com",
                monthYear,
                new ArrayList<>(),
                new ConsolidatedMonthlyEffortResponse.EffortTotals(0L, 0L, 0L, 0L, 0L, 0.0, 0L)
            );
        }
        
        // Collect effort details for each SME
        List<ConsolidatedMonthlyEffortResponse.ReporteeEffortSummary> smeSummaries = new ArrayList<>();
        
        // Totals for consolidation
        long totalSmeConnectCount = 0;
        long totalByteSizedCount = 0;
        long totalLateralTrainingCount = 0;
        long totalQuestionContributionCount = 0;
        double totalHoursAllocated = 0.0;
        long totalSessions = 0;
        
        for (User sme : allSmes) {
            // Get activity groups for this SME and month
            List<SmeActivityGroup> smeActivityGroups = smeActivityGroupRepository
                .findBySmeUserIdAndMonthYear(sme.getId(), monthYear);
            
            // Calculate metrics for this SME
            Long smeConnectCount = getTotalSessionsByCategory(smeActivityGroups, ActivityCategory.CALENDAR_TRAINING);
            Long byteSizedCount = getTotalSessionsByCategory(smeActivityGroups, ActivityCategory.BYTE_SIZED);
            Long lateralTrainingCount = getTotalSessionsByCategory(smeActivityGroups, ActivityCategory.CALENDAR_TRAINING);
            Long questionContributionCount = 0L; // Placeholder for future implementation
            
            Double smeHours = smeActivityGroups.stream()
                .mapToDouble(SmeActivityGroup::getTotalHoursAllocated)
                .sum();
            
            Long smeSessions = smeActivityGroups.stream()
                .mapToLong(SmeActivityGroup::getTotalSessions)
                .sum();
            
            // Create SME summary
            ConsolidatedMonthlyEffortResponse.ReporteeEffortSummary summary = 
                new ConsolidatedMonthlyEffortResponse.ReporteeEffortSummary(
                    sme.getId(),
                    sme.getName(),
                    sme.getEmail(),
                    smeConnectCount,
                    byteSizedCount,
                    lateralTrainingCount,
                    questionContributionCount,
                    smeHours,
                    smeSessions
                );
            
            smeSummaries.add(summary);
            
            // Add to totals
            totalSmeConnectCount += smeConnectCount;
            totalByteSizedCount += byteSizedCount;
            totalLateralTrainingCount += lateralTrainingCount;
            totalQuestionContributionCount += questionContributionCount;
            totalHoursAllocated += smeHours;
            totalSessions += smeSessions;
        }
        
        // Create totals object
        ConsolidatedMonthlyEffortResponse.EffortTotals totals = 
            new ConsolidatedMonthlyEffortResponse.EffortTotals(
                (long) allSmes.size(),
                totalSmeConnectCount,
                totalByteSizedCount,
                totalLateralTrainingCount,
                totalQuestionContributionCount,
                totalHoursAllocated,
                totalSessions
            );
        
        return new ConsolidatedMonthlyEffortResponse(
            null, // Lead doesn't have a specific ID in this context
            "Lead User",
            "lead@edulearnorg.com",
            monthYear,
            smeSummaries,
            totals
        );
    }
    
    /**
     * Helper method to get total sessions by category
     */
    private Long getTotalSessionsByCategory(List<SmeActivityGroup> activityGroups, ActivityCategory category) {
        return activityGroups.stream()
            .filter(group -> group.getCategory() == category)
            .mapToLong(SmeActivityGroup::getTotalSessions)
            .sum();
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
