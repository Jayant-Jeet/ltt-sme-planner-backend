package com.edulearnorg.ltt.smeplanner.dto;

import java.util.List;

/**
 * Response DTO for consolidated monthly effort details of all reportees
 */
public class ConsolidatedMonthlyEffortResponse {
    
    private Long supervisorId;
    private String supervisorName;
    private String supervisorEmail;
    private String monthYear;
    private List<ReporteeEffortSummary> reportees;
    private EffortTotals totals;
    
    public ConsolidatedMonthlyEffortResponse() {}
    
    public ConsolidatedMonthlyEffortResponse(Long supervisorId, String supervisorName, String supervisorEmail, 
                                           String monthYear, List<ReporteeEffortSummary> reportees, EffortTotals totals) {
        this.supervisorId = supervisorId;
        this.supervisorName = supervisorName;
        this.supervisorEmail = supervisorEmail;
        this.monthYear = monthYear;
        this.reportees = reportees;
        this.totals = totals;
    }
    
    // Getters and Setters
    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }
    
    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }
    
    public String getSupervisorEmail() { return supervisorEmail; }
    public void setSupervisorEmail(String supervisorEmail) { this.supervisorEmail = supervisorEmail; }
    
    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }
    
    public List<ReporteeEffortSummary> getReportees() { return reportees; }
    public void setReportees(List<ReporteeEffortSummary> reportees) { this.reportees = reportees; }
    
    public EffortTotals getTotals() { return totals; }
    public void setTotals(EffortTotals totals) { this.totals = totals; }
    
    /**
     * Inner class for individual reportee effort summary
     */
    public static class ReporteeEffortSummary {
        private Long smeId;
        private String smeName;
        private String smeEmail;
        private Long smeConnectCount;
        private Long byteSizedCount;
        private Long lateralTrainingCount;
        private Long questionContributionCount;
        private Double totalHoursAllocated;
        private Long totalSessions;
        
        public ReporteeEffortSummary() {}
        
        public ReporteeEffortSummary(Long smeId, String smeName, String smeEmail,
                                   Long smeConnectCount, Long byteSizedCount, Long lateralTrainingCount,
                                   Long questionContributionCount, Double totalHoursAllocated, Long totalSessions) {
            this.smeId = smeId;
            this.smeName = smeName;
            this.smeEmail = smeEmail;
            this.smeConnectCount = smeConnectCount != null ? smeConnectCount : 0L;
            this.byteSizedCount = byteSizedCount != null ? byteSizedCount : 0L;
            this.lateralTrainingCount = lateralTrainingCount != null ? lateralTrainingCount : 0L;
            this.questionContributionCount = questionContributionCount != null ? questionContributionCount : 0L;
            this.totalHoursAllocated = totalHoursAllocated != null ? totalHoursAllocated : 0.0;
            this.totalSessions = totalSessions != null ? totalSessions : 0L;
        }
        
        // Getters and Setters
        public Long getSmeId() { return smeId; }
        public void setSmeId(Long smeId) { this.smeId = smeId; }
        
        public String getSmeName() { return smeName; }
        public void setSmeName(String smeName) { this.smeName = smeName; }
        
        public String getSmeEmail() { return smeEmail; }
        public void setSmeEmail(String smeEmail) { this.smeEmail = smeEmail; }
        
        public Long getSmeConnectCount() { return smeConnectCount; }
        public void setSmeConnectCount(Long smeConnectCount) { this.smeConnectCount = smeConnectCount; }
        
        public Long getByteSizedCount() { return byteSizedCount; }
        public void setByteSizedCount(Long byteSizedCount) { this.byteSizedCount = byteSizedCount; }
        
        public Long getLateralTrainingCount() { return lateralTrainingCount; }
        public void setLateralTrainingCount(Long lateralTrainingCount) { this.lateralTrainingCount = lateralTrainingCount; }
        
        public Long getQuestionContributionCount() { return questionContributionCount; }
        public void setQuestionContributionCount(Long questionContributionCount) { this.questionContributionCount = questionContributionCount; }
        
        public Double getTotalHoursAllocated() { return totalHoursAllocated; }
        public void setTotalHoursAllocated(Double totalHoursAllocated) { this.totalHoursAllocated = totalHoursAllocated; }
        
        public Long getTotalSessions() { return totalSessions; }
        public void setTotalSessions(Long totalSessions) { this.totalSessions = totalSessions; }
    }
    
    /**
     * Inner class for consolidated effort totals
     */
    public static class EffortTotals {
        private Long totalReportees;
        private Long totalSmeConnectCount;
        private Long totalByteSizedCount;
        private Long totalLateralTrainingCount;
        private Long totalQuestionContributionCount;
        private Double totalHoursAllocated;
        private Long totalSessions;
        
        public EffortTotals() {}
        
        public EffortTotals(Long totalReportees, Long totalSmeConnectCount, Long totalByteSizedCount,
                          Long totalLateralTrainingCount, Long totalQuestionContributionCount,
                          Double totalHoursAllocated, Long totalSessions) {
            this.totalReportees = totalReportees != null ? totalReportees : 0L;
            this.totalSmeConnectCount = totalSmeConnectCount != null ? totalSmeConnectCount : 0L;
            this.totalByteSizedCount = totalByteSizedCount != null ? totalByteSizedCount : 0L;
            this.totalLateralTrainingCount = totalLateralTrainingCount != null ? totalLateralTrainingCount : 0L;
            this.totalQuestionContributionCount = totalQuestionContributionCount != null ? totalQuestionContributionCount : 0L;
            this.totalHoursAllocated = totalHoursAllocated != null ? totalHoursAllocated : 0.0;
            this.totalSessions = totalSessions != null ? totalSessions : 0L;
        }
        
        // Getters and Setters
        public Long getTotalReportees() { return totalReportees; }
        public void setTotalReportees(Long totalReportees) { this.totalReportees = totalReportees; }
        
        public Long getTotalSmeConnectCount() { return totalSmeConnectCount; }
        public void setTotalSmeConnectCount(Long totalSmeConnectCount) { this.totalSmeConnectCount = totalSmeConnectCount; }
        
        public Long getTotalByteSizedCount() { return totalByteSizedCount; }
        public void setTotalByteSizedCount(Long totalByteSizedCount) { this.totalByteSizedCount = totalByteSizedCount; }
        
        public Long getTotalLateralTrainingCount() { return totalLateralTrainingCount; }
        public void setTotalLateralTrainingCount(Long totalLateralTrainingCount) { this.totalLateralTrainingCount = totalLateralTrainingCount; }
        
        public Long getTotalQuestionContributionCount() { return totalQuestionContributionCount; }
        public void setTotalQuestionContributionCount(Long totalQuestionContributionCount) { this.totalQuestionContributionCount = totalQuestionContributionCount; }
        
        public Double getTotalHoursAllocated() { return totalHoursAllocated; }
        public void setTotalHoursAllocated(Double totalHoursAllocated) { this.totalHoursAllocated = totalHoursAllocated; }
        
        public Long getTotalSessions() { return totalSessions; }
        public void setTotalSessions(Long totalSessions) { this.totalSessions = totalSessions; }
    }
}
