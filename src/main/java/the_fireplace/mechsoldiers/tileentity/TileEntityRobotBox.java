package the_fireplace.mechsoldiers.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;

import java.util.Random;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class TileEntityRobotBox extends TileEntity implements ITickable {

	private NBTTagCompound skeletonData;
	private int maxTicks;
	private int ticksRemaining;
	private Random rand;

	public TileEntityRobotBox() {
		//Only for use when world is loading.
		rand = new Random();
	}

	public TileEntityRobotBox(NBTTagCompound skeletonData, int ticksToWait) {
		this.skeletonData = skeletonData;
		maxTicks = ticksRemaining = ticksToWait;
		rand = new Random();
	}

	public void setSkeletonData(NBTTagCompound nbt) {
		skeletonData = nbt;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, getBlockMetadata(), getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation("tile.robot_constructor.name");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		compound.setTag("ConstructingSkeletonData", skeletonData);
		compound.setInteger("TicksRemaining", ticksRemaining);
		compound.setInteger("MaxTicks", maxTicks);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		skeletonData = compound.getCompoundTag("ConstructingSkeletonData");
		ticksRemaining = compound.getInteger("TicksRemaining");
		if (compound.hasKey("MaxTicks"))
			maxTicks = compound.getInteger("MaxTicks");
		else
			maxTicks = compound.getInteger("TicksRemaining");
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			ticksRemaining--;
			if (ticksRemaining <= 0)
				spawnRobot();
		}
	}

	public ItemStack getCPU() {
		return new ItemStack(skeletonData.getCompoundTag("RobotCPU"));
	}

	public ItemStack getSkeleton() {
		return new ItemStack(skeletonData.getCompoundTag("RobotSkeleton"));
	}

	public ItemStack getJoints() {
		return new ItemStack(skeletonData.getCompoundTag("RobotJoints"));
	}

	public String getOwnerId() {
		return skeletonData.getString("OwnerUUID");
	}

	public float getCompletion() {
		return 1.0F - (float) ticksRemaining / (float) maxTicks;
	}

	private void spawnRobot() {
		EntityMechSkeleton robot = new EntityMechSkeleton(world, UUID.fromString(getOwnerId()))
				.setCPU(getCPU())
				.setSkeleton(getSkeleton())
				.setJoints(getJoints());
		robot.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), rand.nextFloat(), rand.nextFloat());
		world.spawnEntity(robot);
		robot.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), rand.nextFloat(), rand.nextFloat());

		world.removeTileEntity(pos);
		world.destroyBlock(pos, false);
	}
}

