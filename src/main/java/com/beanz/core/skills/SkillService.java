package com.beanz.core.skills;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SkillService {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final SkillRewardService rewardService = new SkillRewardService();

    public PlayerSkillsComponent getOrCreateSkills(PlayerRef playerRef, Holder<EntityStore> holder) {
        if (holder == null) {
            LOGGER.atWarning().log(
                "Cannot create skills for %s because no holder is available.",
                usernameOf(playerRef)
            );
            return new PlayerSkillsComponent();
        }

        PlayerSkillsComponent skills = holder.getComponent(PlayerSkillsComponent.getComponentType());

        if (skills == null) {
            skills = new PlayerSkillsComponent();
            holder.addComponent(PlayerSkillsComponent.getComponentType(), skills);
        }

        return skills;
    }

    public PlayerSkillsComponent getOrCreateSkills(PlayerRef playerRef, Store<EntityStore> store, Ref<EntityStore> ref) {
        if (store == null || ref == null) {
            LOGGER.atWarning().log(
                "Cannot resolve live skill data for %s because store or ref is null (store=%s, ref=%s).",
                usernameOf(playerRef),
                store != null,
                ref
            );
            return new PlayerSkillsComponent();
        }

        PlayerSkillsComponent skills = store.getComponent(ref, PlayerSkillsComponent.getComponentType());

        if (skills == null) {
            skills = new PlayerSkillsComponent();
            store.addComponent(ref, PlayerSkillsComponent.getComponentType(), skills);
            LOGGER.atInfo().log(
                "Created live skill data for %s on entity %s.",
                usernameOf(playerRef),
                ref
            );
        }

        return skills;
    }

    public SkillSnapshot getSnapshot(PlayerSkillsComponent skills, SkillType skillType) {
        return skills.snapshot(skillType);
    }

    public SkillProgressionResult awardXp(PlayerSkillsComponent skills, SkillType skillType, int amount) {
        int previousXp = skills.getXp(skillType);
        int previousLevel = skills.getLevel(skillType);

        skills.addXp(skillType, amount);

        return new SkillProgressionResult(
            skillType,
            previousXp,
            skills.getXp(skillType),
            previousLevel,
            skills.getLevel(skillType),
            amount
        );
    }

    public SkillRewardService getRewardService() {
        return rewardService;
    }

    private String usernameOf(PlayerRef playerRef) {
        return playerRef != null ? playerRef.getUsername() : "unknown-player";
    }
}
