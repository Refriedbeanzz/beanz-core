package com.beanz.core.ui;

import com.beanz.core.skills.SkillType;
import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LevelUpNotificationService {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final long DISPLAY_DURATION_MS = 2600L;
    private final Map<PlayerRef, LevelUpNotificationHud> huds = new ConcurrentHashMap<>();
    private final Map<PlayerRef, ScheduledFuture<?>> pendingHides = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "beanz-levelup-hud");
        thread.setDaemon(true);
        return thread;
    });

    public void notifyLevelUp(Player player, SkillType skillType, int level) {
        if (player == null || player.getPlayerRef() == null) {
            return;
        }

        PlayerRef playerRef = player.getPlayerRef();
        LevelUpNotificationHud hud = huds.computeIfAbsent(playerRef, ref -> {
            LevelUpNotificationHud created = new LevelUpNotificationHud(ref);
            player.getHudManager().setCustomHud(ref, created);
            created.show();
            return created;
        });

        cancelPendingHide(playerRef);
        hud.showNotification(skillType.name().charAt(0) + skillType.name().substring(1).toLowerCase() + " increased to Level " + level, "Keep training to unlock stronger passive bonuses.");
        hud.pushUpdate();

        LOGGER.atInfo().log(
            "Showing level-up notification for %s: skill=%s, level=%s",
            playerRef,
            skillType,
            level
        );

        ScheduledFuture<?> hideFuture = scheduler.schedule(() -> {
            hud.hideNotification();
            hud.pushUpdate();
        }, DISPLAY_DURATION_MS, TimeUnit.MILLISECONDS);
        pendingHides.put(playerRef, hideFuture);
    }

    private void cancelPendingHide(PlayerRef playerRef) {
        ScheduledFuture<?> existing = pendingHides.remove(playerRef);
        if (existing != null) {
            existing.cancel(false);
        }
    }
}
