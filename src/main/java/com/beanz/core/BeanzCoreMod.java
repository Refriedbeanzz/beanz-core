package com.beanz.core;

import com.beanz.core.skills.JumpSkillSystem;
import com.beanz.core.skills.JumpFallDamageSystem;
import com.beanz.core.skills.PlayerSkillsComponent;
import com.beanz.core.skills.SkillService;
import com.beanz.core.skills.SkillSnapshot;
import com.beanz.core.skills.SkillType;
import com.beanz.core.ui.LevelUpNotificationService;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BeanzCoreMod extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String JUMP_RESET_MARKER = "beanzskillz_jump_reset_v1.marker";
    private static BeanzCoreMod instance;
    private final SkillService skillService = new SkillService();
    private final LevelUpNotificationService levelUpNotificationService = new LevelUpNotificationService();

    public BeanzCoreMod(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static BeanzCoreMod getInstance() {
        return instance;
    }

    public SkillService getSkillService() {
        return skillService;
    }

    public LevelUpNotificationService getLevelUpNotificationService() {
        return levelUpNotificationService;
    }

    public PlayerSkillsComponent getOrCreateSkills(PlayerRef playerRef, Holder<EntityStore> holder) {
        return skillService.getOrCreateSkills(playerRef, holder);
    }

    public PlayerSkillsComponent getOrCreateSkills(PlayerRef playerRef, Store<EntityStore> store, Ref<EntityStore> ref) {
        return skillService.getOrCreateSkills(playerRef, store, ref);
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
        getEntityStoreRegistry().registerSystem(new JumpFallDamageSystem());
        getEventRegistry().register(PlayerConnectEvent.class, this::onPlayerConnect);

        LOGGER.atInfo().log("Registering /beanz command");
        getCommandRegistry().registerCommand(new BeanzCommand());
    }

    private void onPlayerConnect(PlayerConnectEvent event) {
        PlayerSkillsComponent skills = getOrCreateSkills(event.getPlayerRef(), event.getHolder());
        runOneTimeJumpReset(skills, event.getPlayerRef());
        SkillSnapshot jumpSnapshot = skillService.getSnapshot(skills, SkillType.JUMP);

        LOGGER.atInfo().log(
            "Initialized skill data for %s (Jump level=%s, xp=%s)",
            event.getPlayerRef().getUsername(),
            jumpSnapshot.level(),
            jumpSnapshot.xp()
        );
    }

    private void runOneTimeJumpReset(PlayerSkillsComponent skills, PlayerRef playerRef) {
        Path markerPath = Paths.get(System.getenv("APPDATA"), "Hytale", "UserData", JUMP_RESET_MARKER);

        if (Files.exists(markerPath)) {
            return;
        }

        skills.setJumpXp(0);
        skills.setJumpLevel(1);

        try {
            Files.writeString(markerPath, "Jump skill reset applied for " + (playerRef != null ? playerRef.getUsername() : "unknown-player"));
            LOGGER.atInfo().log(
                "Applied one-time Jump reset for %s (xp=0, level=1). Marker created at %s",
                playerRef != null ? playerRef.getUsername() : "unknown-player",
                markerPath
            );
        } catch (IOException exception) {
            LOGGER.atWarning().withCause(exception).log(
                "Applied Jump reset for %s but failed to write reset marker at %s",
                playerRef != null ? playerRef.getUsername() : "unknown-player",
                markerPath
            );
        }
    }
}
