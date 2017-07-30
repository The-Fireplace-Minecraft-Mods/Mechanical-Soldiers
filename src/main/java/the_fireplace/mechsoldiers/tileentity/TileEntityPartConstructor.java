package the_fireplace.mechsoldiers.tileentity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import the_fireplace.mechsoldiers.blocks.BlockMetalPartConstructor;
import the_fireplace.mechsoldiers.container.ContainerMetalPartConstructor;
import the_fireplace.mechsoldiers.container.SlotMPConstructorFuel;
import the_fireplace.mechsoldiers.registry.MetalMeltRecipes;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntityPartConstructor extends TileEntityLockable implements ITickable, ISidedInventory, IFluidHandler, IFluidTank {
	private static final int[] SLOTS_TOP = new int[]{0, 1};
	private static final int[] SLOTS_BOTTOM = new int[]{3, 2};
	private static final int[] SLOTS_SIDES = new int[]{2, 4};
	private NonNullList<ItemStack> furnaceItemStacks = NonNullList.withSize(5, ItemStack.EMPTY);
	private int furnaceBurnTime;
	private int currentItemBurnTime;
	private int cookTime;
	private int totalCookTime;
	private int heldWaterAmount;
	public static final int heldWaterAmountMax = 6000;
	private String furnaceCustomName;
	public boolean isLoaded;

	@Override
	public int getSizeInventory() {
		return this.furnaceItemStacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.furnaceItemStacks) {
			if (!itemstack.isEmpty()) {
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
		boolean flag = !stack.isEmpty() && stack.isItemEqual(this.furnaceItemStacks.get(index)) && ItemStack.areItemStackTagsEqual(stack, this.furnaceItemStacks.get(index));
		this.furnaceItemStacks.set(index, stack);

		if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}

		if (index == 0 && !flag) {
			this.totalCookTime = this.getCookTime(stack);
			this.cookTime = 0;
			this.markDirty();
		}
	}

	@Override
	public String getName() {
		return "container.metal_part_maker";
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

			if (j >= 0 && j < this.furnaceItemStacks.size()) {
				this.furnaceItemStacks.set(j, new ItemStack(nbttagcompound));
			}
		}

		this.furnaceBurnTime = compound.getInteger("BurnTime");
		this.cookTime = compound.getInteger("CookTime");
		this.totalCookTime = compound.getInteger("CookTimeTotal");
		this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks.get(2));
		this.heldWaterAmount = compound.getInteger("HeldWater");

		if (compound.hasKey("CustomName", 8)) {
			this.furnaceCustomName = compound.getString("CustomName");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("BurnTime", this.furnaceBurnTime);
		compound.setInteger("CookTime", this.cookTime);
		compound.setInteger("CookTimeTotal", this.totalCookTime);
		compound.setInteger("HeldWater", this.heldWaterAmount);
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

		if (this.hasCustomName()) {
			compound.setString("CustomName", this.furnaceCustomName);
		}

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
		boolean flag = this.isActive();
		boolean flag1 = false;

		if (this.isActive()) {
			--this.furnaceBurnTime;
		}

		if (!this.world.isRemote) {
			if (!isLoaded)
				isLoaded = true;
			if (!this.furnaceItemStacks.get(4).isEmpty() && FluidUtil.getFluidHandler(furnaceItemStacks.get(4)) != null && this.heldWaterAmount < heldWaterAmountMax) {
				FluidUtil.tryEmptyContainerAndStow(furnaceItemStacks.get(4), this, this.handlerBottom, 1000, null);
			}
			if (this.isActive() || !this.furnaceItemStacks.get(2).isEmpty() && !this.furnaceItemStacks.get(0).isEmpty() && !this.furnaceItemStacks.get(1).isEmpty()) {
				if (!this.isActive() && this.canSmelt()) {
					this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks.get(2));
					this.currentItemBurnTime = this.furnaceBurnTime;

					if (this.isActive()) {
						flag1 = true;

						if (!this.furnaceItemStacks.get(2).isEmpty()) {
							this.furnaceItemStacks.get(2).shrink(1);

							if (this.furnaceItemStacks.get(2).isEmpty()) {
								this.furnaceItemStacks.set(2, furnaceItemStacks.get(2).getItem().getContainerItem(furnaceItemStacks.get(2)));
							}
						}
					}
				}

				if (this.isActive() && this.canSmelt()) {
					++this.cookTime;

					if (this.cookTime == this.totalCookTime) {
						this.cookTime = 0;
						this.totalCookTime = this.getCookTime(this.furnaceItemStacks.get(0));
						this.smeltItem();
						flag1 = true;
					}
				} else {
					this.cookTime = 0;
				}
			} else if (!this.isActive() && this.cookTime > 0) {
				this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
			}

			if (flag != this.isActive()) {
				flag1 = true;
				BlockMetalPartConstructor.setState(this.isActive(), this.world, this.pos);
			}
		}

		if (flag1) {
			this.markDirty();
		}
	}

	public int getCookTime(ItemStack stack) {
		return 200;
	}

	private boolean canSmelt() {
		if (this.furnaceItemStacks.get(0).isEmpty() || this.furnaceItemStacks.get(1).isEmpty() || heldWaterAmount <= 0) {
			return false;
		} else {
			ItemStack itemstack = MetalMeltRecipes.instance().getMeltingResult(this.furnaceItemStacks.get(0), this.furnaceItemStacks.get(1));
			if (itemstack == null) return false;
			if (MetalMeltRecipes.instance().getWaterCost(itemstack) > heldWaterAmount) return false;
			if (this.furnaceItemStacks.get(3).isEmpty()) return true;
			if (!this.furnaceItemStacks.get(3).isItemEqual(itemstack)) return false;
			int result = furnaceItemStacks.get(3).getCount() + itemstack.getCount();
			return result <= getInventoryStackLimit() && result <= this.furnaceItemStacks.get(3).getMaxStackSize();
		}
	}

	public void smeltItem() {
		if (this.canSmelt()) {
			ItemStack itemstack = MetalMeltRecipes.instance().getMeltingResult(this.furnaceItemStacks.get(0), this.furnaceItemStacks.get(1));

			if (this.furnaceItemStacks.get(3).isEmpty()) {
				this.furnaceItemStacks.set(3, itemstack.copy());
			} else if (this.furnaceItemStacks.get(3).getItem() == itemstack.getItem()) {
				this.furnaceItemStacks.get(3).grow(itemstack.getCount());
			}

			this.furnaceItemStacks.get(0).shrink(1);
			this.furnaceItemStacks.get(1).shrink(1);

			if (this.furnaceItemStacks.get(0).isEmpty()) {
				this.furnaceItemStacks.set(0, ItemStack.EMPTY);
			}

			if (this.furnaceItemStacks.get(1).isEmpty()) {
				this.furnaceItemStacks.set(1, ItemStack.EMPTY);
			}

			drain(MetalMeltRecipes.instance().getWaterCost(itemstack), true);
			world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 0.8f + ((float) world.rand.nextInt(4)) * 0.1f);
		}
	}

	public static int getItemBurnTime(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else {
			Item item = stack.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.AIR) {
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.WOODEN_SLAB) {
					return 150;
				}

				if (block.getDefaultState().getMaterial() == Material.WOOD) {
					return 300;
				}

				if (block == Blocks.COAL_BLOCK) {
					return 16000;
				}
			}

			if (item instanceof ItemTool && "WOOD".equals(((ItemTool) item).getToolMaterialName())) return 200;
			if (item instanceof ItemSword && "WOOD".equals(((ItemSword) item).getToolMaterialName())) return 200;
			if (item instanceof ItemHoe && "WOOD".equals(((ItemHoe) item).getMaterialName())) return 200;
			if (item == Items.STICK) return 100;
			if (item == Items.COAL) return 1600;
			if (item == Items.LAVA_BUCKET) return 20000;
			if (item == Item.getItemFromBlock(Blocks.SAPLING)) return 100;
			if (item == Items.BLAZE_ROD) return 2400;
			return GameRegistry.getFuelValue(stack);
		}
	}

	public static boolean isItemFuel(ItemStack stack) {
		return getItemBurnTime(stack) > 0;
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
		if (index == 3 || index == 5) {
			return false;
		} else if (index != 2) {
			return true;
		} else {
			ItemStack itemstack = this.furnaceItemStacks.get(2);
			return isItemFuel(stack) || SlotMPConstructorFuel.isBucket(stack) && (itemstack.isEmpty() || itemstack.getItem() != Items.BUCKET);
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
		if (direction == EnumFacing.DOWN && index == 1) {
			Item item = stack.getItem();

			if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
				return false;
			}
		}

		return true;
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
				return this.furnaceBurnTime;
			case 1:
				return this.currentItemBurnTime;
			case 2:
				return this.cookTime;
			case 3:
				return this.totalCookTime;
			case 4:
				return this.heldWaterAmount;
			case 5:
				return heldWaterAmountMax;
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
				this.currentItemBurnTime = value;
				break;
			case 2:
				this.cookTime = value;
				break;
			case 3:
				this.totalCookTime = value;
				break;
			case 4:
				this.heldWaterAmount = value;
				break;
			default:

		}
	}

	@Override
	public int getFieldCount() {
		return 6;
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
			return new FluidStack(FluidRegistry.WATER, getFluidAmount());
		return null;
	}

	@Override
	public int getFluidAmount() {
		return heldWaterAmount;
	}

	@Override
	public int getCapacity() {
		return heldWaterAmountMax;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		int maxFillAmount = getCapacity() - getFluidAmount();
		if (resource.getFluid().equals(FluidRegistry.WATER) && maxFillAmount > 0) {
			if (maxFillAmount < resource.amount) {
				if (doFill)
					heldWaterAmount += maxFillAmount;
				return maxFillAmount;
			} else {
				if (doFill)
					heldWaterAmount += resource.amount;
				return resource.amount;
			}
		}
		return 0;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (resource.amount > 0 && resource.getFluid().equals(FluidRegistry.WATER)) {
			if (resource.amount < getFluidAmount()) {
				if (doDrain)
					heldWaterAmount -= resource.amount;
				return new FluidStack(FluidRegistry.WATER, resource.amount);
			} else {
				int prevHeldWater = getFluidAmount();
				if (doDrain)
					heldWaterAmount = 0;
				return new FluidStack(FluidRegistry.WATER, prevHeldWater);
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
					heldWaterAmount -= maxDrain;
				return new FluidStack(FluidRegistry.WATER, maxDrain);
			} else {
				int prevHeldWater = getFluidAmount();
				if (doDrain)
					heldWaterAmount = 0;
				return new FluidStack(FluidRegistry.WATER, prevHeldWater);
			}
		}
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);

	}
}