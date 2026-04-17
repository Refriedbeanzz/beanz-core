package com.beanz.core.skills;

import com.beanz.core.BeanzCoreMod;
import com.beanz.core.abilities.AbilityType;
import com.beanz.core.abilities.PlayerAbilityData;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class RunningSkillSystem extends EntityTickingSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String STAMINA_STAT_ID = "stamina";
    private static final double EPSILON = 0.0001;
    private static final int XP_PER_SPRINT_TICK = 1;

    @Override
    public Query<EntityStore> getQuery() {
        return new AndQuery<>(
            Player.getComponentType(),
            MovementStatesComponent.getComponentType(),
            MovementManager.getComponentType(),
            EntityStatMap.getComponentType(),
            PlayerSkillsComponent.getComponentType(),
            PlayerAbilityData.getComponentType(),
            RunningAbilityStateComponent.getComponentType()
        );
    }

    @Override
    public void tick(
        float dt,
        int index,
        ArchetypeChunk<EntityStore> chunk,
        Store<EntityStore> store,
        CommandBuffer<EntityStore> commandBuffer
    ) {
        Player player = chunk.getComponent(index, Player.getComponentType());
        MovementStatesComponent movementStatesComponent = chunk.getComponent(index, MovementStatesComponent.getComponentType());
        MovementManager movementManager = chunk.getComponent(index, MovementManager.getComponentType());
        EntityStatMap statMap = chunk.getComponent(index, EntityStatMap.getComponentType());
        PlayerSkillsComponent skills = chunk.getComponent(index, PlayerSkillsComponent.getComponentType());
        PlayerAbilityData abilityData = chunk.getComponent(index, PlayerAbilityData.getComponentType());
        RunningAbilityStateComponent runState = chunk.getComponent(index, RunningAbilityStateComponent.getComponentType());
        Ref<EntityStore> ref = chunk.getReferenceTo(index);

        if (player == null || skills == null || abilityData == null || runState == null
            || movementStatesComponent == null || movementManager == null || statMap == null) {
            return;
        }

        MovementStates current = movementStatesComponent.getMovementStates();
        SkillRewardService rewardService = BeanzCoreMod.getInstance().getSkillService().getRewardService();
        long now = System.currentTimeMillis();

        // Sync ability unlocks for running
        BeanzCoreMod.getInstance().syncAbilityUnlocks(player.getPlayerRef(), skills, abilityData);

        // Apply speed multipliers based on Running level
        applySpeedMultipliers(ref, player, skills, abilityData, runState, movementManager, rewardService, now);

        // Return stamina while sprinting (counteracts engine drain — effective stamina cost reduction)
        if (current.sprinting) {
            returnSprintStamina(ref, skills, statMap, rewardService);
        }

        // Award XP while sprinting
        if (current.sprinting) {
            SkillProgressionResult progression = BeanzCoreMod.getInstance().getSkillService()
                .awardXp(skills, SkillType.RUNNING, XP_PER_SPRINT_TICK);
            BeanzCoreMod.getInstance().syncAbilityUnlocks(player.getPlayerRef(), skills, abilityData);

            if (progression.newLevel() > progression.previousLevel()) {
                LOGGER.atInfo().log(
                    "[BeanzCore][Running] Level-up for %s: skill=RUNNING, previousLevel=%s, newLevel=%s",
                    ref,
                    progression.previousLevel(),
                    progression.newLevel()
                );
                BeanzCoreMod.getInstance().getLevelUpNotificationService()
                    .notifyLevelUp(player, skills, SkillType.RUNNING, progression.newLevel());
            }
        }
    }

    private void applySpeedMultipliers(
        Ref<EntityStore> ref,
        Player player,
        PlayerSkillsComponent skills,
        PlayerAbilityData abilityData,
        RunningAbilityStateComponent runState,
        MovementManager movementManager,
        SkillRewardService rewardService,
        long now
    ) {
        MovementSettings defaultSettings = movementManager.getDefaultSettings();
        MovementSettings settings = movementManager.getSettings();

        double runMultiplier = rewardService.getRunSpeedMultiplier(skills);
        double sprintMultiplier = rewardService.getSprintSpeedMultiplier(skills);

        // Add Overdrive bonus on top of sprint multiplier if active
        boolean overdriveActive = runState.isOverdriveActive(now);
        if (overdriveActive) {
            sprintMultiplier += rewardService.getOverdriveSprintBonus();
        }

        float targetFwdRun = (float) (defaultSettings.forwardRunSpeedMultiplier * runMultiplier);
        float targetBwdRun = (float) (defaultSettings.backwardRunSpeedMultiplier * runMultiplier);
        float targetStrafeRun = (float) (defaultSettings.strafeRunSpeedMultiplier * runMultiplier);
        float targetFwdSprint = (float) (defaultSettings.forwardSprintSpeedMultiplier * sprintMultiplier);

        boolean changed = Math.abs(settings.forwardRunSpeedMultiplier - targetFwdRun) > EPSILON
            || Math.abs(settings.backwardRunSpeedMultiplier - targetBwdRun) > EPSILON
            || Math.abs(settings.strafeRunSpeedMultiplier - targetStrafeRun) > EPSILON
            || Math.abs(settings.forwardSprintSpeedMultiplier - targetFwdSprint) > EPSILON;

        if (changed) {
            settings.forwardRunSpeedMultiplier = targetFwdRun;
            settings.backwardRunSpeedMultiplier = targetBwdRun;
            settings.strafeRunSpeedMultiplier = targetStrafeRun;
            settings.forwardSprintSpeedMultiplier = targetFwdSprint;
            movementManager.update(player.getPlayerConnection());

            LOGGER.atInfo().log(
                "[BeanzCore][Running] Speed updated for %s: runMult=%.3f, sprintMult=%.3f, overdriveActive=%s",
                ref,
                runMultiplier,
                sprintMultiplier,
                overdriveActive
            );
        }
    }

    private void returnSprintStamina(
        Ref<EntityStore> ref,
        PlayerSkillsComponent skills,
        EntityStatMap statMap,
        SkillRewardService rewardService
    ) {
        float returnAmount = rewardService.getSprintStaminaReturnPerTick(skills);
        if (returnAmount <= 0.0f) {
            return;
        }

        EntityStatValue staminaValue = statMap.get(STAMINA_STAT_ID);
        if (staminaValue == null) {
            return;
        }

        float current = staminaValue.get();
        float max = staminaValue.getMax();
        if (current >= max) {
            return;
        }

        statMap.addStatValue(staminaValue.getIndex(), returnAmount);
        statMap.update();
    }
}
