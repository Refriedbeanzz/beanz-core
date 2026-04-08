package com.beanz.core.skills;

import java.util.EnumMap;
import java.util.Map;

public class SkillRewardService {
    private static final int MAX_LEVEL = 100;
    private static final double MAX_JUMP_MULTIPLIER = 1.0 + ((2.45 - 1.0) * (49.0 / 99.0));
    private static final double MAX_FALL_DAMAGE_REDUCTION = 0.50;

    public double getTotalReward(PlayerSkillsComponent skills, RewardType rewardType) {
        return getRewardsForSkill(skills, SkillType.JUMP).getOrDefault(rewardType, defaultValueFor(rewardType));
    }

    public Map<RewardType, Double> getRewardsForSkill(PlayerSkillsComponent skills, SkillType skillType) {
        int level = skills.getLevel(skillType);
        Map<RewardType, Double> rewards = new EnumMap<>(RewardType.class);

        switch (skillType) {
            case JUMP -> {
                rewards.put(RewardType.JUMP_FORCE, calculateJumpMultiplier(level));
                rewards.put(RewardType.FALL_DAMAGE_REDUCTION, calculateFallDamageReduction(level));
            }
        }

        return rewards;
    }

    public double getJumpMultiplier(PlayerSkillsComponent skills) {
        return getTotalReward(skills, RewardType.JUMP_FORCE);
    }

    public double getFallDamageReduction(PlayerSkillsComponent skills) {
        return getTotalReward(skills, RewardType.FALL_DAMAGE_REDUCTION);
    }

    public double getFallDamageMultiplier(PlayerSkillsComponent skills) {
        return 1.0 - getFallDamageReduction(skills);
    }

    public double getFallDamageReductionPercent(PlayerSkillsComponent skills) {
        return getFallDamageReduction(skills) * 100.0;
    }

    private double calculateJumpMultiplier(int level) {
        double progression = normalizedProgress(level);
        return 1.0 + ((MAX_JUMP_MULTIPLIER - 1.0) * progression);
    }

    private double calculateFallDamageReduction(int level) {
        return MAX_FALL_DAMAGE_REDUCTION * normalizedProgress(level);
    }

    private double normalizedProgress(int level) {
        int clampedLevel = Math.max(1, Math.min(MAX_LEVEL, level));
        return (clampedLevel - 1) / (double) (MAX_LEVEL - 1);
    }

    private double defaultValueFor(RewardType rewardType) {
        return switch (rewardType) {
            case JUMP_FORCE -> 1.0;
            case FALL_DAMAGE_REDUCTION -> 0.0;
        };
    }
}
