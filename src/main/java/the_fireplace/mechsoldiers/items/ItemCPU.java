package the_fireplace.mechsoldiers.items;

import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.util.ICPU;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.entity.ai.*;

/**
 * @author The_Fireplace
 */
public class ItemCPU extends Item implements ICPU {

	private String material;

	public ItemCPU(String material, int durability) {
		setUnlocalizedName("cpu_" + material);
		setRegistryName("cpu_" + material);
		setMaxDamage(durability);
		this.material = material;
		setCreativeTab(MechSoldiers.TAB_ROBOT_PARTS);
	}

	protected int raiseArmTicks;
	protected EntityAIAttackMelee aiAttackOnCollide = null;

	@Override
	public void addAttackAi(EntityMechSkeleton skeleton, byte mode) {
		if (aiAttackOnCollide == null) {
			aiAttackOnCollide = new EntityAIAttackMelee(skeleton, 0.75D, false) {
				@Override
				public void resetTask() {
					super.resetTask();
					skeleton.setSwingingArms(false);
				}

				@Override
				public void startExecuting() {
					super.startExecuting();
					raiseArmTicks = 0;
				}

				@Override
				public void updateTask() {
					if (shouldContinueExecuting()) {
						++raiseArmTicks;

						if (raiseArmTicks >= 5 && this.attackTick < 10) {
							skeleton.setSwingingArms(true);
						} else {
							skeleton.setSwingingArms(false);
						}
						super.updateTask();
					}
				}
			};
		}
		if (skeleton.getMovementMode() > 0)
			skeleton.tasks.addTask(5, aiAttackOnCollide);
	}

	@Override
	public void addTargetAi(EntityMechSkeleton skeleton, byte mode) {
		switch (mode) {
			case 0:
			default:
				skeleton.setAttackTarget(null);
				skeleton.setRevengeTarget(null);
				break;
			case 2:
				skeleton.targetTasks.addTask(2, new EntityAIMasterHurtTarget(skeleton));
			case 1:
				skeleton.targetTasks.addTask(1, new EntityAIMasterHurtByTarget(skeleton));
				skeleton.targetTasks.addTask(1, new EntityAIHurtByNonAllied(skeleton, !material.equals("copper_redstone")));
				skeleton.targetTasks.addTask(2, new EntityAINearestNonTeamTarget(skeleton, EntityPlayer.class, true));
				skeleton.targetTasks.addTask(2, new EntityAINearestNonTeamTarget(skeleton, EntityArmyMember.class, true));
				skeleton.targetTasks.addTask(3, new EntityAINearestNonTeamTarget(skeleton, IMob.class, true));
		}
	}

	@Override
	public void addMovementAi(EntityMechSkeleton skeleton, byte mode) {
		switch (mode) {
			case 1:
				if (skeleton.shouldMobAttack(new EntityCreeper(skeleton.world))) {
					skeleton.tasks.addTask(3, new EntityAIAvoidEntity(skeleton, EntityCreeper.class, 10.0F, 1.2D, 1.6D));
				}
				skeleton.tasks.addTask(4, new EntityAIOpenDoor(skeleton, material.contains("redstone")));
				skeleton.tasks.addTask(6, new EntityAIFollowMaster(skeleton, 0.75D, 8.0F, 3.0F));
			case 0:
				skeleton.setHomePosAndDistance(new BlockPos(skeleton.posX, skeleton.posY, skeleton.posZ), -1);
				break;
			case 2:
			default:
				skeleton.setHomePosAndDistance(new BlockPos(skeleton.posX, skeleton.posY, skeleton.posZ), 20);
				if (skeleton.shouldMobAttack(new EntityCreeper(skeleton.world))) {
					skeleton.tasks.addTask(3, new EntityAIAvoidEntity(skeleton, EntityCreeper.class, 8.0F, 0.8D, 1.2D));
				}
				skeleton.tasks.addTask(4, new EntityAIOpenDoor(skeleton, material.contains("redstone")));
				skeleton.tasks.addTask(7, new EntityAIWanderBase(skeleton, 0.5D));
		}
	}
}
