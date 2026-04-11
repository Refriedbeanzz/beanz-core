package com.beanz.core.abilities;

import com.beanz.core.skills.JumpAbilityStateComponent;
import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class TestAbility3Interaction extends SimpleInstantInteraction {
    public static final BuilderCodec<TestAbility3Interaction> CODEC = BuilderCodec
        .builder(TestAbility3Interaction.class, TestAbility3Interaction::new, SimpleInstantInteraction.CODEC)
        .build();

    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final double SKY_LEAP_FORCE_SCALE = 1.15;

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
        JumpAbilityStateComponent jumpState = (JumpAbilityStateComponent) commandBuffer.getComponent(ref, JumpAbilityStateComponent.getComponentType());
        MovementStatesComponent movementStates = (MovementStatesComponent) commandBuffer.getComponent(ref, MovementStatesComponent.getComponentType());
        MovementManager movementManager = (MovementManager) commandBuffer.getComponent(ref, MovementManager.getComponentType());
        Velocity velocity = (Velocity) commandBuffer.getComponent(ref, Velocity.getComponentType());
        PlayerRef playerRef = player != null ? player.getPlayerRef() : null;
        String username = playerRef != null ? playerRef.getUsername() : "unknown-player";
        boolean airborne = movementStates != null && movementStates.getMovementStates() != null && !movementStates.getMovementStates().onGround;
        PacketHandler packetHandler = player != null ? player.getPlayerConnection() : null;

        if (!airborne) {
            LOGGER.atInfo().log("[BeanzCore][Ability] SKY_LEAP blocked: not airborne for %s", username);
            if (packetHandler != null) {
                NotificationUtil.sendNotification(
                    packetHandler,
                    Message.raw("SKY_LEAP blocked").color("#ffb347").bold(true),
                    Message.raw("Use Ability3 while airborne.").color("#d8e4f2")
                );
            }
            return;
        }

        if (jumpState != null && jumpState.hasUsedSkyLeapThisAirtime()) {
            LOGGER.atInfo().log("[BeanzCore][Ability] SKY_LEAP blocked: already used this airtime for %s", username);
            return;
        }

        if (velocity == null || movementManager == null || movementManager.getDefaultSettings() == null) {
            LOGGER.atInfo().log("[BeanzCore][Ability] SKY_LEAP blocked: missing movement runtime for %s", username);
            return;
        }

        MovementSettings defaultSettings = movementManager.getDefaultSettings();
        double leapBoost = defaultSettings.jumpForce * SKY_LEAP_FORCE_SCALE;
        double finalY = Math.max(velocity.getY(), 0.0) + leapBoost;
        Vector3d targetVelocity = new Vector3d(velocity.getX(), finalY, velocity.getZ());
        if (jumpState != null) {
            jumpState.setUsedSkyLeapThisAirtime(true);
        }
        velocity.getInstructions().clear();
        velocity.addInstruction(targetVelocity, new VelocityConfig(), ChangeVelocityType.Set);
        velocity.set(targetVelocity);
        velocity.setClient(targetVelocity);

        LOGGER.atInfo().log(
            "[BeanzCore][Ability] SKY_LEAP used for %s (interaction=%s, boost=%.3f, finalY=%.3f)",
            username,
            interactionType,
            leapBoost,
            finalY
        );

        if (packetHandler != null) {
            NotificationUtil.sendNotification(
                packetHandler,
                Message.raw("SKY_LEAP activated").color("#6fd0ff").bold(true),
                Message.raw("Ability3 launched you upward.").color("#d8e4f2")
            );
        }
    }
}
