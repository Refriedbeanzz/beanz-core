package com.beanz.core;

import com.beanz.core.abilities.PlayerAbilityData;
import com.beanz.core.skills.PlayerSkillsComponent;
import com.beanz.core.skills.SkillLevelTable;
import com.beanz.core.skills.SkillType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SetJumpLevelCommand extends AbstractPlayerCommand {

    public SetJumpLevelCommand() {
        super("beanzlevel", "Set skill level: /beanzlevel <level> OR /beanzlevel <player> <skill> <level>");
    }

    @Override
    protected void execute(
        CommandContext context,
        Store<EntityStore> store,
        Ref<EntityStore> ref,
        PlayerRef playerRef,
        World world
    ) {
        String input = context.getInputString();
        String[] tokens = input != null ? input.trim().split("\\s+") : new String[0];

        // /beanzlevel <level>
        if (tokens.length == 2) {
            int level = parseLevel(tokens[1]);
            if (level < 0) return;
            setSkillLevel(playerRef, store, ref, SkillType.JUMP, level);
            return;
        }

        // /beanzlevel <player> <skill> <level>
        if (tokens.length == 4) {
            String targetName = tokens[1];
            String skillName = tokens[2].toUpperCase();
            int level = parseLevel(tokens[3]);
            if (level < 0) return;

            SkillType skillType;
            try {
                skillType = SkillType.valueOf(skillName);
            } catch (IllegalArgumentException e) {
                BeanzCoreMod.getInstance().getLogger().atWarning().log(
                    "[BeanzCore][Admin] /beanzlevel: unknown skill '%s'. Valid: %s",
                    skillName,
                    java.util.Arrays.toString(SkillType.values())
                );
                return;
            }

            // probe World for player lookup
            // world.getPlayerRefs() returns a collection — find by username
            PlayerRef targetRef = null;
            for (PlayerRef pr : world.getPlayerRefs()) {
                if (pr != null && targetName.equalsIgnoreCase(pr.getUsername())) {
                    targetRef = pr;
                    break;
                }
            }

            if (targetRef == null) {
                BeanzCoreMod.getInstance().getLogger().atWarning().log(
                    "[BeanzCore][Admin] /beanzlevel: player '%s' not found or not online",
                    targetName
                );
                return;
            }

            var holder = targetRef.getHolder();
            if (holder == null) {
                BeanzCoreMod.getInstance().getLogger().atWarning().log(
                    "[BeanzCore][Admin] /beanzlevel: could not resolve holder for '%s'",
                    targetName
                );
                return;
            }

            PlayerSkillsComponent skills = BeanzCoreMod.getInstance().getOrCreateSkills(targetRef, holder);
            int xp = SkillLevelTable.getXpRequiredForLevel(level);
            setSkillLevelOnComponent(skills, skillType, level, xp);

            PlayerAbilityData abilityData = BeanzCoreMod.getInstance().getAbilityManager().getOrCreate(targetRef, holder);
            BeanzCoreMod.getInstance().syncAbilityUnlocks(targetRef, skills, abilityData);

            BeanzCoreMod.getInstance().getLogger().atInfo().log(
                "[BeanzCore][Admin] Set %s level for %s to %s (xp=%s) by %s",
                skillType,
                targetName,
                level,
                xp,
                playerRef != null ? playerRef.getUsername() : "unknown"
            );
            return;
        }

        BeanzCoreMod.getInstance().getLogger().atWarning().log(
            "[BeanzCore][Admin] Usage: /beanzlevel <1-100>  OR  /beanzlevel <player> <skill> <level>"
        );
    }

    private int parseLevel(String token) {
        try {
            int level = Integer.parseInt(token.trim());
            return Math.max(1, Math.min(100, level));
        } catch (NumberFormatException e) {
            BeanzCoreMod.getInstance().getLogger().atWarning().log(
                "[BeanzCore][Admin] /beanzlevel: invalid level '%s', must be a number 1-100", token
            );
            return -1;
        }
    }

    private void setSkillLevel(PlayerRef playerRef, Store<EntityStore> store, Ref<EntityStore> ref, SkillType skillType, int level) {
        int xp = SkillLevelTable.getXpRequiredForLevel(level);
        PlayerSkillsComponent skills = BeanzCoreMod.getInstance().getOrCreateSkills(playerRef, store, ref);
        setSkillLevelOnComponent(skills, skillType, level, xp);

        PlayerAbilityData abilityData = BeanzCoreMod.getInstance().getAbilityManager().getOrCreate(playerRef, store, ref);
        BeanzCoreMod.getInstance().syncAbilityUnlocks(playerRef, skills, abilityData);

        BeanzCoreMod.getInstance().getLogger().atInfo().log(
            "[BeanzCore][Admin] Set %s level for %s to %s (xp=%s)",
            skillType,
            playerRef != null ? playerRef.getUsername() : "unknown",
            level,
            xp
        );
    }

    private void setSkillLevelOnComponent(PlayerSkillsComponent skills, SkillType skillType, int level, int xp) {
        switch (skillType) {
            case JUMP -> {
                skills.setJumpLevel(level);
                skills.setJumpXp(xp);
            }
        }
    }
}
