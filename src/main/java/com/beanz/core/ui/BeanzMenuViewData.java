package com.beanz.core.ui;

import com.beanz.core.skills.LevelProgress;
import com.beanz.core.skills.SkillSnapshot;
import com.beanz.core.skills.SkillType;

public record BeanzMenuViewData(
    String title,
    String body,
    String footer,
    SkillOverviewEntryViewData primarySkill
) {
    private static final int PROGRESS_BAR_WIDTH = 388;

    public static BeanzMenuViewData from(SkillSnapshot jumpSnapshot, LevelProgress progress) {
        String progressText = jumpSnapshot.level() >= 100
            ? "Max level reached"
            : String.format(
                "%d / %d XP to Level %d",
                progress.xpIntoLevel(),
                progress.xpNeededForNextLevel(),
                jumpSnapshot.level() + 1
            );

        return new BeanzMenuViewData(
            "BeanzSkillz",
            "Track your active skills and watch each level push your build a little further.",
            "Jump XP increases from every jump. Higher Jump levels improve jump force and reduce fall damage.",
            new SkillOverviewEntryViewData(
                jumpSnapshot.skillName(),
                "Lvl " + jumpSnapshot.level(),
                "Total XP: " + jumpSnapshot.xp(),
                progressText,
                (int) Math.round(PROGRESS_BAR_WIDTH * progress.progressFraction())
            )
        );
    }
}
