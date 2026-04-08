package com.beanz.core;

import com.beanz.core.skills.PlayerSkillsComponent;
import com.beanz.core.skills.LevelProgress;
import com.beanz.core.skills.SkillLevelTable;
import com.beanz.core.skills.SkillService;
import com.beanz.core.skills.SkillSnapshot;
import com.beanz.core.skills.SkillType;
import com.beanz.core.ui.BeanzMenuViewData;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class BeanzCommand extends AbstractPlayerCommand {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public BeanzCommand() {
        super("beanz", "Open the BeanzSkillz menu");
    }

    @Override
    protected void execute(
        CommandContext context,
        Store<EntityStore> store,
        Ref<EntityStore> ref,
        PlayerRef playerRef,
        World world
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());

        if (player == null) {
            BeanzCoreMod.getInstance().getLogger().atWarning().log(
                "/beanz aborted because no Player component was found for ref %s.",
                ref
            );
            return;
        }

        PlayerSkillsComponent skills = BeanzCoreMod.getInstance().getOrCreateSkills(playerRef, store, ref);
        SkillService skillService = BeanzCoreMod.getInstance().getSkillService();
        SkillSnapshot jumpSnapshot = skillService.getSnapshot(skills, SkillType.JUMP);
        LevelProgress progress = SkillLevelTable.getProgress(jumpSnapshot.xp());
        BeanzMenuViewData viewData = BeanzMenuViewData.from(jumpSnapshot, progress);

        LOGGER.atInfo().log(
            "Opening /beanz UI for %s with Jump values: skill=%s, xp=%s, level=%s, progress=%.3f",
            playerRef != null ? playerRef.getUsername() : "unknown-player",
            jumpSnapshot.skillName(),
            jumpSnapshot.xp(),
            jumpSnapshot.level(),
            progress.progressFraction()
        );

        player.getPageManager().openCustomPage(ref, store, new BeanzMenuPage(playerRef, viewData));
    }
}
