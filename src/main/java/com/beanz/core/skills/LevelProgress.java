package com.beanz.core.skills;

public record LevelProgress(
    int currentXp,
    int xpForCurrentLevel,
    int xpForNextLevel,
    int xpIntoLevel,
    int xpNeededForNextLevel,
    double progressFraction
) {
}
