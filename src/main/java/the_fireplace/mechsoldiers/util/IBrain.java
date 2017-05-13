package the_fireplace.mechsoldiers.util;

import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;

/**
 * @author The_Fireplace
 */
public interface IBrain {
    /**
     * Adds the movement AI to the skeleton
     * @param skeleton
     *  The skeleton itself
     * @param mode
     *  The movement mode. 0 is stationed, 1 is follower, 2 is wander area
     */
    void addMovementAi(EntityMechSkeleton skeleton, byte mode);
    /**
     * Adds the attack AI to the skeleton
     * @param skeleton
     *  The skeleton itself
     * @param mode
     *  The attack mode. 0 is passive, 1 is defensive, 2 is aggressive
     */
    void addAttackAi(EntityMechSkeleton skeleton, byte mode);
    /**
     * Adds the movement AI to the skeleton
     * @param skeleton
     *  The skeleton itself
     * @param mode
     *  The attack mode. 0 is passive, 1 is defensive, 2 is aggressive
     */
    void addTargetAi(EntityMechSkeleton skeleton, byte mode);
}
