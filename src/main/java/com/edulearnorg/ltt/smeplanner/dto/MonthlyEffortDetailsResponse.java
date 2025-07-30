package com.edulearnorg.ltt.smeplanner.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Monthly effort details for an SME")
public class MonthlyEffortDetailsResponse {
    
    @Schema(description = "SME ID", example = "1")
    private Long smeId;
    
    @Schema(description = "SME name", example = "John Doe")
    private String smeName;
    
    @Schema(description = "SME email", example = "john.doe@edulearnorg.com")
    private String smeEmail;
    
    @Schema(description = "Month-Year", example = "2025-07")
    private String monthYear;
    
    @Schema(description = "SME Connect count", example = "5")
    private Long smeConnectCount;
    
    @Schema(description = "Byte Sized count", example = "3")
    private Long byteSizedCount;
    
    @Schema(description = "Lateral training count", example = "2")
    private Long lateralTrainingCount;
    
    @Schema(description = "Question contribution count", example = "1")
    private Long questionContributionCount;
    
    @Schema(description = "Total hours allocated", example = "40.5")
    private Double totalHoursAllocated;
    
    @Schema(description = "Total sessions conducted", example = "11")
    private Long totalSessions;
    
    // Constructors
    public MonthlyEffortDetailsResponse() {}
    
    public MonthlyEffortDetailsResponse(Long smeId, String smeName, String smeEmail, String monthYear,
                                      Long smeConnectCount, Long byteSizedCount, Long lateralTrainingCount,
                                      Long questionContributionCount, Double totalHoursAllocated, Long totalSessions) {
        this.smeId = smeId;
        this.smeName = smeName;
        this.smeEmail = smeEmail;
        this.monthYear = monthYear;
        this.smeConnectCount = smeConnectCount;
        this.byteSizedCount = byteSizedCount;
        this.lateralTrainingCount = lateralTrainingCount;
        this.questionContributionCount = questionContributionCount;
        this.totalHoursAllocated = totalHoursAllocated;
        this.totalSessions = totalSessions;
    }
    
    // Getters and Setters
    public Long getSmeId() {
        return smeId;
    }
    
    public void setSmeId(Long smeId) {
        this.smeId = smeId;
    }
    
    public String getSmeName() {
        return smeName;
    }
    
    public void setSmeName(String smeName) {
        this.smeName = smeName;
    }
    
    public String getSmeEmail() {
        return smeEmail;
    }
    
    public void setSmeEmail(String smeEmail) {
        this.smeEmail = smeEmail;
    }
    
    public String getMonthYear() {
        return monthYear;
    }
    
    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }
    
    public Long getSmeConnectCount() {
        return smeConnectCount;
    }
    
    public void setSmeConnectCount(Long smeConnectCount) {
        this.smeConnectCount = smeConnectCount;
    }
    
    public Long getByteSizedCount() {
        return byteSizedCount;
    }
    
    public void setByteSizedCount(Long byteSizedCount) {
        this.byteSizedCount = byteSizedCount;
    }
    
    public Long getLateralTrainingCount() {
        return lateralTrainingCount;
    }
    
    public void setLateralTrainingCount(Long lateralTrainingCount) {
        this.lateralTrainingCount = lateralTrainingCount;
    }
    
    public Long getQuestionContributionCount() {
        return questionContributionCount;
    }
    
    public void setQuestionContributionCount(Long questionContributionCount) {
        this.questionContributionCount = questionContributionCount;
    }
    
    public Double getTotalHoursAllocated() {
        return totalHoursAllocated;
    }
    
    public void setTotalHoursAllocated(Double totalHoursAllocated) {
        this.totalHoursAllocated = totalHoursAllocated;
    }
    
    public Long getTotalSessions() {
        return totalSessions;
    }
    
    public void setTotalSessions(Long totalSessions) {
        this.totalSessions = totalSessions;
    }
}
