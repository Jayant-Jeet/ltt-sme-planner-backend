package com.edulearnorg.ltt.smeplanner.enums;

/**
 * Enum representing different categories of SME activities
 */
public enum ActivityCategory {
    CALENDAR_TRAINING("Calendar Training"),
    BLENDED("Blended"),
    ADHOC_TRAINING("Adhoc Training"),
    BYTE_SIZED("Byte Sized"),
    CONTENT_DEVELOPMENT("Content Development"),
    EVALUATION("Evaluation"),
    SKILL_UPGRADE("Skill Upgrade"),
    MANAGEMENT("Management"),
    TIME_OFF("Time Off"),
    MISCELLANEOUS("Miscellaneous");
    
    private final String displayName;
    
    ActivityCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
