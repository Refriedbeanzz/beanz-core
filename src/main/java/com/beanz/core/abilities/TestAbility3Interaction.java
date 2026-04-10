package com.beanz.core.abilities;

import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class TestAbility3Interaction extends SimpleInstantInteraction {
    public static final BuilderCodec<TestAbility3Interaction> CODEC = BuilderCodec
        .builder(TestAbility3Interaction.class, TestAbility3Interaction::new, SimpleInstantInteraction.CODEC)
        .build();

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final double TEST_UPWARD_BUMP = 3.5;

    public TestAbility3Interaction() {
        super("Beanz_Ability3_Test");
    }

    public TestAbility3Interaction(String id) {
        super(id);
    }

    @Override
    protected void firstRun(InteractionType interactionType, InteractionContext context, CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Player player = (Player) commandBuffer.getComponent(ref, Player.getComponentType());
        Velocity velocity = (Velocity) commandBuffer.getComponent(ref, Velocity.getComponentType());
        PlayerRef playerRef = player != null ? player.getPlayerRef() : null;
        String username = playerRef != null ? playerRef.getUsername() : "unknown-player";

        LOGGER.atInfo().log("[BeanzCore][TestAbility] Ability3 fired successfully for %s", username);
        LOGGER.atInfo().log("[BeanzCore][InputDebug] working ability trigger reached");
        LOGGER.atInfo().log("[BeanzCore][InputDebug] ability=%s player=%s", interactionType, username);

        if (velocity == null) {
            LOGGER.atInfo().log("[BeanzCore][TestAbility] No velocity component found for %s; log-only proof", username);
            return;
        }

        double finalY = Math.max(velocity.getY(), 0.0) + TEST_UPWARD_BUMP;
        Vector3d targetVelocity = new Vector3d(velocity.getX(), finalY, velocity.getZ());
        velocity.getInstructions().clear();
        velocity.addInstruction(targetVelocity, new VelocityConfig(), ChangeVelocityType.Set);
        velocity.set(targetVelocity);
        velocity.setClient(targetVelocity);
    }
}
