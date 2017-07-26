package the_fireplace.mechsoldiers.container;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidUtil;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartConstructor;

import javax.annotation.Nullable;

public class SlotLava extends Slot {
	public SlotLava(IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
		super(inventoryIn, slotIndex, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(@Nullable ItemStack itemstack) {
		return FluidUtil.getFluidContained(itemstack) != null && FluidUtil.getFluidContained(itemstack).getFluid() == FluidRegistry.LAVA;
	}
}