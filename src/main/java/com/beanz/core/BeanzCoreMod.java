package com.beanz.core;

import com.beanz.core.abilities.AbilityManager;
import com.beanz.core.abilities.AbilityInteractionInjector;
import com.beanz.core.abilities.PlayerAbilityData;
import com.beanz.core.abilities.TestAbility3Interaction;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.beanz.core.skills.JumpSkillSystem;
import com.beanz.core.skills.JumpFallDamageSystem;
import com.beanz.core.skills.JumpAbilityStateComponent;
import com.beanz.core.skills.PlayerSkillsComponent;
import com.beanz.core.skills.SkillLevelTable;
import com.beanz.core.skills.SkillService;
import com.beanz.core.skills.SkillSnapshot;
import com.beanz.core.skills.SkillType;
import com.beanz.core.ui.LevelUpNotificationService;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class BeanzCoreMod extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String JUMP_RESET_MARKER = "beanzskillz_jump_reset_v2.marker";
    private static final String JUMP_TEST_BOOST_MARKER = "beanzskillz_jump_test_level_100_v1.marker";
    public static final String TEST_ABILITY3_ROOT_INTERACTION_ID = "Root_BeanzAbility3Test";
    private static BeanzCoreMod instance;
    private final SkillService skillService = new SkillService();
    private final AbilityManager abilityManager = new AbilityManager();
    private final LevelUpNotificationService levelUpNotificationService = new LevelUpNotificationService();
    private final AbilityInteractionInjector abilityInteractionInjector = new AbilityInteractionInjector();

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

    public AbilityManager getAbilityManager() {
        return abilityManager;
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
        LOGGER.atInfo().log("[BeanzCore][TestAbility] registering simple Ability3 test");
        PlayerSkillsComponent.setComponentType(
            getEntityStoreRegistry().registerComponent(
                PlayerSkillsComponent.class,
                "beanz:player_skills",
                PlayerSkillsComponent.CODEC
            )
        );
        JumpAbilityStateComponent.setComponentType(
            getEntityStoreRegistry().registerComponent(
                JumpAbilityStateComponent.class,
                "beanz:jump_ability_state",
                JumpAbilityStateComponent.CODEC
            )
        );
        PlayerAbilityData.setComponentType(
            getEntityStoreRegistry().registerComponent(
                PlayerAbilityData.class,
                "beanz:player_abilities",
                PlayerAbilityData.CODEC
            )
        );
        HytaleServer.get().getEventBus().register(
            (Class) LoadedAssetsEvent.class,
            (Consumer) (event -> abilityInteractionInjector.onItemsLoaded((LoadedAssetsEvent<?, ?, ?>) event))
        );
        LOGGER.atInfo().log("[BeanzCore][InputDebug] AbilityInteractionInjector registered");
        getCodecRegistry(Interaction.CODEC).register("Beanz_Ability3_Test", TestAbility3Interaction.class, TestAbility3Interaction.CODEC);
        getEntityStoreRegistry().registerSystem(new JumpSkillSystem());
        getEntityStoreRegistry().registerSystem(new JumpFallDamageSystem());
        getEventRegistry().register(PlayerConnectEvent.class, this::onPlayerConnect);

        LOGGER.atInfo().log("Registering /beanz command");
        getCommandRegistry().registerCommand(new BeanzCommand());
    }

    private void onPlayerConnect(PlayerConnectEvent event) {
        PlayerSkillsComponent skills = getOrCreateSkills(event.getPlayerRef(), event.getHolder());
        PlayerAbilityData abilityData = abilityManager.getOrCreate(event.getPlayerRef(), event.getHolder());
        JumpAbilityStateComponent jumpState = event.getHolder().getComponent(JumpAbilityStateComponent.getComponentType());
        if (jumpState == null) {
            jumpState = new JumpAbilityStateComponent();
            event.getHolder().addComponent(JumpAbilityStateComponent.getComponentType(), jumpState);
        }
        Interactions interactions = event.getHolder().getComponent(Interactions.getComponentType());
        if (interactions == null) {
            interactions = new Interactions();
            event.getHolder().addComponent(Interactions.getComponentType(), interactions);
        }
        interactions.setInteractionId(InteractionType.Ability3, TEST_ABILITY3_ROOT_INTERACTION_ID);
        jumpState.resetAirAbilities();
        jumpState.setWasGrounded(true);
        runOneTimeJumpReset(skills, event.getPlayerRef());
        runOneTimeJumpTestBoost(skills, event.getPlayerRef());
        syncAbilityUnlocks(event.getPlayerRef(), skills, abilityData);
        SkillSnapshot jumpSnapshot = skillService.getSnapshot(skills, SkillType.JUMP);

        LOGGER.atInfo().log(
            "Initialized skill data for %s (Jump level=%s, xp=%s)",
            event.getPlayerRef().getUsername(),
            jumpSnapshot.level(),
            jumpSnapshot.xp()
        );
    }

    public void syncAbilityUnlocks(PlayerRef playerRef, PlayerSkillsComponent skills, PlayerAbilityData abilityData) {
        if (skills.getLevel(SkillType.JUMP) >= 60) {
            abilityManager.unlockAbility(playerRef, abilityData, com.beanz.core.abilities.AbilityType.SKY_LEAP);
        }
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

    private void runOneTimeJumpTestBoost(PlayerSkillsComponent skills, PlayerRef playerRef) {
        Path markerPath = Paths.get(System.getenv("APPDATA"), "Hytale", "UserData", JUMP_TEST_BOOST_MARKER);

        if (Files.exists(markerPath)) {
            return;
        }

        int level100Xp = SkillLevelTable.getXpRequiredForLevel(100);
        skills.setJumpXp(level100Xp);
        skills.setJumpLevel(100);

        try {
            Files.writeString(markerPath, "Jump test boost to level 100 applied for " + (playerRef != null ? playerRef.getUsername() : "unknown-player"));
            LOGGER.atInfo().log(
                "Applied one-time Jump test boost for %s (xp=%s, level=100). Marker created at %s",
                playerRef != null ? playerRef.getUsername() : "unknown-player",
                level100Xp,
                markerPath
            );
        } catch (IOException exception) {
            LOGGER.atWarning().withCause(exception).log(
                "Applied Jump test boost for %s but failed to write marker at %s",
                playerRef != null ? playerRef.getUsername() : "unknown-player",
                markerPath
            );
        }
    }
}
