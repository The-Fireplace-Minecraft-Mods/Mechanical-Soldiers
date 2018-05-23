package the_fireplace.mechsoldiers.tileentity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import the_fireplace.mechsoldiers.container.ContainerCPUMelter;
import the_fireplace.mechsoldiers.container.SlotCPUMelter;
import the_fireplace.mechsoldiers.container.SlotLava;
import the_fireplace.mechsoldiers.registry.CPUMeltRecipes;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntityCPUMelter extends TileEntityLockable implements ITickable, ISidedInventory, IFluidHandler, IFluidTank {
	private static final int[] SLOTS_TOP = new int[]{1, 2};
	private static final int[] SLOTS_BOTTOM = new int[]{0, 3, 4};
	private static final int[] SLOTS_SIDES = new int[]{0};
	private NonNullList<ItemStack> furnaceItemStacks = NonNullList.withSize(5, ItemStack.EMPTY);
	private int furnaceBurnTime;
	private int heldLavaAmount;
	public static final int heldLavaAmountMax = 10000;
	private String furnaceCustomName;
	public boolean isLoaded;

	@Override
	public int getSizeInventory() {
		return this.furnaceItemStacks.size();
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

		if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
			stack.setCount(this.getInventoryStackLimit());
	}

	@Override
	public String getName() {
		return "container.cpu_melter";
	}

	@Override
	public boolean hasCustomName() {
		return this.furnaceCustomName != null && !this.furnaceCustomName.isEmpty();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		this.furnaceItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot");

			if (j >= 0 && j < this.furnaceItemStacks.size())
				this.furnaceItemStacks.set(j, new ItemStack(nbttagcompound));
		}

		this.furnaceBurnTime = compound.getInteger("BurnTime");
		this.heldLavaAmount = compound.getInteger("HeldLava");

		if (compound.hasKey("CustomName", 8))
			this.furnaceCustomName = compound.getString("CustomName");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("BurnTime", this.furnaceBurnTime);
		compound.setInteger("HeldLava", this.heldLavaAmount);
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

		if (this.hasCustomName())
			compound.setString("CustomName", this.furnaceCustomName);

		return compound;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isActive() {
		return this.furnaceBurnTime > 0;
	}

	@SideOnly(Side.CLIENT)
	public static boolean isActive(IInventory inventory) {
		return inventory.getField(0) > 0;
	}

	@Override
	public void update() {
		boolean isInitiallyActive = this.isActive();
		boolean tileChanged = false;

		if (!this.world.isRemote) {
			if (!isLoaded)
				isLoaded = true;
			if (!getStackInSlot(0).isEmpty() && FluidUtil.getFluidHandler(getStackInSlot(0)) != null && this.heldLavaAmount < heldLavaAmountMax) {
				InvWrapper thisInv = new InvWrapper(this);
				FluidActionResult result = FluidUtil.tryEmptyContainer(getStackInSlot(0), this, Fluid.BUCKET_VOLUME, null, true);
				if(result.isSuccess()){
					getStackInSlot(0).shrink(1);
					if(getStackInSlot(0).isEmpty())
						setInventorySlotContents(0, ItemStack.EMPTY);
					if(FluidUtil.getFluidContained(result.getResult()) == null){
						ItemStack inserted1 = thisInv.insertItem(4, result.getResult(), false);
						if(!inserted1.isEmpty()){
							ItemStack inserted2 = thisInv.insertItem(0, inserted1, false);
							if(!inserted2.isEmpty())
								InventoryHelper.spawnItemStack(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), inserted2);
						}
					}
				}
			}
			if (this.isActive() || !this.furnaceItemStacks.get(1).isEmpty() && !this.furnaceItemStacks.get(2).isEmpty()) {
				if (!this.isActive() && this.canSmelt()) {
					this.furnaceBurnTime = 5000;

					if (this.isActive())
						tileChanged = true;
				}

				if (this.isActive() && this.canSmelt()) {
					if (furnaceBurnTime % 5 == 0)
						--this.heldLavaAmount;
					--this.furnaceBurnTime;

					if (this.furnaceBurnTime == 0) {
						this.smeltItem();
						tileChanged = true;
					}
				} else
					this.furnaceBurnTime = 0;
			} else if (!this.isActive() && this.furnaceBurnTime > 0)
				this.furnaceBurnTime = 0;

			if (isInitiallyActive != this.isActive())
				tileChanged = true;
		}

		if (tileChanged)
			this.markDirty();
	}

	private boolean canSmelt() {
		if (this.furnaceItemStacks.get(1).isEmpty() || this.furnaceItemStacks.get(2).isEmpty() || heldLavaAmount <= 0)
			return false;
		else {
			ItemStack result = CPUMeltRecipes.instance().getMeltingResult(this.furnaceItemStacks.get(1), this.furnaceItemStacks.get(2));
			if (result.isEmpty()) return false;
			if (this.furnaceItemStacks.get(3).isEmpty()) return true;
			if (!this.furnaceItemStacks.get(3).isItemEqual(result)) return false;
			int resultCount = furnaceItemStacks.get(3).getCount() + result.getCount();
			return resultCount <= getInventoryStackLimit() && resultCount <= this.furnaceItemStacks.get(3).getMaxStackSize();
		}
	}

	public void smeltItem() {
		if (this.canSmelt()) {
			ItemStack itemstack = CPUMeltRecipes.instance().getMeltingResult(this.furnaceItemStacks.get(1), this.furnaceItemStacks.get(2));

			if (this.furnaceItemStacks.get(3).isEmpty())
				this.furnaceItemStacks.set(3, itemstack.copy());
			else if (this.furnaceItemStacks.get(3).getItem() == itemstack.getItem())
				this.furnaceItemStacks.get(3).grow(itemstack.getCount());

			this.furnaceItemStacks.get(1).shrink(1);
			this.furnaceItemStacks.get(2).shrink(1);

			if (this.furnaceItemStacks.get(1).isEmpty())
				this.furnaceItemStacks.set(1, ItemStack.EMPTY);

			if (this.furnaceItemStacks.get(2).isEmpty())
				this.furnaceItemStacks.set(2, ItemStack.EMPTY);
		}
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
				return SlotLava.isLava(stack);
			case 1:
				return SlotCPUMelter.isStackValid("left", stack);
			case 2:
				return SlotCPUMelter.isStackValid("right", stack);
			case 4:
				return FluidUtil.getFluidHandler(stack) != null;
			default:
				return false;
		}
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
		if (direction == EnumFacing.DOWN && index == 0)
			if (FluidUtil.getFluidContained(stack) != null && FluidUtil.getFluidContained(stack).getFluid() == FluidRegistry.LAVA)
				return false;

		return true;
	}

	@Override
	public String getGuiID() {
		return "mechsoldiers:cpu_melter";
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerCPUMelter(playerInventory, this);
	}

	@Override
	public int getField(int id) {
		switch (id) {
			case 0:
				return this.furnaceBurnTime;
			case 1:
				return this.heldLavaAmount;
			case 2:
				return heldLavaAmountMax;
			default:
				return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
			case 0:
				this.furnaceBurnTime = value;
				break;
			case 1:
				this.heldLavaAmount = value;
				break;
			default:

		}
	}

	@Override
	public int getFieldCount() {
		return 3;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.furnaceItemStacks)
			if (!itemstack.isEmpty())
				return false;

		return true;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.furnaceItemStacks.size(); ++i)
			this.furnaceItemStacks.set(i, ItemStack.EMPTY);
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
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) this;
		return super.getCapability(capability, facing);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[]{new FluidTankProperties(new FluidStack(getFluid(), getFluidAmount()), getCapacity())};
	}

	@Nullable
	@Override
	public FluidStack getFluid() {
		if (getFluidAmount() > 0)
			return new FluidStack(FluidRegistry.LAVA, getFluidAmount());
		return null;
	}

	@Override
	public int getFluidAmount() {
		return heldLavaAmount;
	}

	@Override
	public int getCapacity() {
		return heldLavaAmountMax;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		int maxFillAmount = getCapacity() - getFluidAmount();
		if (resource.getFluid().equals(FluidRegistry.LAVA) && maxFillAmount > 0) {
			if (maxFillAmount < resource.amount) {
				if (doFill)
					heldLavaAmount += maxFillAmount;
				return maxFillAmount;
			} else {
				if (doFill)
					heldLavaAmount += resource.amount;
				return resource.amount;
			}
		}
		return 0;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (resource.amount > 0 && resource.getFluid().equals(FluidRegistry.LAVA)) {
			if (resource.amount < getFluidAmount()) {
				if (doDrain)
					heldLavaAmount -= resource.amount;
				return new FluidStack(FluidRegistry.LAVA, resource.amount);
			} else {
				int prevHeldLava = getFluidAmount();
				if (doDrain)
					heldLavaAmount = 0;
				return new FluidStack(FluidRegistry.LAVA, prevHeldLava);
			}
		}
		return null;
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if (maxDrain > 0) {
			if (maxDrain < getFluidAmount()) {
				if (doDrain)
					heldLavaAmount -= maxDrain;
				return new FluidStack(FluidRegistry.LAVA, maxDrain);
			} else {
				int prevHeldLava = getFluidAmount();
				if (doDrain)
					heldLavaAmount = 0;
				return new FluidStack(FluidRegistry.LAVA, prevHeldLava);
			}
		}
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);

	}
}