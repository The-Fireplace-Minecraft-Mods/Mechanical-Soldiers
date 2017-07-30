package the_fireplace.mechsoldiers.entity.ai;

import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.util.ICPU;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.entity.ai.*;

public class AlienCPU implements ICPU {

	protected int raiseArmTicks;
	protected EntityAIAttackMelee aiAttackOnCollide = null;

	@Override
	public void addAttackAi(EntityMechSkeleton skeleton, byte mode) {
		if (aiAttackOnCollide == null) {
			aiAttackOnCollide = new EntityAIAttackMelee(skeleton, 2.0D, false) {
				@Override
				public void resetTask() {
					super.resetTask();
					skeleton.setSwingingArms(false);
					if (skeleton.getRNG().nextInt(5) == 3) {
						double d0 = skeleton.posX + (skeleton.world.rand.nextDouble() - 0.5D) * 64.0D;
						double d1 = skeleton.posY + (double) (skeleton.world.rand.nextInt(64) - 32);
						double d2 = skeleton.posZ + (skeleton.world.rand.nextDouble() - 0.5D) * 64.0D;
						EntityAITeleport.teleportTo(skeleton, d0, d1, d2);
					}
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
			case 2:
				skeleton.targetTasks.addTask(2, new EntityAIMasterHurtTarget(skeleton));
			case 1:
				skeleton.targetTasks.addTask(2, new EntityAINearestNonTeamTarget(skeleton, EntityPlayer.class, false));
				skeleton.targetTasks.addTask(2, new EntityAINearestNonTeamTarget(skeleton, EntityArmyMember.class, true));
				skeleton.targetTasks.addTask(3, new EntityAINearestNonTeamTarget(skeleton, IMob.class, true));
			case 0:
			default:
				skeleton.targetTasks.addTask(1, new EntityAIMasterHurtByTarget(skeleton));
				skeleton.targetTasks.addTask(1, new EntityAIHurtByNonAllied(skeleton, true));
				skeleton.targetTasks.addTask(4, new EntityAINearestNonTeamTarget(skeleton, IAnimals.class, false));
		}
	}

	@Override
	public void addMovementAi(EntityMechSkeleton skeleton, byte mode) {
		switch (mode) {
			case 1:
				skeleton.setHomePosAndDistance(new BlockPos(skeleton.posX, skeleton.posY, skeleton.posZ), -1);
				skeleton.tasks.addTask(0, new EntityAITeleport(skeleton));
				skeleton.tasks.addTask(4, new EntityAIOpenDoor(skeleton, false));
				skeleton.tasks.addTask(6, new EntityAIFollowMaster(skeleton, 1.0D, 20.0F, 1.0F));
				skeleton.tasks.addTask(7, new EntityAIWanderBase(skeleton, 1.0D));
				break;
			case 0:
				skeleton.tasks.addTask(0, new EntityAITeleport(skeleton));
				break;
			case 2:
			default:
				skeleton.setHomePosAndDistance(new BlockPos(skeleton.posX, skeleton.posY, skeleton.posZ), 20);
				skeleton.tasks.addTask(0, new EntityAITeleport(skeleton));
				skeleton.tasks.addTask(4, new EntityAIOpenDoor(skeleton, false));
				skeleton.tasks.addTask(7, new EntityAIWanderBase(skeleton, 1.0D));
		}
	}
}
