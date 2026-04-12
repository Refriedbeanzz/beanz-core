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
        .append(
            new KeyedCodec<>("AirTicks", Codec.INTEGER),
            JumpAbilityStateComponent::setAirTicks,
            JumpAbilityStateComponent::getAirTicks
        )
        .add()
        .append(
            new KeyedCodec<>("JumpReleasedSinceGroundJump", Codec.BOOLEAN),
            JumpAbilityStateComponent::setJumpReleasedSinceGroundJump,
            JumpAbilityStateComponent::hasJumpReleasedSinceGroundJump
        )
        .add()
        .append(
            new KeyedCodec<>("JumpWasPressedLastTick", Codec.BOOLEAN),
            JumpAbilityStateComponent::setJumpWasPressedLastTick,
            JumpAbilityStateComponent::wasJumpPressedLastTick
        )
        .add()
        .append(
            new KeyedCodec<>("HasLeftGroundSinceInitialJump", Codec.BOOLEAN),
            JumpAbilityStateComponent::setHasLeftGroundSinceInitialJump,
            JumpAbilityStateComponent::hasLeftGroundSinceInitialJump
        )
        .add()
        .append(
            new KeyedCodec<>("DoubleTapWindowTicks", Codec.INTEGER),
            JumpAbilityStateComponent::setDoubleTapWindowTicks,
            JumpAbilityStateComponent::getDoubleTapWindowTicks
        )
        .add()
        .append(
            new KeyedCodec<>("DoubleJumpCooldownTicks", Codec.INTEGER),
            JumpAbilityStateComponent::setDoubleJumpCooldownTicks,
            JumpAbilityStateComponent::getDoubleJumpCooldownTicks
        )
        .add()
        .append(
            new KeyedCodec<>("UsedSkyLeapThisAirtime", Codec.BOOLEAN),
            JumpAbilityStateComponent::setUsedSkyLeapThisAirtime,
            JumpAbilityStateComponent::hasUsedSkyLeapThisAirtime
        )
        .add()
        .append(
            new KeyedCodec<>("BufferedDoubleJumpPressTicks", Codec.INTEGER),
            JumpAbilityStateComponent::setBufferedDoubleJumpPressTicks,
            JumpAbilityStateComponent::getBufferedDoubleJumpPressTicks
        )
        .add()
        .append(
            new KeyedCodec<>("StaminaExhaustedAtMillis", Codec.LONG),
            JumpAbilityStateComponent::setStaminaExhaustedAtMillis,
            JumpAbilityStateComponent::getStaminaExhaustedAtMillis
        )
        .add()
        .append(
            new KeyedCodec<>("StaminaDelayStatIndex", Codec.INTEGER),
            JumpAbilityStateComponent::setStaminaDelayStatIndex,
            JumpAbilityStateComponent::getStaminaDelayStatIndex
        )
        .add()
        .append(
            new KeyedCodec<>("StaminaDelayStatValue", Codec.FLOAT),
            JumpAbilityStateComponent::setStaminaDelayStatValue,
            JumpAbilityStateComponent::getStaminaDelayStatValue
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
    private int airTicks;
    private boolean jumpReleasedSinceGroundJump;
    private boolean jumpWasPressedLastTick;
    private boolean hasLeftGroundSinceInitialJump;
    private int doubleTapWindowTicks;
    private int doubleJumpCooldownTicks;
    private boolean usedSkyLeapThisAirtime;
    private int bufferedDoubleJumpPressTicks;
    private long staminaExhaustedAtMillis;
    private int staminaDelayStatIndex;
    private float staminaDelayStatValue;

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

    public int getAirTicks() {
        return airTicks;
    }

    public void setAirTicks(int airTicks) {
        this.airTicks = airTicks;
    }

    public boolean hasJumpReleasedSinceGroundJump() {
        return jumpReleasedSinceGroundJump;
    }

    public void setJumpReleasedSinceGroundJump(boolean jumpReleasedSinceGroundJump) {
        this.jumpReleasedSinceGroundJump = jumpReleasedSinceGroundJump;
    }

    public void trackAirborneState(boolean onGround) {
        if (onGround) {
            airTicks = 0;
            return;
        }

        airTicks++;
    }

    public void markGroundJumpStarted() {
        airTicks = 0;
        jumpReleasedSinceGroundJump = false;
        hasLeftGroundSinceInitialJump = false;
    }

    public boolean wasJumpPressedLastTick() {
        return jumpWasPressedLastTick;
    }

    public void setJumpWasPressedLastTick(boolean jumpWasPressedLastTick) {
        this.jumpWasPressedLastTick = jumpWasPressedLastTick;
    }

    public boolean hasLeftGroundSinceInitialJump() {
        return hasLeftGroundSinceInitialJump;
    }

    public void setHasLeftGroundSinceInitialJump(boolean hasLeftGroundSinceInitialJump) {
        this.hasLeftGroundSinceInitialJump = hasLeftGroundSinceInitialJump;
    }

    public int getDoubleTapWindowTicks() {
        return doubleTapWindowTicks;
    }

    public void setDoubleTapWindowTicks(int doubleTapWindowTicks) {
        this.doubleTapWindowTicks = Math.max(0, doubleTapWindowTicks);
    }

    public int getDoubleJumpCooldownTicks() {
        return doubleJumpCooldownTicks;
    }

    public void setDoubleJumpCooldownTicks(int doubleJumpCooldownTicks) {
        this.doubleJumpCooldownTicks = Math.max(0, doubleJumpCooldownTicks);
    }

    public boolean hasUsedSkyLeapThisAirtime() {
        return usedSkyLeapThisAirtime;
    }

    public void setUsedSkyLeapThisAirtime(boolean usedSkyLeapThisAirtime) {
        this.usedSkyLeapThisAirtime = usedSkyLeapThisAirtime;
    }

    public int getBufferedDoubleJumpPressTicks() {
        return bufferedDoubleJumpPressTicks;
    }

    public void setBufferedDoubleJumpPressTicks(int bufferedDoubleJumpPressTicks) {
        this.bufferedDoubleJumpPressTicks = Math.max(0, bufferedDoubleJumpPressTicks);
    }

    public long getStaminaExhaustedAtMillis() {
        return staminaExhaustedAtMillis;
    }

    public void setStaminaExhaustedAtMillis(long staminaExhaustedAtMillis) {
        this.staminaExhaustedAtMillis = Math.max(0L, staminaExhaustedAtMillis);
    }

    public int getStaminaDelayStatIndex() {
        return staminaDelayStatIndex;
    }

    public void setStaminaDelayStatIndex(int staminaDelayStatIndex) {
        this.staminaDelayStatIndex = Math.max(0, staminaDelayStatIndex);
    }

    public float getStaminaDelayStatValue() {
        return staminaDelayStatValue;
    }

    public void setStaminaDelayStatValue(float staminaDelayStatValue) {
        this.staminaDelayStatValue = staminaDelayStatValue;
    }

    public void tickDoubleJumpTimers(boolean onGround) {
        if (doubleTapWindowTicks > 0) {
            doubleTapWindowTicks--;
        }
        if (doubleJumpCooldownTicks > 0) {
            doubleJumpCooldownTicks--;
        }
        if (bufferedDoubleJumpPressTicks > 0) {
            bufferedDoubleJumpPressTicks--;
        }
        if (onGround) {
            doubleTapWindowTicks = 0;
            bufferedDoubleJumpPressTicks = 0;
        }
    }

    public void armDoubleTapWindow(int ticks) {
        doubleTapWindowTicks = Math.max(doubleTapWindowTicks, ticks);
    }

    public void consumeDoubleJump(int cooldownTicks) {
        usedDoubleJump = true;
        doubleTapWindowTicks = 0;
        doubleJumpCooldownTicks = Math.max(doubleJumpCooldownTicks, cooldownTicks);
        bufferedDoubleJumpPressTicks = 0;
    }

    public void bufferDoubleJumpPress(int ticks) {
        bufferedDoubleJumpPressTicks = Math.max(bufferedDoubleJumpPressTicks, ticks);
    }

    public void resetAirAbilities() {
        usedDoubleJump = false;
        usedWallJump = false;
        usedSkyLeapThisAirtime = false;
        airTicks = 0;
        jumpReleasedSinceGroundJump = false;
        jumpWasPressedLastTick = false;
        hasLeftGroundSinceInitialJump = false;
        doubleTapWindowTicks = 0;
        bufferedDoubleJumpPressTicks = 0;
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
        copy.airTicks = this.airTicks;
        copy.jumpReleasedSinceGroundJump = this.jumpReleasedSinceGroundJump;
        copy.jumpWasPressedLastTick = this.jumpWasPressedLastTick;
        copy.hasLeftGroundSinceInitialJump = this.hasLeftGroundSinceInitialJump;
        copy.doubleTapWindowTicks = this.doubleTapWindowTicks;
        copy.doubleJumpCooldownTicks = this.doubleJumpCooldownTicks;
        copy.usedSkyLeapThisAirtime = this.usedSkyLeapThisAirtime;
        copy.bufferedDoubleJumpPressTicks = this.bufferedDoubleJumpPressTicks;
        copy.staminaExhaustedAtMillis = this.staminaExhaustedAtMillis;
        copy.staminaDelayStatIndex = this.staminaDelayStatIndex;
        copy.staminaDelayStatValue = this.staminaDelayStatValue;
        return copy;
    }
}
