package the_fireplace.mechsoldiers.tileentity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.oredict.OreDictionary;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.container.ContainerMetalPartConstructor;
import the_fireplace.mechsoldiers.registry.PartRegistry;
import the_fireplace.mechsoldiers.util.EnumPartType;
import the_fireplace.overlord.items.ItemOverlordsSeal;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntityPartPainter extends TileEntityLockable implements ISidedInventory {
	private static final int[] SLOTS_TOP = new int[]{0};
	private static final int[] SLOTS_BOTTOM = new int[]{4};
	private static final int[] SLOTS_SIDES = new int[]{1, 2, 3};
	private NonNullList<ItemStack> furnaceItemStacks = NonNullList.withSize(5, ItemStack.EMPTY);
	private short redValue;
	private short greenValue;
	private short blueValue;

	public void spawnSkeleton() {
		if (getStackInSlot(0).isEmpty() || (redValue > 0 && getStackInSlot(1).isEmpty()) || getStackInSlot(1).getCount()<redValue/85 || (greenValue > 0 && getStackInSlot(2).isEmpty()) || getStackInSlot(2).getCount()<greenValue/85 || (blueValue > 0 && getStackInSlot(3).isEmpty()) || getStackInSlot(3).getCount()<blueValue/85)
			return;
		ItemStack output = getStackInSlot(0);
		NBTTagCompound outData = new NBTTagCompound();
		outData.setInteger("RobotCPU", oneCPU.writeToNBT(new NBTTagCompound()));
		outData.setTag("RobotSkeleton", oneSkeleton.writeToNBT(new NBTTagCompound()));
		outData.setTag("RobotJoints", oneJoints.writeToNBT(new NBTTagCompound()));

		robotBox.setTagCompound(outData);

		setInventorySlotContents(5, robotBox);

		for (int i = 1; i < 5; i++) {
			if (getStackInSlot(i).getCount() > 1)
				getStackInSlot(i).shrink(1);
			else
				setInventorySlotContents(i, ItemStack.EMPTY);
		}

		if (!getStackInSlot(0).isEmpty()) {
			if (getStackInSlot(0).getItem() instanceof ItemOverlordsSeal) {
				if (((ItemOverlordsSeal) getStackInSlot(0).getItem()).isConsumable())
					if (getStackInSlot(0).getCount() > 1)
						getStackInSlot(0).shrink(1);
					else
						setInventorySlotContents(0, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return this.furnaceItemStacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.furnaceItemStacks)
		{
			if (!itemstack.isEmpty())
			{
				return false;
			}
		}

		return true;
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
	public ItemStack getStackInSlot(int index) {
		return this.furnaceItemStacks.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(this.furnaceItemStacks, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.furnaceItemStacks, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.furnaceItemStacks.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
	}

	@Override
	public String getName() {
		return "container.part_painter";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		this.furnaceItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot");

			if (j >= 0 && j < this.furnaceItemStacks.size()) {
				this.furnaceItemStacks.set(j, new ItemStack(nbttagcompound));
			}
		}

		this.redValue = compound.getShort("RedValue");
		this.greenValue = compound.getShort("GreenValue");
		this.blueValue = compound.getShort("BlueValue");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setShort("RedValue", this.redValue);
		compound.setShort("GreenValue", this.greenValue);
		compound.setShort("BlueValue", this.blueValue);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.furnaceItemStacks.size(); ++i) {
			if (!this.furnaceItemStacks.get(i).isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				this.furnaceItemStacks.get(i).writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		compound.setTag("Items", nbttaglist);

		return compound;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		switch(index){
			case 0:
				return isSkeletonOrJoint(stack);
			case 1:
				return isDyeColor(stack, "Red");
			case 2:
				return isDyeColor(stack, "Green");
			case 3:
				return isDyeColor(stack, "Blue");
			case 4:
			default:
				return false;
		}
	}

	public static boolean isSkeletonOrJoint(ItemStack stack){
		return PartRegistry.isPartOfType(stack, EnumPartType.SKELETON) || PartRegistry.isPartOfType(stack, EnumPartType.JOINTS);
	}

	public static boolean isDyeColor(ItemStack stack, String color){
		for(int id : OreDictionary.getOreIDs(stack)){
			if(OreDictionary.getOreName(id).equals("dye"+color))
				return true;
		}
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.DOWN ? SLOTS_BOTTOM : (side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return this.isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == 4;
	}

	@Override
	public String getGuiID() {
		return "mechsoldiers:part_constructor";
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerMetalPartConstructor(playerInventory, this);
	}

	@Override
	public int getField(int id) {
		switch (id) {
			case 0:
				return this.redValue;
			case 1:
				return this.greenValue;
			case 2:
				return this.blueValue;
			default:
				return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
			case 0:
				this.redValue = (short)value;
				break;
			case 1:
				this.greenValue = (short)value;
				break;
			case 2:
				this.blueValue = (short)value;
				break;
			default:

		}
	}

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.furnaceItemStacks.size(); ++i) {
			this.furnaceItemStacks.set(i, ItemStack.EMPTY);
		}
	}

	IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
	IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
	IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			if (facing == EnumFacing.DOWN)
				return (T) handlerBottom;
			else if (facing == EnumFacing.UP)
				return (T) handlerTop;
			else
				return (T) handlerSide;
		return super.getCapability(capability, facing);
	}
}
