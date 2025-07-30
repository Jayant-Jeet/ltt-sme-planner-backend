package com.edulearnorg.ltt.smeplanner.enums;

public enum UserRole {
    SME("SME"),
    SUPERVISOR("Supervisor"),
    LEAD("Lead");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return this.name(); // Return enum name (uppercase) instead of displayName
    }
}
