package the_fireplace.mechsoldiers.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class TileEntityMiniTank extends TileEntity implements IFluidHandler, IFluidTank {
	private int heldFluidAmount;
	public static final int maxCapacity = 12000;
	private Fluid heldFluid;

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
		return new TextComponentTranslation("tile.mini_tank.name");
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.heldFluidAmount = compound.getInteger("HeldFluid");
		this.heldFluid = FluidRegistry.getFluid(compound.getString("FluidId"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("HeldFluid", this.heldFluidAmount);
		if(heldFluid != null)
			compound.setString("FluidId", FluidRegistry.getFluidName(heldFluid));
		return compound;
	}

	@Nullable
	@Override
	public FluidStack getFluid() {
		if(getFluidAmount() > 0)
			return new FluidStack(heldFluid, heldFluidAmount);
		return null;
	}

	@Override
	public int getFluidAmount() {
		return heldFluidAmount;
	}

	@Override
	public int getCapacity() {
		return getBlockType().getRegistryName().toString().contains("full") ? maxCapacity : maxCapacity /2;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[]{new FluidTankProperties(new FluidStack(getFluid(), getFluidAmount()), getCapacity())};
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		int maxFillAmount = getCapacity() - getFluidAmount();
		if (maxFillAmount > 0 && (heldFluid == null || resource.getFluid().equals(heldFluid))) {
			if(doFill && heldFluid == null)
				heldFluid = resource.getFluid();
			if (maxFillAmount < resource.amount) {
				if (doFill) {
					heldFluidAmount += maxFillAmount;
				}
				return maxFillAmount;
			} else {
				if (doFill) {
					heldFluidAmount += resource.amount;
				}
				return resource.amount;
			}
		}
		return 0;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (resource.amount > 0 && resource.getFluid().equals(heldFluid)) {
			if (resource.amount < getFluidAmount()) {
				if (doDrain)
					heldFluidAmount -= resource.amount;
				return new FluidStack(heldFluid, resource.amount);
			} else {
				int prevHeldAmount = getFluidAmount();
				if(heldFluid == null || prevHeldAmount <= 0)
					return null;
				FluidStack out = new FluidStack(heldFluid, prevHeldAmount);
				if (doDrain) {
					heldFluidAmount = 0;
					heldFluid = null;
				}
				return out;
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
					heldFluidAmount -= maxDrain;
				return new FluidStack(heldFluid, maxDrain);
			} else {
				int prevHeldAmount = getFluidAmount();
				if(heldFluid == null || prevHeldAmount <= 0)
					return null;
				FluidStack out = new FluidStack(heldFluid, prevHeldAmount);
				if (doDrain) {
					heldFluidAmount = 0;
					heldFluid = null;
				}
				return out;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) this;
		return super.getCapability(capability, facing);
	}
}
