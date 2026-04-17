package com.beanz.core.skills;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class RunningAbilityStateComponent implements Component<EntityStore> {
    public static final BuilderCodec<RunningAbilityStateComponent> CODEC = BuilderCodec
        .builder(RunningAbilityStateComponent.class, RunningAbilityStateComponent::new)
        .append(
            new KeyedCodec<>("OverdriveActiveUntilMillis", Codec.LONG),
            RunningAbilityStateComponent::setOverdriveActiveUntilMillis,
            RunningAbilityStateComponent::getOverdriveActiveUntilMillis
        )
        .add()
        .build();

    private static ComponentType<EntityStore, RunningAbilityStateComponent> componentType;

    private long overdriveActiveUntilMillis;

    public static ComponentType<EntityStore, RunningAbilityStateComponent> getComponentType() {
        return componentType;
    }

    public static void setComponentType(ComponentType<EntityStore, RunningAbilityStateComponent> componentType) {
        RunningAbilityStateComponent.componentType = componentType;
    }

    public long getOverdriveActiveUntilMillis() {
        return overdriveActiveUntilMillis;
    }

    public void setOverdriveActiveUntilMillis(long overdriveActiveUntilMillis) {
        this.overdriveActiveUntilMillis = overdriveActiveUntilMillis;
    }

    public boolean isOverdriveActive(long now) {
        return overdriveActiveUntilMillis > now;
    }

    public void activateOverdrive(long now, long durationMs) {
        overdriveActiveUntilMillis = now + durationMs;
    }

    public void clearOverdrive() {
        overdriveActiveUntilMillis = 0L;
    }

    @Override
    public RunningAbilityStateComponent clone() {
        RunningAbilityStateComponent copy = new RunningAbilityStateComponent();
        copy.overdriveActiveUntilMillis = this.overdriveActiveUntilMillis;
        return copy;
    }
}
