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
public class SkyLeapInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<SkyLeapInteraction> CODEC = BuilderCodec
        .builder(SkyLeapInteraction.class, SkyLeapInteraction::new, SimpleInstantInteraction.CODEC)
        .build();

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    public SkyLeapInteraction() {
        super("Beanz_Sky_Leap");
    }

    public SkyLeapInteraction(String id) {
        super(id);
    }

    @Override
    protected void firstRun(InteractionType interactionType, InteractionContext context, CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Player player = (Player) commandBuffer.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = player != null ? player.getPlayerRef() : null;

        LOGGER.atInfo().log("[BeanzCore][InputDebug] working ability trigger reached");
        LOGGER.atInfo().log(
            "[BeanzCore][InputDebug] ability=%s player=%s",
            interactionType,
            playerRef != null ? playerRef.getUsername() : "unknown-player"
        );

        if (playerRef == null) {
            LOGGER.atWarning().log("[BeanzCore][Ability] SKY_LEAP blocked: interaction fired without a player ref");
            return;
        }

        BeanzCoreMod.getInstance().getAbilityManager().useSkyLeap(playerRef);
    }
}
