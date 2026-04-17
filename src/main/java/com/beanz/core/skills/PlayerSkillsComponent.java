package com.beanz.core.skills;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class PlayerSkillsComponent implements Component<EntityStore> {
    public static final BuilderCodec<PlayerSkillsComponent> CODEC = BuilderCodec
        .builder(PlayerSkillsComponent.class, PlayerSkillsComponent::new)
        .append(new KeyedCodec<>("JumpXp", Codec.INTEGER), PlayerSkillsComponent::setJumpXp, PlayerSkillsComponent::getJumpXp)
        .add()
        .append(new KeyedCodec<>("JumpLevel", Codec.INTEGER), PlayerSkillsComponent::setJumpLevel, PlayerSkillsComponent::getJumpLevel)
        .add()
        .append(
            new KeyedCodec<>("JumpNotificationsEnabled", Codec.BOOLEAN),
            PlayerSkillsComponent::setJumpNotificationsEnabled,
            PlayerSkillsComponent::isJumpNotificationsEnabled
        )
        .add()
        .append(new KeyedCodec<>("RunningXp", Codec.INTEGER), PlayerSkillsComponent::setRunningXp, PlayerSkillsComponent::getRunningXp)
        .add()
        .append(new KeyedCodec<>("RunningLevel", Codec.INTEGER), PlayerSkillsComponent::setRunningLevel, PlayerSkillsComponent::getRunningLevel)
        .add()
        .build();

    private static ComponentType<EntityStore, PlayerSkillsComponent> componentType;

    private int jumpXp;
    private int jumpLevel = 1;
    private boolean jumpNotificationsEnabled = true;
    private int runningXp;
    private int runningLevel = 1;

    public static ComponentType<EntityStore, PlayerSkillsComponent> getComponentType() {
        return componentType;
    }

    public static void setComponentType(ComponentType<EntityStore, PlayerSkillsComponent> componentType) {
        PlayerSkillsComponent.componentType = componentType;
    }

    public int getXp(SkillType skillType) {
        return switch (skillType) {
            case JUMP -> jumpXp;
            case RUNNING -> runningXp;
        };
    }

    public void setXp(SkillType skillType, int xp) {
        switch (skillType) {
            case JUMP -> jumpXp = xp;
            case RUNNING -> runningXp = xp;
        }
    }

    public int getLevel(SkillType skillType) {
        return switch (skillType) {
            case JUMP -> jumpLevel;
            case RUNNING -> runningLevel;
        };
    }

    public void setLevel(SkillType skillType, int level) {
        switch (skillType) {
            case JUMP -> jumpLevel = level;
            case RUNNING -> runningLevel = level;
        }
    }

    public void addXp(SkillType skillType, int amount) {
        setXp(skillType, getXp(skillType) + amount);
        setLevel(skillType, SkillLevelTable.getLevelForXp(getXp(skillType)));
    }

    public boolean isNotificationEnabled(SkillType skillType) {
        return switch (skillType) {
            case JUMP -> jumpNotificationsEnabled;
            case RUNNING -> true;
        };
    }

    public void setNotificationEnabled(SkillType skillType, boolean enabled) {
        switch (skillType) {
            case JUMP -> jumpNotificationsEnabled = enabled;
            case RUNNING -> {}
        }
    }

    public SkillSnapshot snapshot(SkillType skillType) {
        return SkillSnapshot.from(skillType, getXp(skillType), getLevel(skillType));
    }

    public int getJumpXp() {
        return jumpXp;
    }

    public void setJumpXp(int jumpXp) {
        this.jumpXp = jumpXp;
    }

    public int getJumpLevel() {
        return jumpLevel;
    }

    public void setJumpLevel(int jumpLevel) {
        this.jumpLevel = jumpLevel;
    }

    public boolean isJumpNotificationsEnabled() {
        return jumpNotificationsEnabled;
    }

    public void setJumpNotificationsEnabled(boolean jumpNotificationsEnabled) {
        this.jumpNotificationsEnabled = jumpNotificationsEnabled;
    }

    public int getRunningXp() {
        return runningXp;
    }

    public void setRunningXp(int runningXp) {
        this.runningXp = runningXp;
    }

    public int getRunningLevel() {
        return runningLevel;
    }

    public void setRunningLevel(int runningLevel) {
        this.runningLevel = runningLevel;
    }

    @Override
    public PlayerSkillsComponent clone() {
        PlayerSkillsComponent copy = new PlayerSkillsComponent();
        copy.jumpXp = this.jumpXp;
        copy.jumpLevel = this.jumpLevel;
        copy.jumpNotificationsEnabled = this.jumpNotificationsEnabled;
        copy.runningXp = this.runningXp;
        copy.runningLevel = this.runningLevel;
        return copy;
    }
}
