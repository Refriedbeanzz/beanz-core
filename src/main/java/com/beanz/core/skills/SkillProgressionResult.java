package com.beanz.core.skills;

public record SkillProgressionResult(
    SkillType skillType,
    int previousXp,
    int newXp,
    int previousLevel,
    int newLevel,
    int awardedXp
) {
    public SkillSnapshot snapshot() {
        return SkillSnapshot.from(skillType, newXp, newLevel);
    }
}
