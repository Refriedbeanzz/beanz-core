package com.beanz.core.skills;

import java.util.EnumMap;
import java.util.Map;

public class SkillRewardService {
    private static final int MAX_LEVEL = 100;
    private static final int DOUBLE_JUMP_UNLOCK_LEVEL = 60;
    private static final int WALL_JUMP_UNLOCK_LEVEL = 80;
    private static final double MAX_JUMP_MULTIPLIER = 1.5;
    private static final double MAX_FALL_DAMAGE_REDUCTION = 1.0;
    private static final double MIN_JUMP_STAMINA_COST = 2.0;
    private static final double BASE_JUMP_STAMINA_COST = 5.5;
    private static final double JUMP_STAMINA_DISCOUNT_PER_LEVEL = 0.05;
    private static final double DOUBLE_JUMP_FORCE_SCALE = 0.9;
    private static final double SKY_LEAP_FORCE_SCALE = 0.6;
    private static final double WALL_JUMP_VERTICAL_FORCE_SCALE = 0.85;
    private static final double WALL_JUMP_HORIZONTAL_FORCE = 7.5;

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

    public double getJumpStaminaCost(PlayerSkillsComponent skills) {
        int level = skills.getLevel(SkillType.JUMP);
        return Math.max(MIN_JUMP_STAMINA_COST, BASE_JUMP_STAMINA_COST - (JUMP_STAMINA_DISCOUNT_PER_LEVEL * level));
    }

    public boolean hasDoubleJumpUnlocked(PlayerSkillsComponent skills) {
        return skills.getLevel(SkillType.JUMP) >= DOUBLE_JUMP_UNLOCK_LEVEL;
    }

    public boolean hasWallJumpUnlocked(PlayerSkillsComponent skills) {
        return skills.getLevel(SkillType.JUMP) >= WALL_JUMP_UNLOCK_LEVEL;
    }

    public double getDoubleJumpForceScale() {
        return DOUBLE_JUMP_FORCE_SCALE;
    }

    public double getSkyLeapForceScale() {
        return SKY_LEAP_FORCE_SCALE;
    }

    public double getWallJumpVerticalForceScale() {
        return WALL_JUMP_VERTICAL_FORCE_SCALE;
    }

    public double getWallJumpHorizontalForce(PlayerSkillsComponent skills) {
        return WALL_JUMP_HORIZONTAL_FORCE * (0.85 + (0.15 * normalizedProgress(skills.getLevel(SkillType.JUMP))));
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
