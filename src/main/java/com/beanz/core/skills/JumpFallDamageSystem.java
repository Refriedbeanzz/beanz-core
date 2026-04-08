package com.beanz.core.skills;

import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class JumpFallDamageSystem extends DamageEventSystem {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final Query<EntityStore> QUERY = new AndQuery<>(
        Player.getComponentType(),
        PlayerSkillsComponent.getComponentType()
    );

    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }

    @Override
    public com.hypixel.hytale.component.SystemGroup getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    @Override
    public void handle(
        int index,
        ArchetypeChunk<EntityStore> chunk,
        Store<EntityStore> store,
        CommandBuffer<EntityStore> commandBuffer,
        Damage damage
    ) {
        if (damage == null || damage.getCause() != DamageCause.FALL) {
            return;
        }

        PlayerSkillsComponent skills = chunk.getComponent(index, PlayerSkillsComponent.getComponentType());
        SkillRewardService rewardService = com.beanz.core.BeanzCoreMod.getInstance().getSkillService().getRewardService();

        if (skills == null) {
            return;
        }

        int jumpLevel = skills.getLevel(SkillType.JUMP);
        float previousDamage = damage.getAmount();
        double fallDamageMultiplier = rewardService.getFallDamageMultiplier(skills);
        float reducedDamage = (float) (previousDamage * fallDamageMultiplier);

        damage.setAmount(reducedDamage);

        LOGGER.atInfo().log(
            "Reduced fall damage for Jump skill: level=%s, damage=%.3f -> %.3f, reduction=%.1f%%, multiplier=%.3f",
            jumpLevel,
            previousDamage,
            reducedDamage,
            rewardService.getFallDamageReductionPercent(skills),
            fallDamageMultiplier
        );
    }
}
