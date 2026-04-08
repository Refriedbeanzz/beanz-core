package com.beanz.core.skills;

public record SkillSnapshot(SkillType skillType, String skillName, int xp, int level) {
    public static SkillSnapshot from(SkillType skillType, int xp, int level) {
        return new SkillSnapshot(skillType, SkillRegistry.get(skillType).displayName(), xp, level);
    }
}
