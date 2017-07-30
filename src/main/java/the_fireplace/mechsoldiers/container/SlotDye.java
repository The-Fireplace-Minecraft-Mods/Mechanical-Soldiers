package the_fireplace.mechsoldiers.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartStainer;

/**
 * @author The_Fireplace
 */
public class SlotDye extends Slot {
	IInventory inv;
	String color;

	public SlotDye(String color, IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		inv = inventoryIn;
		this.color = color;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return TileEntityPartStainer.isDyeColor(stack, color);
	}
}
