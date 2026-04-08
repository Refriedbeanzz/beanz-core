package com.beanz.core.skills;

import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class JumpSkillSystem extends EntityTickingSystem<EntityStore> {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final int XP_PER_JUMP = 1;
    private static final double EPSILON = 0.0001;

    @Override
    public Query<EntityStore> getQuery() {
        return new AndQuery<>(
            Player.getComponentType(),
            MovementStatesComponent.getComponentType(),
            MovementManager.getComponentType(),
            PlayerSkillsComponent.getComponentType()
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
        MovementStatesComponent movementStatesComponent = chunk.getComponent(index, MovementStatesComponent.getComponentType());
        MovementStates current = movementStatesComponent.getMovementStates();
        MovementStates previous = movementStatesComponent.getSentMovementStates();

        if (!current.jumping || previous.jumping) {
            return;
        }

        Ref<EntityStore> ref = chunk.getReferenceTo(index);
        Player player = chunk.getComponent(index, Player.getComponentType());
        MovementManager movementManager = chunk.getComponent(index, MovementManager.getComponentType());
        PlayerSkillsComponent skills = chunk.getComponent(index, PlayerSkillsComponent.getComponentType());
        SkillService skillService = com.beanz.core.BeanzCoreMod.getInstance().getSkillService();

        if (player == null || skills == null || movementManager == null) {
            return;
        }

        int jumpLevel = skills.getLevel(SkillType.JUMP);
        SkillRewardService rewardService = skillService.getRewardService();
        MovementSettings settings = movementManager.getSettings();
        MovementSettings defaultSettings = movementManager.getDefaultSettings();
        double baseJumpForce = defaultSettings.jumpForce;
        double multiplier = rewardService.getJumpMultiplier(skills);
        double previousConfiguredJumpForce = settings.jumpForce;
        double boostedJumpForce = baseJumpForce * multiplier;

        if (Math.abs(previousConfiguredJumpForce - boostedJumpForce) > EPSILON) {
            settings.jumpForce = (float) boostedJumpForce;
            movementManager.update(player.getPlayerConnection());

            LOGGER.atInfo().log(
                "Adjusted runtime MovementManager.settings.jumpForce for %s: before=%.3f, after=%.3f, level=%s, multiplier=%.3f",
                ref,
                previousConfiguredJumpForce,
                boostedJumpForce,
                jumpLevel,
                multiplier
            );
        }

        SkillProgressionResult progression = skillService.awardXp(skills, SkillType.JUMP, XP_PER_JUMP);

        if (progression.newLevel() > progression.previousLevel()) {
            com.beanz.core.BeanzCoreMod.getInstance()
                .getLevelUpNotificationService()
                .notifyLevelUp(player, SkillType.JUMP, progression.newLevel());
        }

        LOGGER.atInfo().log(
            "Jump XP awarded for %s: previousXp=%s, newXp=%s, currentLevel=%s, MovementManager.settings.jumpForce=%.3f -> %.3f, multiplier=%.3f",
            ref,
            progression.previousXp(),
            progression.newXp(),
            progression.newLevel(),
            previousConfiguredJumpForce,
            boostedJumpForce,
            multiplier
        );
    }
}
