package com.beanz.core.abilities;

import com.beanz.core.BeanzCoreMod;
import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class AbilityInputPacketWatcher implements PlayerPacketWatcher {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();

    @Override
    public void accept(PlayerRef playerRef, Packet packet) {
        if (!(packet instanceof SyncInteractionChains syncInteractionChains)) {
            return;
        }

        int updateCount = syncInteractionChains.updates != null ? syncInteractionChains.updates.length : 0;
        LOGGER.atInfo().log(
            "[BeanzCore][InputDebug] SyncInteractionChains received for %s updates=%s",
            playerRef != null ? playerRef.getUsername() : "unknown-player",
            updateCount
        );

        if (syncInteractionChains.updates == null) {
            return;
        }

        for (SyncInteractionChain update : syncInteractionChains.updates) {
            if (update == null) {
                continue;
            }

            LOGGER.atInfo().log(
                "[BeanzCore][InputDebug] interactionType=%s player=%s",
                update.interactionType,
                playerRef != null ? playerRef.getUsername() : "unknown-player"
            );

            if (update.interactionType != InteractionType.Ability3) {
                continue;
            }

            LOGGER.atInfo().log(
                "[BeanzCore][Input] Ability3 detected for %s",
                playerRef != null ? playerRef.getUsername() : "unknown-player"
            );
            BeanzCoreMod.getInstance().getAbilityManager().useSkyLeap(playerRef);
        }
    }
}
