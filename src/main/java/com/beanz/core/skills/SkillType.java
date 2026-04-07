package com.beanz.core.skills;

public enum SkillType {
    JUMP("Jump");

    private final String displayName;

    SkillType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
