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

public class TestAbility3Interaction extends SimpleInstantInteraction {
    public static final BuilderCodec<TestAbility3Interaction> CODEC = BuilderCodec
        .builder(TestAbility3Interaction.class, TestAbility3Interaction::new, SimpleInstantInteraction.CODEC)
        .build();

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    public TestAbility3Interaction() {
        super("Beanz_Test_Ability");
    }

    public TestAbility3Interaction(String id) {
        super(id);
    }

    @Override
    protected void firstRun(InteractionType interactionType, InteractionContext context, CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Player player = (Player) commandBuffer.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = player != null ? player.getPlayerRef() : null;

        if (playerRef == null) {
            LOGGER.atWarning().log("[BeanzCore][Ability] SKY_LEAP blocked: interaction=%s fired without a player ref", interactionType);
            return;
        }

        BeanzCoreMod.getInstance().getAbilityManager().useSkyLeap(playerRef);
    }
}
