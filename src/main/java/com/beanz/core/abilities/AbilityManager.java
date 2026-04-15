package com.beanz.core.abilities;

import com.beanz.core.skills.JumpAbilityStateComponent;
import com.beanz.core.skills.PlayerSkillsComponent;
import com.beanz.core.skills.SkillRewardService;
import com.beanz.core.skills.SkillType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;

public class AbilityManager {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String STAMINA_STAT_ID = "stamina";
    private static final double EPSILON = 0.0001;
    private static final boolean SKY_LEAP_TEMPORARILY_DISABLED = false;

    public PlayerAbilityData getOrCreate(PlayerRef playerRef, Holder<EntityStore> holder) {
        if (holder == null) {
            return new PlayerAbilityData();
        }

        PlayerAbilityData abilityData = holder.getComponent(PlayerAbilityData.getComponentType());
        if (abilityData == null) {
            abilityData = new PlayerAbilityData();
            holder.addComponent(PlayerAbilityData.getComponentType(), abilityData);
        }
        return abilityData;
    }

    public PlayerAbilityData getOrCreate(PlayerRef playerRef, Store<EntityStore> store, Ref<EntityStore> ref) {
        if (store == null || ref == null) {
            LOGGER.atWarning().log(
                "[BeanzCore][Ability] Cannot resolve ability data for %s because store or ref is null.",
                playerRef != null ? playerRef.getUsername() : "unknown-player"
            );
            return new PlayerAbilityData();
        }

        PlayerAbilityData abilityData = store.getComponent(ref, PlayerAbilityData.getComponentType());
        if (abilityData == null) {
            abilityData = new PlayerAbilityData();
            store.addComponent(ref, PlayerAbilityData.getComponentType(), abilityData);
        }
        return abilityData;
    }

    public void unlockAbility(PlayerRef playerRef, PlayerAbilityData abilityData, AbilityType type) {
        if (abilityData == null || abilityData.isUnlocked(type)) {
            return;
        }

        abilityData.unlock(type);
        LOGGER.atInfo().log(
            "[BeanzCore][Ability] Unlocked %s for %s",
            type,
            playerRef != null ? playerRef.getUsername() : "unknown-player"
        );
    }

    public boolean isUnlocked(PlayerAbilityData abilityData, AbilityType type) {
        return abilityData != null && abilityData.isUnlocked(type);
    }

    public boolean canUse(PlayerAbilityData abilityData, AbilityType type, long now) {
        return abilityData != null && abilityData.canUse(type, now);
    }

    public boolean isSkyLeapExecutionEnabled() {
        return !SKY_LEAP_TEMPORARILY_DISABLED;
    }

    public void markUsed(PlayerAbilityData abilityData, AbilityType type, long now) {
        if (abilityData != null) {
            abilityData.markUsed(type, now);
        }
    }

    public boolean useSkyLeap(PlayerRef playerRef) {
        if (!isSkyLeapExecutionEnabled()) {
            LOGGER.atInfo().log("[BeanzCore][Ability] SKY_LEAP blocked: temporarily disabled");
            return false;
        }

        if (playerRef == null) {
            LOGGER.atWarning().log("[BeanzCore][Ability] SKY_LEAP blocked: player ref was null");
            return false;
        }

        Holder<EntityStore> holder = playerRef.getHolder();
        if (holder == null) {
            LOGGER.atInfo().log(
                "[BeanzCore][AbilityDebug] SKY_LEAP holder lookup: player=%s, entityRef=%s, holder=%s, lookupPath=%s",
                playerRef.getUsername(),
                "n/a",
                "null",
                "playerRef.getHolder()"
            );
            LOGGER.atWarning().log("[BeanzCore][Ability] SKY_LEAP blocked: holder missing for %s", playerRef.getUsername());
            return false;
        }

        Player player = holder.getComponent(Player.getComponentType());
        PlayerSkillsComponent skills = getOrCreateSkills(playerRef, holder);
        PlayerAbilityData abilityData = getOrCreate(playerRef, holder);
        JumpAbilityStateComponent jumpState = getOrCreateJumpState(holder);
        MovementStatesComponent movementStates = holder.getComponent(MovementStatesComponent.getComponentType());
        MovementManager movementManager = holder.getComponent(MovementManager.getComponentType());
        EntityStatMap statMap = holder.getComponent(EntityStatMap.getComponentType());
        Velocity velocity = holder.getComponent(Velocity.getComponentType());

        return useSkyLeap(
            playerRef,
            player,
            null,
            skills,
            abilityData,
            jumpState,
            movementStates,
            movementManager,
            statMap,
            velocity
        );
    }

    public boolean useSkyLeap(
        PlayerRef playerRef,
        Player player,
        Ref<EntityStore> ref,
        PlayerSkillsComponent skills,
        PlayerAbilityData abilityData,
        JumpAbilityStateComponent jumpState,
        MovementStatesComponent movementStates,
        MovementManager movementManager,
        EntityStatMap statMap,
        Velocity velocity
    ) {
        if (!isSkyLeapExecutionEnabled()) {
            LOGGER.atInfo().log("[BeanzCore][Ability] SKY_LEAP blocked: temporarily disabled");
            return false;
        }

        if (playerRef == null) {
            LOGGER.atWarning().log("[BeanzCore][Ability] SKY_LEAP blocked: player ref was null");
            return false;
        }

        LOGGER.atInfo().log("[BeanzCore][Ability] Ability3 pressed for %s", usernameOf(playerRef, player));

        if (skills == null || abilityData == null || jumpState == null || movementStates == null || movementManager == null || statMap == null || velocity == null) {
            LOGGER.atWarning().log("[BeanzCore][Ability] SKY_LEAP blocked: missing runtime components for %s", usernameOf(playerRef, player));
            return false;
        }

        SkillRewardService rewardService = com.beanz.core.BeanzCoreMod.getInstance().getSkillService().getRewardService();
        long now = System.currentTimeMillis();

        if (!isUnlocked(abilityData, AbilityType.SKY_LEAP)) {
            LOGGER.atInfo().log("[BeanzCore][Ability] SKY_LEAP blocked: locked for %s", usernameOf(playerRef, player));
            return false;
        }

        boolean airborne = !movementStates.getMovementStates().onGround && jumpState.hasLeftGroundSinceInitialJump();
        if (!airborne) {
            LOGGER.atInfo().log("[BeanzCore][Ability] SKY_LEAP blocked: not airborne for %s", usernameOf(playerRef, player));
            return false;
        }

        if (jumpState.hasUsedSkyLeapThisAirtime()) {
            LOGGER.atInfo().log("[BeanzCore][Ability] SKY_LEAP blocked: already used this airtime for %s", usernameOf(playerRef, player));
            return false;
        }

        if (!canUse(abilityData, AbilityType.SKY_LEAP, now)) {
            LOGGER.atInfo().log("[BeanzCore][Ability] SKY_LEAP blocked: cooldown for %s", usernameOf(playerRef, player));
            return false;
        }

        EntityStatValue staminaValue = statMap.get(STAMINA_STAT_ID);
        double staminaBefore = staminaValue != null ? staminaValue.get() : -1.0;
        double staminaCost = rewardService.getJumpStaminaCost(skills);
        boolean exhaustedRecoveryActive = false;
        if (staminaValue == null || exhaustedRecoveryActive || staminaBefore <= EPSILON) {
            LOGGER.atInfo().log(
                "[BeanzCore][Ability] SKY_LEAP blocked: insufficient stamina for %s (staminaBefore=%.3f, staminaCost=%.3f, exhausted=%s)",
                usernameOf(playerRef, player),
                staminaBefore,
                staminaCost,
                exhaustedRecoveryActive
            );
            return false;
        }

        double jumpRatio = staminaCost <= EPSILON
            ? 1.0
            : Math.min(1.0, Math.max(0.0, staminaBefore / staminaCost));
        double staminaUsed = Math.min(staminaBefore, staminaCost);
        float staminaAfter = statMap.subtractStatValue(staminaValue.getIndex(), (float) staminaUsed);
        statMap.update();

        MovementSettings defaultSettings = movementManager.getDefaultSettings();
        double baseJumpForce = defaultSettings.jumpForce;
        int jumpLevel = skills.getLevel(SkillType.JUMP);
        double skillAdjustedJumpForce = baseJumpForce * rewardService.getJumpMultiplier(skills);
        double skillBonus = skillAdjustedJumpForce - baseJumpForce;
        double abilityBonus = skillAdjustedJumpForce * rewardService.getDoubleJumpForceScale() * jumpRatio;
        double finalJumpForce = baseJumpForce + skillBonus + abilityBonus;

        double previousX = velocity.getX();
        double previousY = velocity.getY();
        double previousZ = velocity.getZ();
        double finalY = Math.max(previousY, 0.0) + finalJumpForce;
        Vector3d finalVelocity = new Vector3d(previousX, finalY, previousZ);
        velocity.getInstructions().clear();
        velocity.addInstruction(finalVelocity, new VelocityConfig(), ChangeVelocityType.Set);
        velocity.set(finalVelocity);
        velocity.setClient(finalVelocity);

        LOGGER.atInfo().log(
            "[BeanzCore][AbilityDebug] SKY_LEAP final force for %s: jumpLevel=%s, skillBonus=%.3f, abilityBonus=%.3f, finalJumpForce=%.3f",
            usernameOf(playerRef, player),
            jumpLevel,
            skillBonus,
            abilityBonus,
            finalJumpForce
        );

        markUsed(abilityData, AbilityType.SKY_LEAP, now);
        jumpState.setUsedSkyLeapThisAirtime(true);

        LOGGER.atInfo().log(
            "[BeanzCore][Ability] SKY_LEAP used successfully for %s (level=%s, staminaBefore=%.3f, staminaCost=%.3f, staminaAfter=%.3f, jumpRatio=%.3f, finalJumpForce=%.3f)",
            usernameOf(playerRef, player),
            jumpLevel,
            staminaBefore,
            staminaCost,
            staminaAfter,
            jumpRatio,
            finalJumpForce
        );

        PacketHandler packetHandler = player != null ? player.getPlayerConnection() : null;
        if (packetHandler != null) {
            NotificationUtil.sendNotification(
                packetHandler,
                Message.raw("SKY_LEAP activated").color("#6fd0ff").bold(true),
                Message.raw("Ability3 launched you upward.").color("#d8e4f2")
            );
        }
        return true;
    }

    private PlayerSkillsComponent getOrCreateSkills(PlayerRef playerRef, Holder<EntityStore> holder) {
        return com.beanz.core.BeanzCoreMod.getInstance().getOrCreateSkills(playerRef, holder);
    }

    private JumpAbilityStateComponent getOrCreateJumpState(Holder<EntityStore> holder) {
        JumpAbilityStateComponent jumpState = holder.getComponent(JumpAbilityStateComponent.getComponentType());
        if (jumpState == null) {
            jumpState = new JumpAbilityStateComponent();
            holder.addComponent(JumpAbilityStateComponent.getComponentType(), jumpState);
        }
        return jumpState;
    }

    private String usernameOf(PlayerRef playerRef, Player player) {
        if (playerRef != null) {
            return playerRef.getUsername();
        }
        return player != null && player.getPlayerRef() != null ? player.getPlayerRef().getUsername() : "unknown-player";
    }
}
