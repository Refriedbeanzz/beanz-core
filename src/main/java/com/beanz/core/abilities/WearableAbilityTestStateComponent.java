package com.beanz.core.abilities;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class WearableAbilityTestStateComponent implements Component<EntityStore> {
    public static final BuilderCodec<WearableAbilityTestStateComponent> CODEC = BuilderCodec
        .builder(WearableAbilityTestStateComponent.class, WearableAbilityTestStateComponent::new)
        .append(
            new KeyedCodec<>("WearableEquipped", Codec.BOOLEAN),
            WearableAbilityTestStateComponent::setWearableEquipped,
            WearableAbilityTestStateComponent::isWearableEquipped
        )
        .add()
        .build();

    private static ComponentType<EntityStore, WearableAbilityTestStateComponent> componentType;

    private boolean wearableEquipped;

    public static ComponentType<EntityStore, WearableAbilityTestStateComponent> getComponentType() {
        return componentType;
    }

    public static void setComponentType(ComponentType<EntityStore, WearableAbilityTestStateComponent> componentType) {
        WearableAbilityTestStateComponent.componentType = componentType;
    }

    public boolean isWearableEquipped() {
        return wearableEquipped;
    }

    public void setWearableEquipped(boolean wearableEquipped) {
        this.wearableEquipped = wearableEquipped;
    }

    @Override
    public WearableAbilityTestStateComponent clone() {
        WearableAbilityTestStateComponent copy = new WearableAbilityTestStateComponent();
        copy.wearableEquipped = wearableEquipped;
        return copy;
    }
}
