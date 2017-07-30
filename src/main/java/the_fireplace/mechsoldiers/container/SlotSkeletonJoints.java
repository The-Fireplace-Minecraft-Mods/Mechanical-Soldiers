package the_fireplace.mechsoldiers.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.mechsoldiers.registry.PartRegistry;
import the_fireplace.mechsoldiers.util.EnumPartType;

/**
 * @author The_Fireplace
 */
public class SlotSkeletonJoints extends Slot {
	IInventory inv;

	public SlotSkeletonJoints(IInventory inventoryIn, int index, int xPosition,
	                          int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		inv = inventoryIn;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return PartRegistry.isPartOfType(stack, EnumPartType.SKELETON) || PartRegistry.isPartOfType(stack, EnumPartType.JOINTS);
	}
}
