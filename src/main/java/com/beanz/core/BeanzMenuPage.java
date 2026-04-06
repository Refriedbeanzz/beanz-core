package com.beanz.core;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class BeanzMenuPage extends BasicCustomUIPage {
    private static final String PAGE_LAYOUT = "UI/Custom/BeanzMenu.ui";

    public BeanzMenuPage(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss);
    }

    @Override
    public void build(UICommandBuilder commandBuilder) {
        commandBuilder.append(PAGE_LAYOUT);
        commandBuilder.set("#Title.Text", "Beanz Core");
        commandBuilder.set("#Body.Text", "The /beanz command is working.");
        commandBuilder.set("#Footer.Text", "This is your first in-game Beanz menu.");
    }
}
