package com.beanz.core;

import com.beanz.core.ui.BeanzMenuViewData;
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
        commandBuilder.set("#SkillEntryName.Text", viewData.primarySkill().skillName());
        commandBuilder.set("#SkillEntryLevel.Text", viewData.primarySkill().levelText());
        commandBuilder.set("#SkillEntryXp.Text", viewData.primarySkill().xpSummaryText());
        commandBuilder.set("#SkillEntryProgressText.Text", viewData.primarySkill().progressText());
        commandBuilder.setObject("#SkillEntryFill.Anchor", progressFillAnchor(viewData.primarySkill().progressWidth()));
        commandBuilder.set("#Footer.Text", viewData.footer());

        LOGGER.atInfo().log(
            "Assigned BeanzMenu overview values: skill=%s, level=%s, xp=%s, progressWidth=%s",
            viewData.primarySkill().skillName(),
            viewData.primarySkill().levelText(),
            viewData.primarySkill().xpSummaryText(),
            viewData.primarySkill().progressWidth()
        );
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
