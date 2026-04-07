package com.beanz.core.skills;

import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class JumpSkillSystem extends EntityTickingSystem<EntityStore> {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final int XP_PER_JUMP = 1;

    @Override
    public Query<EntityStore> getQuery() {
        return new AndQuery<>(
            Player.getComponentType(),
            MovementStatesComponent.getComponentType(),
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
        PlayerSkillsComponent skills = chunk.getComponent(index, PlayerSkillsComponent.getComponentType());

        if (player == null || skills == null) {
            return;
        }

        int previousLevel = skills.getLevel(SkillType.JUMP);
        skills.addXp(SkillType.JUMP, XP_PER_JUMP);

        LOGGER.atInfo().log(
            "Jump detected for %s: +%s Jump XP (xp=%s, level=%s -> %s, plannedBonus=%s)",
            ref,
            XP_PER_JUMP,
            skills.getXp(SkillType.JUMP),
            previousLevel,
            skills.getLevel(SkillType.JUMP),
            JumpSkillRewards.getPlannedJumpForceBonus(skills.getLevel(SkillType.JUMP))
        );
    }
}
