package com.beanz.core.abilities;

import com.beanz.core.BeanzCoreMod;
import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class OverdriveInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<OverdriveInteraction> CODEC = BuilderCodec
        .builder(OverdriveInteraction.class, OverdriveInteraction::new, SimpleInstantInteraction.CODEC)
        .build();

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    public OverdriveInteraction() {
        super("Beanz_Overdrive");
    }

    public OverdriveInteraction(String id) {
        super(id);
    }

    @Override
    protected void firstRun(InteractionType interactionType, InteractionContext context, CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Player player = (Player) commandBuffer.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = player != null ? player.getPlayerRef() : null;

        LOGGER.atInfo().log("[BeanzCore][InputDebug] Overdrive ability trigger reached");
        LOGGER.atInfo().log(
            "[BeanzCore][InputDebug] ability=%s player=%s",
            interactionType,
            playerRef != null ? playerRef.getUsername() : "unknown-player"
        );

        if (playerRef == null) {
            LOGGER.atWarning().log("[BeanzCore][Ability] OVERDRIVE blocked: interaction fired without a player ref");
            return;
        }

        BeanzCoreMod.getInstance().getAbilityManager().useOverdrive(playerRef);
    }
}
