package com.beanz.core.abilities;

import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class WearableAbilityTestSystem extends EntityTickingSystem<EntityStore> {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final String WEARABLE_TEST_ITEM_ID = "Beanz_Ability3_Wearable_Test";

    @Override
    public Query<EntityStore> getQuery() {
        return new AndQuery<>(
            Player.getComponentType(),
            InventoryComponent.Armor.getComponentType(),
            WearableAbilityTestStateComponent.getComponentType()
        );
    }

    @Override
    public void tick(
        float dt,
        int index,
        ArchetypeChunk<EntityStore> chunk,
        Store<EntityStore> store,
        CommandBuffer<EntityStore> commandBuffer
    ) {
        Player player = chunk.getComponent(index, Player.getComponentType());
        InventoryComponent.Armor armor = chunk.getComponent(index, InventoryComponent.Armor.getComponentType());
        WearableAbilityTestStateComponent state = chunk.getComponent(index, WearableAbilityTestStateComponent.getComponentType());

        boolean equipped = isWearableEquipped(armor);
        if (equipped == state.isWearableEquipped()) {
            return;
        }

        state.setWearableEquipped(equipped);
        String username = player != null && player.getPlayerRef() != null ? player.getPlayerRef().getUsername() : "unknown-player";
        if (equipped) {
            LOGGER.atInfo().log("[BeanzCore][WearableTest] equipped for %s", username);
        } else {
            LOGGER.atInfo().log("[BeanzCore][WearableTest] unequipped for %s", username);
        }
    }

    private boolean isWearableEquipped(InventoryComponent.Armor armor) {
        if (armor == null) {
            return false;
        }

        ItemContainer inventory = armor.getInventory();
        if (inventory == null) {
            return false;
        }

        short capacity = inventory.getCapacity();
        for (short slot = 0; slot < capacity; slot++) {
            ItemStack itemStack = inventory.getItemStack(slot);
            if (itemStack == null || itemStack.isEmpty()) {
                continue;
            }
            if (WEARABLE_TEST_ITEM_ID.equals(itemStack.getItemId())) {
                return true;
            }
        }

        return false;
    }
}
