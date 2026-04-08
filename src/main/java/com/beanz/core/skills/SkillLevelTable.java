package com.beanz.core.skills;

public final class SkillLevelTable {
    private static final int MAX_LEVEL = 100;
    private static final int[] LEVEL_THRESHOLDS = buildThresholds();

    private SkillLevelTable() {
    }

    public static int getLevelForXp(int xp) {
        int sanitizedXp = Math.max(0, xp);
        int resolvedLevel = 1;

        for (int i = 0; i < LEVEL_THRESHOLDS.length; i++) {
            if (sanitizedXp >= LEVEL_THRESHOLDS[i]) {
                resolvedLevel = i + 1;
            }
        }

        return resolvedLevel;
    }

    public static int getXpRequiredForLevel(int level) {
        int clampedLevel = Math.max(1, Math.min(LEVEL_THRESHOLDS.length, level));
        return LEVEL_THRESHOLDS[clampedLevel - 1];
    }

    public static int getXpToNextLevel(int currentLevel) {
        int clampedLevel = Math.max(1, Math.min(MAX_LEVEL, currentLevel));
        if (clampedLevel >= MAX_LEVEL) {
            return 0;
        }

        return (int) Math.round(20 + (0.9 * clampedLevel * clampedLevel));
    }

    public static LevelProgress getProgress(int xp) {
        int sanitizedXp = Math.max(0, xp);
        int level = getLevelForXp(sanitizedXp);
        int xpForCurrentLevel = getXpRequiredForLevel(level);
        int xpForNextLevel = level >= LEVEL_THRESHOLDS.length ? xpForCurrentLevel : getXpRequiredForLevel(level + 1);
        int xpIntoLevel = Math.max(0, sanitizedXp - xpForCurrentLevel);
        int xpNeededForNextLevel = Math.max(0, xpForNextLevel - xpForCurrentLevel);
        double progressFraction = xpNeededForNextLevel == 0
            ? 1.0
            : Math.min(1.0, Math.max(0.0, xpIntoLevel / (double) xpNeededForNextLevel));

        return new LevelProgress(
            sanitizedXp,
            xpForCurrentLevel,
            xpForNextLevel,
            xpIntoLevel,
            xpNeededForNextLevel,
            progressFraction
        );
    }

    private static int[] buildThresholds() {
        int[] thresholds = new int[MAX_LEVEL];
        thresholds[0] = 0;

        int runningXp = 0;
        for (int level = 2; level <= MAX_LEVEL; level++) {
            runningXp += getXpToNextLevel(level - 1);
            thresholds[level - 1] = runningXp;
        }

        return thresholds;
    }
}
