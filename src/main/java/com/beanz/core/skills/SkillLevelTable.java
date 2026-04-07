package com.beanz.core.skills;

public final class SkillLevelTable {
    private static final int[] LEVEL_THRESHOLDS = {0, 10, 25, 45, 70, 100, 140, 185, 235, 290};

    private SkillLevelTable() {
    }

    public static int getLevelForXp(int xp) {
        int resolvedLevel = 1;

        for (int i = 0; i < LEVEL_THRESHOLDS.length; i++) {
            if (xp >= LEVEL_THRESHOLDS[i]) {
                resolvedLevel = i + 1;
            }
        }

        return resolvedLevel;
    }
}
