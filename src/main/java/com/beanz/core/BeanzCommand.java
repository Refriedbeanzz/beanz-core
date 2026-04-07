package com.beanz.core;

import com.beanz.core.skills.PlayerSkillsComponent;
import com.beanz.core.skills.SkillSnapshot;
import com.beanz.core.skills.SkillType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class BeanzCommand extends AbstractPlayerCommand {
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
        PlayerSkillsComponent skills = BeanzCoreMod.getInstance().getOrCreateSkills(playerRef);
        SkillSnapshot jumpSnapshot = skills.snapshot(SkillType.JUMP);

        player.getPageManager().openCustomPage(ref, store, new BeanzMenuPage(playerRef, jumpSnapshot));
    }
}
