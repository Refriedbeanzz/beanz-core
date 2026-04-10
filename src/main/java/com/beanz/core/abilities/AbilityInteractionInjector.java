package com.beanz.core.abilities;

import com.beanz.core.BeanzCoreMod;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;

public class AbilityInteractionInjector {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final Field interactionsField;
    private final Field cachedPacketField;
    private boolean injected;

    public AbilityInteractionInjector() {
        try {
            interactionsField = Item.class.getDeclaredField("interactions");
            interactionsField.setAccessible(true);
            cachedPacketField = Item.class.getDeclaredField("cachedPacket");
            cachedPacketField.setAccessible(true);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to prepare Ability3 item interaction injection", exception);
        }
    }

    public void onItemsLoaded(LoadedAssetsEvent<?, ?, ?> event) {
        if (event == null || event.getAssetClass() == null || !Item.class.isAssignableFrom(event.getAssetClass())) {
            return;
        }

        if (injected) {
            return;
        }

        int modifiedCount = 0;
        for (Object value : event.getLoadedAssets().values()) {
            if (!(value instanceof Item item)) {
                continue;
            }
            try {
                @SuppressWarnings("unchecked")
                Map<InteractionType, String> currentInteractions =
                    (Map<InteractionType, String>) interactionsField.get(item);
                Map<InteractionType, String> updatedInteractions = currentInteractions == null
                    ? new EnumMap<>(InteractionType.class)
                    : new EnumMap<>(currentInteractions);

                String currentId = updatedInteractions.get(InteractionType.Ability3);
                if (BeanzCoreMod.TEST_ABILITY3_ROOT_INTERACTION_ID.equals(currentId)) {
                    continue;
                }

                updatedInteractions.put(InteractionType.Ability3, BeanzCoreMod.TEST_ABILITY3_ROOT_INTERACTION_ID);
                interactionsField.set(item, updatedInteractions);
                cachedPacketField.set(item, null);
                modifiedCount++;
            } catch (ReflectiveOperationException exception) {
                LOGGER.atWarning().withCause(exception).log(
                    "[BeanzCore][InputDebug] Failed to inject Ability3 into item %s",
                    item.getId()
                );
            }
        }

        injected = true;
        LOGGER.atInfo().log(
            "[BeanzCore][InputDebug] Injected Ability3 root interaction into %s item assets",
            modifiedCount
        );
    }
}
