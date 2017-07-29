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
import the_fireplace.mechsoldiers.container.ContainerMetalPartConstructor;
import the_fireplace.mechsoldiers.registry.PartRegistry;
import the_fireplace.mechsoldiers.util.EnumPartType;
import the_fireplace.overlord.tileentity.ISkeletonMaker;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntityPartStainer extends TileEntityLockable implements ISidedInventory, ISkeletonMaker {//Implements ISkeletonMaker so I don't have to make more packets
	private static final int[] SLOTS_TOP = new int[]{0};
	private static final int[] SLOTS_BOTTOM = new int[]{4};
	private static final int[] SLOTS_SIDES = new int[]{1, 2, 3, 5};
	private NonNullList<ItemStack> furnaceItemStacks = NonNullList.withSize(6, ItemStack.EMPTY);
	private short redValue=255;
	private short greenValue=255;
	private short blueValue=255;

	public int getRed(){
		return redValue;
	}

	public int getGreen(){
		return greenValue;
	}

	public int getBlue(){
		return blueValue;
	}

	public boolean hasEnoughRed(){
		return getStackInSlot(1).getCount()>= getFinalRedCost() && !(redValue < 255 && getStackInSlot(1).isEmpty());
	}

	public boolean hasEnoughGreen(){
		return getStackInSlot(2).getCount()>= getFinalGreenCost() && !(greenValue < 255 && getStackInSlot(2).isEmpty());
	}

	public boolean hasEnoughBlue(){
		return getStackInSlot(3).getCount()>= getFinalBlueCost() && !(blueValue < 255 && getStackInSlot(3).isEmpty());
	}

	public int getFinalRedCost(){
		return getRedCost()+ getDarknessCompensationCost();
	}

	public int getFinalGreenCost(){
		return getGreenCost()+ getDarknessCompensationCost();
	}

	public int getFinalBlueCost(){
		return getBlueCost()+ getDarknessCompensationCost();
	}

	public int getRedCost(){
		int initCost = (greenValue/85)*(blueValue/85)/-3+(redValue/85);
		if(initCost <= 0)
			return 0;
		else
			return initCost;
	}

	public int getGreenCost(){
		int initCost = (redValue/85)*(blueValue/85)/-3+(greenValue/85);
		if(initCost <= 0)
			return 0;
		else
			return initCost;
	}

	public int getBlueCost(){
		int initCost = (redValue/85)*(greenValue/85)/-3+(blueValue/85);
		if(initCost <= 0)
			return 0;
		else
			return initCost;
	}

	public int getDarknessCost(){
		int cost = 0;
		if(redValue<85)
			cost += 1;
		if(greenValue<85)
			cost += 1;
		if(blueValue<85)
			cost += 1;
		return cost;
	}

	public int getDarknessCompensationCost(){
		return getDarknessCost()-getBlackDyeCost();
	}

	public int getBlackDyeCost(){
		if(!getStackInSlot(5).isEmpty()){
			if(getStackInSlot(5).getCount() >= getDarknessCost())
				return getDarknessCost();
			else
				return getStackInSlot(5).getCount();
		}else
			return 0;
	}


	@Override
	public void spawnSkeleton() {//I am using this method so I can piggyback off the Overlord packets as much as possible.
		if (getStackInSlot(0).isEmpty() || !getStackInSlot(4).isEmpty() || !hasEnoughRed() || !hasEnoughGreen() || !hasEnoughBlue())
			return;
		ItemStack output = getStackInSlot(0).copy();
		NBTTagCompound outData = new NBTTagCompound();
		outData.setInteger("StainColor", new Color(redValue, greenValue, blueValue).getRGB());

		output.setTagCompound(outData);
		output.setCount(1);

		setInventorySlotContents(4, output);

		if (getStackInSlot(0).getCount() > 1)
			getStackInSlot(0).shrink(1);
		else
			setInventorySlotContents(0, ItemStack.EMPTY);

		if (getStackInSlot(1).getCount() > 1)
			getStackInSlot(1).shrink(getFinalRedCost());
		if(getStackInSlot(1).isEmpty())
			setInventorySlotContents(1, ItemStack.EMPTY);
		if (getStackInSlot(2).getCount() > 1)
			getStackInSlot(2).shrink(getFinalGreenCost());
		if(getStackInSlot(2).isEmpty())
			setInventorySlotContents(2, ItemStack.EMPTY);
		if (getStackInSlot(3).getCount() > 1)
			getStackInSlot(3).shrink(getFinalBlueCost());
		if(getStackInSlot(3).isEmpty())
			setInventorySlotContents(3, ItemStack.EMPTY);
		if (getStackInSlot(5).getCount() > 1)
			getStackInSlot(5).shrink(getBlackDyeCost());
		if(getStackInSlot(5).isEmpty())
			setInventorySlotContents(5, ItemStack.EMPTY);
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
		return "container.part_stainer";
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
			case 5:
				return isDyeColor(stack, "Black");
			case 4:
			default:
				return false;
		}
	}

	public static boolean isSkeletonOrJoint(ItemStack stack){
		return PartRegistry.isPartOfType(stack, EnumPartType.SKELETON) || PartRegistry.isPartOfType(stack, EnumPartType.JOINTS);
	}

	public static boolean isDyeColor(ItemStack stack, String color){
		for(int id : OreDictionary.getOreIDs(stack))
			if(OreDictionary.getOreName(id).equals("dye"+color))
				return true;
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
