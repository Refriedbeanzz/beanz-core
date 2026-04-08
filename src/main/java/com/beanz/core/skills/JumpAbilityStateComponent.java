package com.beanz.core.skills;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class JumpAbilityStateComponent implements Component<EntityStore> {
    public static final BuilderCodec<JumpAbilityStateComponent> CODEC = BuilderCodec
        .builder(JumpAbilityStateComponent.class, JumpAbilityStateComponent::new)
        .append(
            new KeyedCodec<>("UsedDoubleJump", Codec.BOOLEAN),
            JumpAbilityStateComponent::setUsedDoubleJump,
            JumpAbilityStateComponent::hasUsedDoubleJump
        )
        .add()
        .append(
            new KeyedCodec<>("UsedWallJump", Codec.BOOLEAN),
            JumpAbilityStateComponent::setUsedWallJump,
            JumpAbilityStateComponent::hasUsedWallJump
        )
        .add()
        .append(
            new KeyedCodec<>("WasGrounded", Codec.BOOLEAN),
            JumpAbilityStateComponent::setWasGrounded,
            JumpAbilityStateComponent::wasGrounded
        )
        .add()
        .append(
            new KeyedCodec<>("DoubleJumpUnlockLogged", Codec.BOOLEAN),
            JumpAbilityStateComponent::setDoubleJumpUnlockLogged,
            JumpAbilityStateComponent::isDoubleJumpUnlockLogged
        )
        .add()
        .append(
            new KeyedCodec<>("WallJumpUnlockLogged", Codec.BOOLEAN),
            JumpAbilityStateComponent::setWallJumpUnlockLogged,
            JumpAbilityStateComponent::isWallJumpUnlockLogged
        )
        .add()
        .append(
            new KeyedCodec<>("RecentWallContactTicks", Codec.INTEGER),
            JumpAbilityStateComponent::setRecentWallContactTicks,
            JumpAbilityStateComponent::getRecentWallContactTicks
        )
        .add()
        .append(
            new KeyedCodec<>("RecentWallNormalX", Codec.DOUBLE),
            JumpAbilityStateComponent::setRecentWallNormalX,
            JumpAbilityStateComponent::getRecentWallNormalX
        )
        .add()
        .append(
            new KeyedCodec<>("RecentWallNormalZ", Codec.DOUBLE),
            JumpAbilityStateComponent::setRecentWallNormalZ,
            JumpAbilityStateComponent::getRecentWallNormalZ
        )
        .add()
        .build();

    private static ComponentType<EntityStore, JumpAbilityStateComponent> componentType;

    private boolean usedDoubleJump;
    private boolean usedWallJump;
    private boolean wasGrounded = true;
    private boolean doubleJumpUnlockLogged;
    private boolean wallJumpUnlockLogged;
    private int recentWallContactTicks;
    private double recentWallNormalX;
    private double recentWallNormalZ;

    public static ComponentType<EntityStore, JumpAbilityStateComponent> getComponentType() {
        return componentType;
    }

    public static void setComponentType(ComponentType<EntityStore, JumpAbilityStateComponent> componentType) {
        JumpAbilityStateComponent.componentType = componentType;
    }

    public boolean hasUsedDoubleJump() {
        return usedDoubleJump;
    }

    public void setUsedDoubleJump(boolean usedDoubleJump) {
        this.usedDoubleJump = usedDoubleJump;
    }

    public boolean hasUsedWallJump() {
        return usedWallJump;
    }

    public void setUsedWallJump(boolean usedWallJump) {
        this.usedWallJump = usedWallJump;
    }

    public boolean wasGrounded() {
        return wasGrounded;
    }

    public void setWasGrounded(boolean wasGrounded) {
        this.wasGrounded = wasGrounded;
    }

    public boolean isDoubleJumpUnlockLogged() {
        return doubleJumpUnlockLogged;
    }

    public void setDoubleJumpUnlockLogged(boolean doubleJumpUnlockLogged) {
        this.doubleJumpUnlockLogged = doubleJumpUnlockLogged;
    }

    public boolean isWallJumpUnlockLogged() {
        return wallJumpUnlockLogged;
    }

    public void setWallJumpUnlockLogged(boolean wallJumpUnlockLogged) {
        this.wallJumpUnlockLogged = wallJumpUnlockLogged;
    }

    public int getRecentWallContactTicks() {
        return recentWallContactTicks;
    }

    public void setRecentWallContactTicks(int recentWallContactTicks) {
        this.recentWallContactTicks = recentWallContactTicks;
    }

    public double getRecentWallNormalX() {
        return recentWallNormalX;
    }

    public void setRecentWallNormalX(double recentWallNormalX) {
        this.recentWallNormalX = recentWallNormalX;
    }

    public double getRecentWallNormalZ() {
        return recentWallNormalZ;
    }

    public void setRecentWallNormalZ(double recentWallNormalZ) {
        this.recentWallNormalZ = recentWallNormalZ;
    }

    public boolean hasRecentWallContact() {
        return recentWallContactTicks > 0;
    }

    public Vector3d getRecentWallNormal() {
        Vector3d wallNormal = new Vector3d(recentWallNormalX, 0.0, recentWallNormalZ);
        return wallNormal.closeToZero(0.0001) ? null : wallNormal.normalize();
    }

    public void updateRecentWallContact(Vector3d wallNormal, int graceTicks) {
        if (wallNormal == null || wallNormal.closeToZero(0.0001)) {
            return;
        }

        Vector3d normalized = new Vector3d(wallNormal.getX(), 0.0, wallNormal.getZ()).normalize();
        recentWallNormalX = normalized.getX();
        recentWallNormalZ = normalized.getZ();
        recentWallContactTicks = graceTicks;
    }

    public void tickRecentWallContact(boolean grounded) {
        if (grounded) {
            clearRecentWallContact();
            return;
        }

        if (recentWallContactTicks > 0) {
            recentWallContactTicks--;
        }
    }

    public void clearRecentWallContact() {
        recentWallContactTicks = 0;
        recentWallNormalX = 0.0;
        recentWallNormalZ = 0.0;
    }

    public void resetAirAbilities() {
        usedDoubleJump = false;
        usedWallJump = false;
        clearRecentWallContact();
    }

    @Override
    public JumpAbilityStateComponent clone() {
        JumpAbilityStateComponent copy = new JumpAbilityStateComponent();
        copy.usedDoubleJump = this.usedDoubleJump;
        copy.usedWallJump = this.usedWallJump;
        copy.wasGrounded = this.wasGrounded;
        copy.doubleJumpUnlockLogged = this.doubleJumpUnlockLogged;
        copy.wallJumpUnlockLogged = this.wallJumpUnlockLogged;
        copy.recentWallContactTicks = this.recentWallContactTicks;
        copy.recentWallNormalX = this.recentWallNormalX;
        copy.recentWallNormalZ = this.recentWallNormalZ;
        return copy;
    }
}
