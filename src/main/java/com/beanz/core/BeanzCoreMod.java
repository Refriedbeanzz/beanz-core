package com.beanz.core;

import com.beanz.core.skills.JumpSkillSystem;
import com.beanz.core.skills.PlayerSkillsComponent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class BeanzCoreMod extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static BeanzCoreMod instance;

    public BeanzCoreMod(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static BeanzCoreMod getInstance() {
        return instance;
    }

    public PlayerSkillsComponent getOrCreateSkills(com.hypixel.hytale.server.core.universe.PlayerRef playerRef) {
        var holder = playerRef.getHolder();
        PlayerSkillsComponent skills = holder.getComponent(PlayerSkillsComponent.getComponentType());

        if (skills == null) {
            skills = new PlayerSkillsComponent();
            holder.addComponent(PlayerSkillsComponent.getComponentType(), skills);
        }

        return skills;
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up BeanzSkillz jump skill");
        PlayerSkillsComponent.setComponentType(
            getEntityStoreRegistry().registerComponent(
                PlayerSkillsComponent.class,
                "beanz:player_skills",
                PlayerSkillsComponent.CODEC
            )
        );
        getEntityStoreRegistry().registerSystem(new JumpSkillSystem());
        getEventRegistry().register(PlayerConnectEvent.class, this::onPlayerConnect);

        LOGGER.atInfo().log("Registering /beanz command");
        getCommandRegistry().registerCommand(new BeanzCommand());
    }

    private void onPlayerConnect(PlayerConnectEvent event) {
        PlayerSkillsComponent skills = getOrCreateSkills(event.getPlayerRef());

        LOGGER.atInfo().log(
            "Initialized skill data for %s (Jump level=%s, xp=%s)",
            event.getPlayerRef().getUsername(),
            skills.getLevel(com.beanz.core.skills.SkillType.JUMP),
            skills.getXp(com.beanz.core.skills.SkillType.JUMP)
        );
    }
}
