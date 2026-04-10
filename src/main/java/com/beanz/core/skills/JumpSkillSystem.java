package com.beanz.core.skills;

import com.beanz.core.BeanzCoreMod;
import com.beanz.core.abilities.PlayerAbilityData;
import com.google.common.flogger.FluentLogger;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.MovementSettings;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.collision.BlockCollisionData;
import com.hypixel.hytale.server.core.modules.collision.CollisionResult;
import com.hypixel.hytale.server.core.modules.entity.component.CollisionResultComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.stamina.SprintStaminaRegenDelay;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;

public class JumpSkillSystem extends EntityTickingSystem<EntityStore> {
    private static final FluentLogger LOGGER = FluentLogger.forEnclosingClass();
    private static final int XP_PER_JUMP = 1;
    private static final double EPSILON = 0.0001;
    private static final double WALL_NORMAL_Y_LIMIT = 0.35;
    private static final String STAMINA_STAT_ID = "stamina";
    private static final int WALL_CONTACT_GRACE_TICKS = 6;
    private static final int BUFFERED_DOUBLE_JUMP_PRESS_TICKS = 6;

    @Override
    public Query<EntityStore> getQuery() {
        return new AndQuery<>(
            Player.getComponentType(),
            MovementStatesComponent.getComponentType(),
            MovementManager.getComponentType(),
            CollisionResultComponent.getComponentType(),
            TransformComponent.getComponentType(),
            PlayerInput.getComponentType(),
            EntityStatMap.getComponentType(),
            PlayerSkillsComponent.getComponentType(),
            PlayerAbilityData.getComponentType(),
            JumpAbilityStateComponent.getComponentType(),
            Velocity.getComponentType()
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
        MovementStatesComponent movementStatesComponent = chunk.getComponent(index, MovementStatesComponent.getComponentType());
        MovementStates current = movementStatesComponent.getMovementStates();
        MovementStates previous = movementStatesComponent.getSentMovementStates();

        Ref<EntityStore> ref = chunk.getReferenceTo(index);
        Player player = chunk.getComponent(index, Player.getComponentType());
        MovementManager movementManager = chunk.getComponent(index, MovementManager.getComponentType());
        CollisionResultComponent collisionResultComponent = chunk.getComponent(index, CollisionResultComponent.getComponentType());
        TransformComponent transformComponent = chunk.getComponent(index, TransformComponent.getComponentType());
        PlayerInput playerInput = chunk.getComponent(index, PlayerInput.getComponentType());
        EntityStatMap statMap = chunk.getComponent(index, EntityStatMap.getComponentType());
        PlayerSkillsComponent skills = chunk.getComponent(index, PlayerSkillsComponent.getComponentType());
        PlayerAbilityData abilityData = chunk.getComponent(index, PlayerAbilityData.getComponentType());
        JumpAbilityStateComponent jumpState = chunk.getComponent(index, JumpAbilityStateComponent.getComponentType());
        Velocity velocity = chunk.getComponent(index, Velocity.getComponentType());
        SkillService skillService = BeanzCoreMod.getInstance().getSkillService();

        if (player == null || skills == null || abilityData == null || jumpState == null || movementManager == null || statMap == null || velocity == null) {
            return;
        }

        BeanzCoreMod.getInstance().syncAbilityUnlocks(player.getPlayerRef(), skills, abilityData);
        boolean wasGroundedLastTick = jumpState.wasGrounded();
        boolean jumpWasPressedLastTick = jumpState.wasJumpPressedLastTick();
        resetAirAbilitiesIfLanded(ref, current, jumpState);
        jumpState.trackAirborneState(current.onGround);
        boolean jumpCurrentlyPressed = current.jumping;
        boolean movementJumpEdgeDetected = current.jumping && !previous.jumping;
        boolean jumpRisingEdge = jumpCurrentlyPressed && !jumpWasPressedLastTick;
        boolean queuedJumpPressDetected = hasQueuedJumpInput(playerInput);
        boolean queuedJumpReleaseDetected = hasQueuedJumpRelease(playerInput);
        boolean newJumpPress = jumpRisingEdge || movementJumpEdgeDetected || queuedJumpPressDetected;
        boolean jumpInputDetected = newJumpPress;
        boolean justLeftGround = !current.onGround && wasGroundedLastTick;
        boolean hasLeftGroundSinceInitialJump = jumpState.hasLeftGroundSinceInitialJump() || justLeftGround;

        if (justLeftGround && !jumpState.hasLeftGroundSinceInitialJump()) {
            jumpState.setHasLeftGroundSinceInitialJump(true);
            LOGGER.atInfo().log(
                "Player left ground for %s: airTicks=%s, jumpPressed=%s",
                ref,
                jumpState.getAirTicks(),
                jumpCurrentlyPressed
            );
        }

        boolean jumpReleasedNow = !jumpCurrentlyPressed || queuedJumpReleaseDetected;
        if (!current.onGround && jumpState.hasLeftGroundSinceInitialJump() && jumpReleasedNow) {
            if (!jumpState.hasJumpReleasedSinceGroundJump()) {
                LOGGER.atInfo().log(
                    "Jump released after takeoff for %s: jumpPressed=%s, queuedJumpReleaseDetected=%s",
                    ref,
                    jumpCurrentlyPressed,
                    queuedJumpReleaseDetected
                );
            }
            jumpState.setJumpReleasedSinceGroundJump(true);
        }
        boolean groundedJump = newJumpPress
            && !hasLeftGroundSinceInitialJump
            && (current.onGround || wasGroundedLastTick);

        if (!jumpInputDetected) {
            jumpState.setWasGrounded(current.onGround);
            jumpState.setJumpWasPressedLastTick(jumpCurrentlyPressed);
            return;
        }

        int jumpLevel = skills.getLevel(SkillType.JUMP);
        SkillRewardService rewardService = skillService.getRewardService();
        MovementSettings settings = movementManager.getSettings();
        MovementSettings defaultSettings = movementManager.getDefaultSettings();
        double baseJumpForce = defaultSettings.jumpForce;
        double multiplier = rewardService.getJumpMultiplier(skills);
        double previousConfiguredJumpForce = settings.jumpForce;
        double fullJumpForce = baseJumpForce * multiplier;
        boolean airborne = !current.onGround;
        boolean nearWall = false;
        int wallContactTicks = 0;
        JumpAbilityType abilityType = groundedJump ? JumpAbilityType.GROUND : null;
        String actionChosen = abilityType != null ? abilityType.name() : "NONE";

        LOGGER.atInfo().log(
            "Jump ability input for %s: jumpPressed=%s, jumpPressedLastTick=%s, movementJumpEdgeDetected=%s, queuedJumpPressDetected=%s, queuedJumpReleaseDetected=%s, newJumpPress=%s, jumpInputDetected=%s, hasLeftGroundSinceInitialJump=%s, jumpReleasedSinceTakeoff=%s, airborne=%s, onGround=%s, skyLeapUnlocked=%s, usedSkyLeapThisAirtime=%s, nearWall=%s, wallContactTicks=%s, actionChosen=%s",
            ref,
            jumpCurrentlyPressed,
            jumpWasPressedLastTick,
            movementJumpEdgeDetected,
            queuedJumpPressDetected,
            queuedJumpReleaseDetected,
            newJumpPress,
            jumpInputDetected,
            hasLeftGroundSinceInitialJump,
            jumpState.hasJumpReleasedSinceGroundJump(),
            airborne,
            current.onGround,
            abilityData.isUnlocked(com.beanz.core.abilities.AbilityType.SKY_LEAP),
            jumpState.hasUsedSkyLeapThisAirtime(),
            nearWall,
            wallContactTicks,
            actionChosen
        );
        if (!groundedJump && abilityType == null) {
            LOGGER.atInfo().log(
                "Air jump attempt ignored for %s: newJumpPress=%s, airborne=%s, onGround=%s, hasLeftGroundSinceInitialJump=%s, jumpReleasedSinceTakeoff=%s, usedSkyLeapThisAirtime=%s, skyLeapUnlocked=%s, actionChosen=%s, blockedReason=air_ability_handled_by_ability3",
                ref,
                newJumpPress,
                airborne,
                current.onGround,
                hasLeftGroundSinceInitialJump,
                jumpState.hasJumpReleasedSinceGroundJump(),
                jumpState.hasUsedSkyLeapThisAirtime(),
                abilityData.isUnlocked(com.beanz.core.abilities.AbilityType.SKY_LEAP),
                actionChosen
            );
            jumpState.setWasGrounded(current.onGround);
            jumpState.setJumpWasPressedLastTick(jumpCurrentlyPressed);
            return;
        }
        EntityStatValue staminaValue = statMap.get(STAMINA_STAT_ID);
        SprintStaminaRegenDelay staminaRegenDelay =
            (SprintStaminaRegenDelay) store.getResource(SprintStaminaRegenDelay.getResourceType());
        double staminaCost = rewardService.getJumpStaminaCost(skills);
        double staminaBefore = staminaValue != null ? staminaValue.get() : -1.0;
        boolean staminaDelayActive = staminaRegenDelay != null && staminaRegenDelay.hasDelay();
        boolean exhaustedRecoveryActive = staminaDelayActive && staminaBefore <= EPSILON;
        double staminaRatio = staminaValue == null || staminaCost <= 0.0
            ? 1.0
            : Math.min(1.0, Math.max(0.0, staminaBefore / staminaCost));
        double jumpRatio = exhaustedRecoveryActive ? 0.0 : staminaRatio;
        boolean bonusJumpAllowed = jumpRatio > 0.0 && abilityType != null;
        String blockedReason = exhaustedRecoveryActive
            ? "stamina_recovery"
            : abilityType == null
                ? "air_ability_unavailable"
                : (staminaBefore <= EPSILON ? "zero_stamina" : "none");
        double appliedJumpForce = calculateAppliedJumpForce(baseJumpForce, fullJumpForce, jumpRatio, abilityType, rewardService);

        if (staminaValue == null) {
            LOGGER.atWarning().log(
                "Jump stamina stat '%s' was not found for %s. Skipping stamina consumption and bonus gating.",
                STAMINA_STAT_ID,
                ref
            );
        } else {
            float appliedCost = (float) Math.min(staminaBefore, staminaCost);
            float newStamina = statMap.subtractStatValue(staminaValue.getIndex(), appliedCost);
            statMap.update();

            LOGGER.atInfo().log(
                "Jump stamina used for %s: level=%s, staminaBefore=%.3f, staminaCost=%.3f, staminaAfter=%.3f, staminaDelayActive=%s, exhaustedRecoveryActive=%s, bonusJumpAllowed=%s, jumpRatio=%.3f, finalJumpForce=%.3f, blockedReason=%s",
                ref,
                jumpLevel,
                staminaBefore,
                staminaCost,
                newStamina,
                staminaDelayActive,
                exhaustedRecoveryActive,
                bonusJumpAllowed,
                jumpRatio,
                appliedJumpForce,
                blockedReason
            );

            if (exhaustedRecoveryActive) {
                LOGGER.atInfo().log(
                    "Jump bonus suppressed by stamina recovery for %s: level=%s, rawStamina=%.3f, staminaDelayActive=%s, exhaustedRecoveryActive=%s, bonusJumpAllowed=%s, jumpRatio=%.3f, finalJumpForce=%.3f, blockedReason=%s",
                    ref,
                    jumpLevel,
                    staminaBefore,
                    staminaDelayActive,
                    exhaustedRecoveryActive,
                    bonusJumpAllowed,
                    jumpRatio,
                    appliedJumpForce,
                    blockedReason
                );
            } else if (staminaBefore <= 0.0) {
                LOGGER.atInfo().log(
                    "Jump has zero stamina for %s: level=%s, staminaBefore=%.3f, staminaDelayActive=%s, exhaustedRecoveryActive=%s, bonusJumpAllowed=%s, staminaCost=%.3f, jumpRatio=%.3f, finalJumpForce=%.3f, blockedReason=%s",
                    ref,
                    jumpLevel,
                    staminaBefore,
                    staminaDelayActive,
                    exhaustedRecoveryActive,
                    bonusJumpAllowed,
                    staminaCost,
                    jumpRatio,
                    appliedJumpForce,
                    blockedReason
                );
            }
        }

        if (abilityType == JumpAbilityType.GROUND) {
            jumpState.markGroundJumpStarted();
            LOGGER.atInfo().log(
                "Initial ground jump registered for %s",
                ref
            );
        }

        if (abilityType == JumpAbilityType.GROUND && Math.abs(previousConfiguredJumpForce - appliedJumpForce) > EPSILON) {
            settings.jumpForce = (float) appliedJumpForce;
            movementManager.update(player.getPlayerConnection());

            LOGGER.atInfo().log(
                "Adjusted runtime MovementManager.settings.jumpForce for %s: ability=%s, before=%.3f, after=%.3f, level=%s, multiplier=%.3f, staminaDelayActive=%s, exhaustedRecoveryActive=%s, bonusJumpAllowed=%s, jumpRatio=%.3f, blockedReason=%s",
                ref,
                abilityType,
                previousConfiguredJumpForce,
                appliedJumpForce,
                jumpLevel,
                multiplier,
                staminaDelayActive,
                exhaustedRecoveryActive,
                bonusJumpAllowed,
                jumpRatio,
                blockedReason
            );
        }

        SkillProgressionResult progression = skillService.awardXp(skills, SkillType.JUMP, XP_PER_JUMP);
        BeanzCoreMod.getInstance().syncAbilityUnlocks(player.getPlayerRef(), skills, abilityData);

        if (progression.newLevel() > progression.previousLevel()) {
            LOGGER.atInfo().log(
                "Level-up detected for %s: skill=%s, previousLevel=%s, newLevel=%s",
                ref,
                SkillType.JUMP,
                progression.previousLevel(),
                progression.newLevel()
            );

            com.beanz.core.BeanzCoreMod.getInstance()
                .getLevelUpNotificationService()
                .notifyLevelUp(player, skills, SkillType.JUMP, progression.newLevel());
        }

        LOGGER.atInfo().log(
            "Jump XP awarded for %s: previousXp=%s, newXp=%s, currentLevel=%s, ability=%s, rawStamina=%.3f, staminaDelayActive=%s, exhaustedRecoveryActive=%s, bonusJumpAllowed=%s, MovementManager.settings.jumpForce=%.3f -> %.3f, multiplier=%.3f, jumpRatio=%.3f, blockedReason=%s",
            ref,
            progression.previousXp(),
            progression.newXp(),
            progression.newLevel(),
            abilityType,
            staminaBefore,
            staminaDelayActive,
            exhaustedRecoveryActive,
            bonusJumpAllowed,
            previousConfiguredJumpForce,
            appliedJumpForce,
            multiplier,
            jumpRatio,
            blockedReason
        );

        jumpState.setWasGrounded(current.onGround);
        jumpState.setJumpWasPressedLastTick(jumpCurrentlyPressed);
    }

    private void logAbilityUnlocks(
        Ref<EntityStore> ref,
        PlayerSkillsComponent skills,
        SkillRewardService rewardService,
        JumpAbilityStateComponent jumpState
    ) {
        if (rewardService.hasDoubleJumpUnlocked(skills) && !jumpState.isDoubleJumpUnlockLogged()) {
            jumpState.setDoubleJumpUnlockLogged(true);
            LOGGER.atInfo().log(
                "Double jump unlocked for %s at Jump level %s",
                ref,
                skills.getLevel(SkillType.JUMP)
            );
        }

        if (rewardService.hasWallJumpUnlocked(skills) && !jumpState.isWallJumpUnlockLogged()) {
            jumpState.setWallJumpUnlockLogged(true);
            LOGGER.atInfo().log(
                "Wall jump unlocked for %s at Jump level %s",
                ref,
                skills.getLevel(SkillType.JUMP)
            );
        }
    }

    private void resetAirAbilitiesIfLanded(
        Ref<EntityStore> ref,
        MovementStates current,
        JumpAbilityStateComponent jumpState
    ) {
        if (current.onGround && !jumpState.wasGrounded()) {
            boolean usedSkyLeap = jumpState.hasUsedSkyLeapThisAirtime();
            jumpState.resetAirAbilities();
            LOGGER.atInfo().log("Reset Jump air abilities on landing for %s", ref);
            if (usedSkyLeap) {
                LOGGER.atInfo().log("[BeanzCore][Ability] SKY_LEAP airtime reset for %s", ref);
            }
        }
    }

    private boolean hasQueuedJumpInput(PlayerInput playerInput) {
        if (playerInput == null) {
            return false;
        }

        for (Object update : playerInput.getMovementUpdateQueue()) {
            if (update instanceof PlayerInput.SetMovementStates setMovementStates
                && setMovementStates.movementStates() != null
                && setMovementStates.movementStates().jumping) {
                return true;
            }
        }

        return false;
    }

    private boolean hasQueuedJumpRelease(PlayerInput playerInput) {
        if (playerInput == null) {
            return false;
        }

        for (Object update : playerInput.getMovementUpdateQueue()) {
            if (update instanceof PlayerInput.SetMovementStates setMovementStates
                && setMovementStates.movementStates() != null
                && !setMovementStates.movementStates().jumping) {
                return true;
            }
        }

        return false;
    }

    private void updateWallContactState(
        MovementStates current,
        CollisionResultComponent collisionResultComponent,
        TransformComponent transformComponent,
        Velocity velocity,
        JumpAbilityStateComponent jumpState
    ) {
        Vector3d detectedWallNormal = findWallNormal(collisionResultComponent, transformComponent, velocity);

        if (detectedWallNormal != null) {
            jumpState.updateRecentWallContact(detectedWallNormal, WALL_CONTACT_GRACE_TICKS);
            return;
        }

        jumpState.tickRecentWallContact(current.onGround);
    }

    private SkillRewardService rewardService(SkillService skillService)
    {
        return skillService.getRewardService();
    }

    private boolean isDoubleJumpRequested(
        MovementStates current,
        boolean newJumpPress,
        JumpAbilityStateComponent jumpState,
        boolean doubleJumpUnlocked
    ) {
        if (!doubleJumpUnlocked || jumpState.hasUsedDoubleJump() || current.onGround) {
            return false;
        }

        boolean requested = newJumpPress
            && jumpState.getAirTicks() > 0
            && jumpState.hasLeftGroundSinceInitialJump()
            && jumpState.hasJumpReleasedSinceGroundJump();
        boolean bufferedRequest = jumpState.getBufferedDoubleJumpPressTicks() > 0
            && jumpState.getAirTicks() > 1
            && jumpState.hasLeftGroundSinceInitialJump();

        requested = requested || bufferedRequest;

        if (requested) {
            LOGGER.atInfo().log(
                "Airborne re-press detected: airborne=%s, airTicks=%s, newJumpPress=%s, bufferedRequest=%s, bufferedDoubleJumpPressTicks=%s, hasLeftGroundSinceInitialJump=%s, jumpReleasedSinceTakeoff=%s",
                !current.onGround,
                jumpState.getAirTicks(),
                newJumpPress,
                bufferedRequest,
                jumpState.getBufferedDoubleJumpPressTicks(),
                jumpState.hasLeftGroundSinceInitialJump(),
                jumpState.hasJumpReleasedSinceGroundJump()
            );
        }

        return requested;
    }

    private double calculateAppliedJumpForce(
        double baseJumpForce,
        double fullJumpForce,
        double jumpRatio,
        JumpAbilityType abilityType,
        SkillRewardService rewardService
    ) {
        if (abilityType == null) {
            return baseJumpForce;
        }

        return switch (abilityType) {
            case GROUND -> baseJumpForce + ((fullJumpForce - baseJumpForce) * jumpRatio);
            case DOUBLE -> fullJumpForce * rewardService.getDoubleJumpForceScale() * jumpRatio;
            case WALL -> fullJumpForce * rewardService.getWallJumpVerticalForceScale() * jumpRatio;
        };
    }

    private void applyDoubleJump(
        Ref<EntityStore> ref,
        PlayerSkillsComponent skills,
        JumpAbilityStateComponent jumpState,
        Velocity velocity,
        double appliedJumpForce
    ) {
        double previousX = velocity.getX();
        double previousY = velocity.getY();
        double previousZ = velocity.getZ();
        double finalX = previousX;
        double finalY = Math.max(previousY, 0.0) + appliedJumpForce;
        double finalZ = previousZ;
        applyAirVelocityInstruction(velocity, finalX, finalY, finalZ);
        jumpState.consumeDoubleJump(0);

        LOGGER.atInfo().log(
            "Double jump used for %s: level=%s, previousVelocity=(%.3f, %.3f, %.3f), appliedBoost=%.3f, finalVelocity=(%.3f, %.3f, %.3f), movementPath=velocity_instruction_set",
            ref,
            skills.getLevel(SkillType.JUMP),
            previousX,
            previousY,
            previousZ,
            appliedJumpForce,
            finalX,
            finalY,
            finalZ
        );
    }

    private boolean applyWallJump(
        Ref<EntityStore> ref,
        PlayerSkillsComponent skills,
        JumpAbilityStateComponent jumpState,
        Vector3d wallNormal,
        Velocity velocity,
        double appliedJumpForce,
        SkillRewardService rewardService
    ) {
        if (wallNormal == null) {
            LOGGER.atInfo().log(
                "Wall jump was attempted for %s but no valid wall surface was found nearby",
                ref
            );
            return false;
        }

        double horizontalForce = rewardService.getWallJumpHorizontalForce(skills);
        double previousX = velocity.getX();
        double previousY = velocity.getY();
        double previousZ = velocity.getZ();
        double horizontalX = wallNormal.getX() * horizontalForce;
        double horizontalZ = wallNormal.getZ() * horizontalForce;
        double finalX = horizontalX;
        double finalY = Math.max(previousY, 0.0) + appliedJumpForce;
        double finalZ = horizontalZ;
        applyAirVelocityInstruction(velocity, finalX, finalY, finalZ);
        jumpState.setUsedWallJump(true);

        LOGGER.atInfo().log(
            "Wall jump used for %s: level=%s, wallNormal=(%.3f, %.3f, %.3f), previousVelocity=(%.3f, %.3f, %.3f), horizontalForce=%.3f, appliedBoost=(%.3f, %.3f, %.3f), finalVelocity=(%.3f, %.3f, %.3f), movementPath=velocity_instruction_set",
            ref,
            skills.getLevel(SkillType.JUMP),
            wallNormal.getX(),
            wallNormal.getY(),
            wallNormal.getZ(),
            previousX,
            previousY,
            previousZ,
            horizontalForce,
            horizontalX,
            appliedJumpForce,
            horizontalZ,
            finalX,
            finalY,
            finalZ
        );
        return true;
    }

    private void applyAirVelocityInstruction(Velocity velocity, double x, double y, double z) {
        Vector3d targetVelocity = new Vector3d(x, y, z);
        velocity.getInstructions().clear();
        velocity.addInstruction(targetVelocity, new VelocityConfig(), ChangeVelocityType.Set);
        velocity.set(targetVelocity);
        velocity.setClient(targetVelocity);
    }

    private Vector3d findWallNormal(
        CollisionResultComponent collisionResultComponent,
        TransformComponent transformComponent,
        Velocity velocity
    ) {
        if (collisionResultComponent == null) {
            return findAdjacentWallNormal(transformComponent);
        }

        CollisionResult collisionResult = collisionResultComponent.getCollisionResult();
        if (collisionResult == null) {
            return findAdjacentWallNormal(transformComponent);
        }

        Vector3d bestNormal = null;
        double bestCollisionStart = Double.MAX_VALUE;

        for (int i = 0; i < collisionResult.getBlockCollisionCount(); i++) {
            BlockCollisionData collisionData = collisionResult.getBlockCollision(i);
            if (collisionData == null) {
                continue;
            }

            Vector3d normal = collisionData.collisionNormal;
            if (normal == null || Math.abs(normal.getY()) > WALL_NORMAL_Y_LIMIT) {
                continue;
            }

            Vector3d horizontalNormal = new Vector3d(normal.getX(), 0.0, normal.getZ());
            if (horizontalNormal.closeToZero(EPSILON)) {
                continue;
            }

            if (collisionData.collisionStart < bestCollisionStart) {
                bestCollisionStart = collisionData.collisionStart;
                bestNormal = horizontalNormal.normalize();
            }
        }

        if (bestNormal != null) {
            return bestNormal;
        }

        Vector3d adjacentWallNormal = findAdjacentWallNormal(transformComponent);
        if (adjacentWallNormal != null) {
            return adjacentWallNormal;
        }

        return collisionResult.isSliding ? inferWallNormalFromVelocity(velocity) : null;
    }

    private Vector3d findAdjacentWallNormal(TransformComponent transformComponent) {
        if (transformComponent == null || transformComponent.getChunk() == null || transformComponent.getPosition() == null) {
            return null;
        }

        WorldChunk chunk = transformComponent.getChunk();
        Vector3d position = transformComponent.getPosition();
        int chunkWorldX = chunk.getX() * 16;
        int chunkWorldZ = chunk.getZ() * 16;
        int baseX = (int) Math.floor(position.getX());
        int baseY = (int) Math.floor(position.getY());
        int baseZ = (int) Math.floor(position.getZ());

        int[][] directions = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
        };

        for (int[] direction : directions) {
            int sampleX = baseX + direction[0];
            int sampleZ = baseZ + direction[1];
            if (isSolidWallBlock(chunk, chunkWorldX, chunkWorldZ, sampleX, baseY, sampleZ)
                || isSolidWallBlock(chunk, chunkWorldX, chunkWorldZ, sampleX, baseY + 1, sampleZ)) {
                return new Vector3d(-direction[0], 0.0, -direction[1]);
            }
        }

        return null;
    }

    private boolean isSolidWallBlock(
        WorldChunk chunk,
        int chunkWorldX,
        int chunkWorldZ,
        int worldX,
        int worldY,
        int worldZ
    ) {
        if (worldY < 0) {
            return false;
        }

        int localX = worldX - chunkWorldX;
        int localZ = worldZ - chunkWorldZ;
        if (localX < 0 || localX >= 16 || localZ < 0 || localZ >= 16) {
            return false;
        }

        return chunk.getBlock(localX, worldY, localZ) != 0;
    }

    private Vector3d inferWallNormalFromVelocity(Velocity velocity) {
        if (velocity == null) {
            return null;
        }

        Vector3d horizontalVelocity = new Vector3d(-velocity.getX(), 0.0, -velocity.getZ());
        return horizontalVelocity.closeToZero(EPSILON) ? null : horizontalVelocity.normalize();
    }
}
