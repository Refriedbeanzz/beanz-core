package com.beanz.core.abilities;

import com.beanz.core.skills.SkillType;

public enum AbilityType {
    SKY_LEAP(SkillType.JUMP, 60);

    private final SkillType requiredSkill;
    private final int requiredLevel;

    AbilityType(SkillType requiredSkill, int requiredLevel) {
        this.requiredSkill = requiredSkill;
        this.requiredLevel = requiredLevel;
    }

    public SkillType getRequiredSkill() {
        return requiredSkill;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public boolean isUnlockedAtLevel(int level) {
        return level >= requiredLevel;
    }
}
