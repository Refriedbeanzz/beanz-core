package com.beanz.core.skills;

public final class SkillLevelTable {
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
        int[] thresholds = new int[100];

        addCurveRange(thresholds, 1, 25, 0, 500, 1.25);
        addCurveRange(thresholds, 25, 50, 500, 1500, 1.40);
        addCurveRange(thresholds, 50, 75, 1500, 4500, 1.60);
        addCurveRange(thresholds, 75, 90, 4500, 12000, 1.85);
        addCurveRange(thresholds, 90, 100, 12000, 30000, 2.20);

        return thresholds;
    }

    private static void addCurveRange(
        int[] thresholds,
        int startLevel,
        int endLevel,
        int startXp,
        int endXp,
        double exponent
    ) {
        int span = endLevel - startLevel;

        for (int level = startLevel; level <= endLevel; level++) {
            double progress = span == 0 ? 0.0 : (level - startLevel) / (double) span;
            double curvedProgress = Math.pow(progress, exponent);
            thresholds[level - 1] = (int) Math.round(startXp + ((endXp - startXp) * curvedProgress));
        }
    }
}
