package the_fireplace.mechsoldiers.items;

import net.minecraft.entity.ai.EntityAITasks;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;

/**
 * @author The_Fireplace
 */
public interface IBrain {
    /**
     * Adds the movement AI to the skeleton
     * @param tasks
     *  The skeleton's AI tasks
     * @param skeleton
     *  The skeleton itself
     * @param mode
     *  The movement mode. 0 is stationed, 1 is follower, 2 is wander area
     */
    void addMovementAi(EntityAITasks tasks, EntityMechSkeleton skeleton, byte mode);
    /**
     * Adds the attack AI to the skeleton
     * @param tasks
     *  The skeleton's AI tasks
     * @param skeleton
     *  The skeleton itself
     * @param mode
     *  The attack mode. 0 is passive, 1 is defensive, 2 is aggressive
     */
    void addAttackAi(EntityAITasks tasks, EntityMechSkeleton skeleton, byte mode);
    /**
     * Adds the movement AI to the skeleton
     * @param targetTasks
     *  The skeleton's target AI tasks
     * @param skeleton
     *  The skeleton itself
     * @param mode
     *  The attack mode. 0 is passive, 1 is defensive, 2 is aggressive
     */
    void addTargetAi(EntityAITasks targetTasks, EntityMechSkeleton skeleton, byte mode);
}
