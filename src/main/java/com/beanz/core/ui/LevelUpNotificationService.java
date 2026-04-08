package com.beanz.core.ui;

import com.beanz.core.skills.SkillType;
import com.beanz.core.skills.PlayerSkillsComponent;
import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LevelUpNotificationService {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private final Map<PlayerRef, Map<SkillType, Integer>> lastShownLevels = new ConcurrentHashMap<>();

    public void notifyLevelUp(Player player, PlayerSkillsComponent skills, SkillType skillType, int level) {
        if (player == null || player.getPlayerRef() == null) {
            LOGGER.atWarning().log(
                "Skipping level-up notification because player or PlayerRef is unavailable (skill=%s, level=%s)",
                skillType,
                level
            );
            return;
        }

        PlayerRef playerRef = player.getPlayerRef();
        if (skills != null && !skills.isNotificationEnabled(skillType)) {
            LOGGER.atInfo().log(
                "Level-up notification suppressed for %s: skill=%s, level=%s (notifications disabled)",
                playerRef,
                skillType,
                level
            );
            return;
        }

        Map<SkillType, Integer> playerLevels = lastShownLevels.computeIfAbsent(playerRef, ref -> new ConcurrentHashMap<>());
        Integer lastShownLevel = playerLevels.get(skillType);
        if (lastShownLevel != null && lastShownLevel == level) {
            LOGGER.atInfo().log(
                "Skipping duplicate level-up notification for %s: skill=%s, level=%s",
                playerRef,
                skillType,
                level
            );
            return;
        }

        PacketHandler packetHandler = player.getPlayerConnection();
        if (packetHandler == null) {
            LOGGER.atWarning().log(
                "Unable to display level-up notification for %s: skill=%s, level=%s because PacketHandler is null",
                playerRef,
                skillType,
                level
            );
            return;
        }

        playerLevels.put(skillType, level);
        String skillName = skillType.name().charAt(0) + skillType.name().substring(1).toLowerCase(Locale.ROOT);
        String title = skillName + " increased to Level " + level;
        String subtitle = "Keep training to unlock stronger passive bonuses.";

        NotificationUtil.sendNotification(
            packetHandler,
            Message.raw(title).color("#6fd0ff").bold(true),
            Message.raw(subtitle).color("#d8e4f2")
        );

        LOGGER.atInfo().log(
            "Displaying level-up notification for %s: skill=%s, level=%s, title=%s",
            playerRef,
            skillType,
            level,
            title
        );
    }
}
