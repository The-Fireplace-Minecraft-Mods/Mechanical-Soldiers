package the_fireplace.mechsoldiers.container;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartConstructor;

import javax.annotation.Nullable;

public class SlotMPConstructorFuel extends Slot {
	public SlotMPConstructorFuel(IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
		super(inventoryIn, slotIndex, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(@Nullable ItemStack stack) {
		return TileEntityPartConstructor.isItemFuel(stack) || isBucket(stack);
	}

	public static boolean isBucket(ItemStack stack) {
		return stack != null && stack.getItem() != null && stack.getItem() == Items.BUCKET;
	}
}