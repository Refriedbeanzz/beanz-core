package com.beanz.core;

import com.beanz.core.ui.BeanzMenuViewData;
import com.beanz.core.ui.SkillOverviewEntryViewData;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class BeanzMenuPage extends BasicCustomUIPage {
    private static final String PAGE_LAYOUT = "Pages/BeanzMenu.ui";
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final BeanzMenuViewData viewData;

    public BeanzMenuPage(PlayerRef playerRef, BeanzMenuViewData viewData) {
        super(playerRef, CustomPageLifetime.CanDismiss);
        this.viewData = viewData;
    }

    @Override
    public void build(UICommandBuilder commandBuilder) {
        commandBuilder.append(PAGE_LAYOUT);
        commandBuilder.set("#Title.Text", viewData.title());
        commandBuilder.set("#Body.Text", viewData.body());
        commandBuilder.set("#Footer.Text", viewData.footer());

        setSkillEntry(commandBuilder, "Jump", viewData.jumpSkill());
        setSkillEntry(commandBuilder, "Running", viewData.runningSkill());

        LOGGER.atInfo().log(
            "Built BeanzMenu: jump=Lvl %s, running=Lvl %s",
            viewData.jumpSkill().levelText(),
            viewData.runningSkill().levelText()
        );
    }

    private void setSkillEntry(UICommandBuilder commandBuilder, String prefix, SkillOverviewEntryViewData entry) {
        commandBuilder.set("#" + prefix + "EntryName.Text", entry.skillName());
        commandBuilder.set("#" + prefix + "EntryLevel.Text", entry.levelText());
        commandBuilder.set("#" + prefix + "EntryXp.Text", entry.xpSummaryText());
        commandBuilder.set("#" + prefix + "EntryProgressText.Text", entry.progressText());
        commandBuilder.setObject("#" + prefix + "EntryFill.Anchor", progressFillAnchor(entry.progressWidth()));
    }

    private com.hypixel.hytale.server.core.ui.Anchor progressFillAnchor(int width) {
        com.hypixel.hytale.server.core.ui.Anchor anchor = new com.hypixel.hytale.server.core.ui.Anchor();
        anchor.setLeft(com.hypixel.hytale.server.core.ui.Value.of(0));
        anchor.setTop(com.hypixel.hytale.server.core.ui.Value.of(0));
        anchor.setHeight(com.hypixel.hytale.server.core.ui.Value.of(12));
        anchor.setWidth(com.hypixel.hytale.server.core.ui.Value.of(width));
        return anchor;
    }
}
