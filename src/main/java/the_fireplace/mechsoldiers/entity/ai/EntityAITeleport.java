package the_fireplace.mechsoldiers.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;

import java.util.Random;

public class EntityAITeleport extends EntityAIBase {
	private final EntityMechSkeleton skeleton;

	public EntityAITeleport(EntityMechSkeleton entityIn) {
		this.skeleton = entityIn;
	}

	@Override
	public boolean shouldExecute() {
		return this.skeleton.isInWater() || this.skeleton.isInLava() || this.skeleton.world.isRainingAt(skeleton.getPosition());
	}

	@Override
	public void updateTask() {
		double d0 = skeleton.posX + (skeleton.world.rand.nextDouble() - 0.5D) * 64.0D;
		double d1 = skeleton.posY + (double) (skeleton.world.rand.nextInt(64) - 32);
		double d2 = skeleton.posZ + (skeleton.world.rand.nextDouble() - 0.5D) * 64.0D;
		EntityAITeleport.teleportTo(skeleton, d0, d1, d2);
	}

	public static boolean teleportTo(EntityMechSkeleton skeleton, double x, double y, double z) {
		EnderTeleportEvent event = new EnderTeleportEvent(skeleton, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) return false;
		boolean flag = attemptTeleport(skeleton, event.getTargetX(), event.getTargetY(), event.getTargetZ());

		if (flag) {
			skeleton.world.playSound(null, skeleton.prevPosX, skeleton.prevPosY, skeleton.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, skeleton.getSoundCategory(), 1.0F, 1.0F);
			skeleton.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
		}

		return flag;
	}

	public static boolean attemptTeleport(EntityMechSkeleton skeleton, double x, double y, double z) {
		double d0 = skeleton.posX;
		double d1 = skeleton.posY;
		double d2 = skeleton.posZ;
		skeleton.posX = x;
		skeleton.posY = y;
		skeleton.posZ = z;
		boolean flag = false;
		BlockPos blockpos = new BlockPos(skeleton);
		World world = skeleton.world;
		Random random = skeleton.getRNG();

		if (world.isBlockLoaded(blockpos)) {
			boolean flag1 = false;

			while (!flag1 && blockpos.getY() > 0) {
				BlockPos blockpos1 = blockpos.down();
				IBlockState iblockstate = world.getBlockState(blockpos1);

				if (iblockstate.getMaterial().blocksMovement()) {
					flag1 = true;
				} else {
					--skeleton.posY;
					blockpos = blockpos1;
				}
			}

			if (flag1) {
				skeleton.setPositionAndUpdate(skeleton.posX, skeleton.posY, skeleton.posZ);

				if (world.getCollisionBoxes(skeleton, skeleton.getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(skeleton.getEntityBoundingBox())) {
					flag = true;
				}
			}
		}

		if (!flag) {
			skeleton.setPositionAndUpdate(d0, d1, d2);
			return false;
		} else {
			for (int j = 0; j < 128; ++j) {
				double d6 = (double) j / 127.0D;
				float f = (random.nextFloat() - 0.5F) * 0.2F;
				float f1 = (random.nextFloat() - 0.5F) * 0.2F;
				float f2 = (random.nextFloat() - 0.5F) * 0.2F;
				double d3 = d0 + (skeleton.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double) skeleton.width * 2.0D;
				double d4 = d1 + (skeleton.posY - d1) * d6 + random.nextDouble() * (double) skeleton.height;
				double d5 = d2 + (skeleton.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double) skeleton.width * 2.0D;
				world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (double) f, (double) f1, (double) f2);
			}
			skeleton.getNavigator().clearPathEntity();

			return true;
		}
	}
}