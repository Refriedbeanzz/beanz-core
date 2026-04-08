package com.beanz.core.ui;

public record SkillOverviewEntryViewData(
    String skillName,
    String levelText,
    String xpSummaryText,
    String progressText,
    int progressWidth
) {
}
