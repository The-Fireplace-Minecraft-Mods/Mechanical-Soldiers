package the_fireplace.mechsoldiers.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.util.ICPU;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.entity.ai.*;

public class GreenCPU implements ICPU {

	protected int raiseArmTicks;
	protected EntityAIAttackMelee aiAttackOnCollide = null;

	@Override
	public void addAttackAi(EntityMechSkeleton skeleton, byte mode) {
		if (aiAttackOnCollide == null) {
			aiAttackOnCollide = new EntityAIAttackMelee(skeleton, 1.4D, false) {
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

				@Override
				protected void checkAndPerformAttack(EntityLivingBase entity, double distance) {
					double d0 = this.getAttackReachSqr(entity);

					if (distance <= d0 && this.attackTick <= 0) {
						this.attackTick = 30;
						this.attacker.swingArm(EnumHand.MAIN_HAND);
						this.attacker.attackEntityAsMob(entity);
						entity.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f - ((float) entity.world.rand.nextInt(8)) * 0.1f);
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
				skeleton.targetTasks.addTask(1, new EntityAIMasterHurtByTarget(skeleton));
				skeleton.targetTasks.addTask(1, new EntityAIHurtByNonAllied(skeleton, true));
				skeleton.targetTasks.addTask(2, new EntityAINearestNonTeamTarget(skeleton, EntityPlayer.class, true));
				skeleton.targetTasks.addTask(2, new EntityAINearestNonTeamTarget(skeleton, EntityArmyMember.class, true));
				skeleton.targetTasks.addTask(3, new EntityAINearestNonTeamTarget(skeleton, IMob.class, true));
			case 0:
			default:
				break;
		}
	}

	@Override
	public void addMovementAi(EntityMechSkeleton skeleton, byte mode) {
		switch (mode) {
			case 1:
				skeleton.setHomePosAndDistance(new BlockPos(skeleton.posX, skeleton.posY, skeleton.posZ), -1);
				skeleton.tasks.addTask(3, new EntityAIAvoidEntity(skeleton, EntityOcelot.class, 20.0F, 1.5D, 2.0D));
				skeleton.tasks.addTask(3, new EntityAIAvoidEntity(skeleton, EntityWolf.class, 10.0F, 1.2D, 1.5D));//TODO: Avoid parrots
				skeleton.tasks.addTask(4, new EntityAIOpenDoor(skeleton, false));
				skeleton.tasks.addTask(6, new EntityAIFollowMaster(skeleton, 1.0D, 16.0F, 3.0F));
				break;
			case 0:
				break;
			case 2:
			default:
				skeleton.setHomePosAndDistance(new BlockPos(skeleton.posX, skeleton.posY, skeleton.posZ), 20);
				skeleton.tasks.addTask(3, new EntityAIAvoidEntity(skeleton, EntityOcelot.class, 20.0F, 1.5D, 2.0D));
				skeleton.tasks.addTask(3, new EntityAIAvoidEntity(skeleton, EntityWolf.class, 10.0F, 1.2D, 1.5D));//TODO: Avoid parrots
				skeleton.tasks.addTask(4, new EntityAIOpenDoor(skeleton, false));
				skeleton.tasks.addTask(7, new EntityAIWanderBase(skeleton, 1.0D));
		}
	}
}
