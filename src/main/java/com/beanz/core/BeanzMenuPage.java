package com.beanz.core;

import com.beanz.core.skills.SkillSnapshot;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class BeanzMenuPage extends BasicCustomUIPage {
    private static final String PAGE_LAYOUT = "Pages/BeanzMenu.ui";
    private final SkillSnapshot jumpSnapshot;

    public BeanzMenuPage(PlayerRef playerRef, SkillSnapshot jumpSnapshot) {
        super(playerRef, CustomPageLifetime.CanDismiss);
        this.jumpSnapshot = jumpSnapshot;
    }

    @Override
    public void build(UICommandBuilder commandBuilder) {
        commandBuilder.append(PAGE_LAYOUT);
        commandBuilder.set("#Title.Text", "BeanzSkillz");
        commandBuilder.set("#Body.Text", "Use /beanz to check your first live skill.");
        commandBuilder.set("#SkillNameValue.Text", jumpSnapshot.skillName());
        commandBuilder.set("#SkillXpValue.Text", Integer.toString(jumpSnapshot.xp()));
        commandBuilder.set("#SkillLevelValue.Text", Integer.toString(jumpSnapshot.level()));
        commandBuilder.set("#Footer.Text", "Jump to earn XP. Future levels can increase jump force.");
    }
}
