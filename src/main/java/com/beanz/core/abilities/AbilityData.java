package com.beanz.core.abilities;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class AbilityData {
    public static final BuilderCodec<AbilityData> CODEC = BuilderCodec
        .builder(AbilityData.class, AbilityData::new)
        .append(new KeyedCodec<>("Type", Codec.STRING), AbilityData::setTypeName, AbilityData::getTypeName)
        .add()
        .append(new KeyedCodec<>("Unlocked", Codec.BOOLEAN), AbilityData::setUnlocked, AbilityData::isUnlocked)
        .add()
        .append(new KeyedCodec<>("CooldownMs", Codec.LONG), AbilityData::setCooldownMs, AbilityData::getCooldownMs)
        .add()
        .append(new KeyedCodec<>("LastUsedAt", Codec.LONG), AbilityData::setLastUsedAt, AbilityData::getLastUsedAt)
        .add()
        .build();

    private AbilityType type = AbilityType.SKY_LEAP;
    private boolean unlocked;
    private long cooldownMs = 2000L;
    private long lastUsedAt = Long.MIN_VALUE;

    public AbilityData() {
    }

    public AbilityData(AbilityType type, boolean unlocked, long cooldownMs, long lastUsedAt) {
        this.type = type;
        this.unlocked = unlocked;
        this.cooldownMs = cooldownMs;
        this.lastUsedAt = lastUsedAt;
    }

    public AbilityType getType() {
        return type;
    }

    public void setType(AbilityType type) {
        this.type = type;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    public void setCooldownMs(long cooldownMs) {
        this.cooldownMs = Math.max(0L, cooldownMs);
    }

    public long getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(long lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public boolean isOnCooldown(long now) {
        return cooldownMs > 0L && lastUsedAt != Long.MIN_VALUE && now - lastUsedAt < cooldownMs;
    }

    public void markUsed(long now) {
        lastUsedAt = now;
    }

    public AbilityData copy() {
        return new AbilityData(type, unlocked, cooldownMs, lastUsedAt);
    }

    private String getTypeName() {
        return type.name();
    }

    private void setTypeName(String typeName) {
        this.type = AbilityType.valueOf(typeName);
    }
}
