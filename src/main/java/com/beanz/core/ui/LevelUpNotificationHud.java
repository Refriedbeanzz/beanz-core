package com.beanz.core.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class LevelUpNotificationHud extends CustomUIHud {
    private static final String HUD_LAYOUT = "Hud/LevelUpNotification.ui";

    private String title = "";
    private String subtitle = "";
    private boolean visible;

    public LevelUpNotificationHud(PlayerRef playerRef) {
        super(playerRef);
    }

    public void showNotification(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.visible = true;
    }

    public void hideNotification() {
        this.visible = false;
    }

    public void pushUpdate() {
        update(true, new UICommandBuilder());
    }

    @Override
    protected void build(UICommandBuilder commandBuilder) {
        commandBuilder.append(HUD_LAYOUT);
        commandBuilder.set("#LevelToast.Visible", visible);
        commandBuilder.set("#LevelToastTitle.Text", title);
        commandBuilder.set("#LevelToastSubtitle.Text", subtitle);
    }
}
