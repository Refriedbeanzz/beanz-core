package com.beanz.core.abilities;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.EnumMap;
import java.util.Map;

public class PlayerAbilityData implements Component<EntityStore> {
    public static final BuilderCodec<PlayerAbilityData> CODEC = BuilderCodec
        .builder(PlayerAbilityData.class, PlayerAbilityData::new)
        .append(new KeyedCodec<>("SkyLeap", AbilityData.CODEC), PlayerAbilityData::setSkyLeap, PlayerAbilityData::getSkyLeap)
        .add()
        .build();

    private static ComponentType<EntityStore, PlayerAbilityData> componentType;

    private AbilityData skyLeap = defaultData(AbilityType.SKY_LEAP);

    public static ComponentType<EntityStore, PlayerAbilityData> getComponentType() {
        return componentType;
    }

    public static void setComponentType(ComponentType<EntityStore, PlayerAbilityData> componentType) {
        PlayerAbilityData.componentType = componentType;
    }

    public AbilityData get(AbilityType type) {
        return switch (type) {
            case SKY_LEAP -> skyLeap;
        };
    }

    public boolean isUnlocked(AbilityType type) {
        return get(type).isUnlocked();
    }

    public void unlock(AbilityType type) {
        get(type).setUnlocked(true);
    }

    public void lock(AbilityType type) {
        get(type).setUnlocked(false);
    }

    public boolean canUse(AbilityType type, long now) {
        AbilityData abilityData = get(type);
        return abilityData.isUnlocked() && !abilityData.isOnCooldown(now);
    }

    public void markUsed(AbilityType type, long now) {
        get(type).markUsed(now);
    }

    public Map<AbilityType, AbilityData> asMap() {
        Map<AbilityType, AbilityData> abilities = new EnumMap<>(AbilityType.class);
        abilities.put(AbilityType.SKY_LEAP, skyLeap);
        return abilities;
    }

    public AbilityData getSkyLeap() {
        return skyLeap;
    }

    public void setSkyLeap(AbilityData skyLeap) {
        this.skyLeap = skyLeap != null ? skyLeap : defaultData(AbilityType.SKY_LEAP);
    }

    @Override
    public PlayerAbilityData clone() {
        PlayerAbilityData copy = new PlayerAbilityData();
        copy.skyLeap = this.skyLeap.copy();
        return copy;
    }

    private static AbilityData defaultData(AbilityType type) {
        return new AbilityData(type, false, 2000L, Long.MIN_VALUE);
    }
}
