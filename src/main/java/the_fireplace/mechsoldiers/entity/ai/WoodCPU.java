package the_fireplace.mechsoldiers.entity.ai;

import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.util.ICPU;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.entity.ai.*;

public class WoodCPU implements ICPU {

	private EntityAIArmyBow aiArrowAttack = null;

	@Override
	public void addAttackAi(EntityMechSkeleton skeleton, byte mode) {
		if (aiArrowAttack == null) {
			aiArrowAttack = new EntityAIArmyBow(skeleton, 1.5D, 15, (float) ConfigValues.MAXARROWDISTANCE);
		}
		skeleton.tasks.addTask(5, aiArrowAttack);
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
				skeleton.tasks.addTask(4, new EntityAIOpenDoor(skeleton, false));
				skeleton.tasks.addTask(6, new EntityAIFollowMaster(skeleton, 1.0D, 16.0F, 3.0F));
				break;
			case 0:
				break;
			case 2:
			default:
				skeleton.setHomePosAndDistance(new BlockPos(skeleton.posX, skeleton.posY, skeleton.posZ), 20);
				skeleton.tasks.addTask(4, new EntityAIOpenDoor(skeleton, false));
				skeleton.tasks.addTask(7, new EntityAIWanderBase(skeleton, 1.0D));
		}
	}
}
