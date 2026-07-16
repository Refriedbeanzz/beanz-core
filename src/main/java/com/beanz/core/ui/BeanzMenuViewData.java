package com.beanz.core.ui;

import com.beanz.core.skills.LevelProgress;
import com.beanz.core.skills.SkillLevelTable;
import com.beanz.core.skills.SkillSnapshot;

public record BeanzMenuViewData(
    String title,
    String body,
    String footer,
    SkillOverviewEntryViewData jumpSkill,
    SkillOverviewEntryViewData runningSkill
) {
    private static final int PROGRESS_BAR_WIDTH = 388;

    public static BeanzMenuViewData from(SkillSnapshot jumpSnapshot, SkillSnapshot runningSnapshot) {
        LevelProgress jumpProgress = SkillLevelTable.getProgress(jumpSnapshot.xp());
        LevelProgress runningProgress = SkillLevelTable.getProgress(runningSnapshot.xp());

        return new BeanzMenuViewData(
            "BeanzSkillz",
            "Track your active skills and watch each level push your build a little further.",
            "Skills level up as you play. Each level unlocks better stats and new abilities.",
            buildEntry(jumpSnapshot, jumpProgress),
            buildEntry(runningSnapshot, runningProgress)
        );
    }

    private static SkillOverviewEntryViewData buildEntry(SkillSnapshot snapshot, LevelProgress progress) {
        String progressText = snapshot.level() >= 100
            ? "Max level reached"
            : String.format(
                "%d / %d XP to Level %d",
                progress.xpIntoLevel(),
                progress.xpNeededForNextLevel(),
                snapshot.level() + 1
            );

        return new SkillOverviewEntryViewData(
            snapshot.skillName(),
            "Lvl " + snapshot.level(),
            "Total XP: " + snapshot.xp(),
            progressText,
            (int) Math.round(PROGRESS_BAR_WIDTH * progress.progressFraction())
        );
    }
}
