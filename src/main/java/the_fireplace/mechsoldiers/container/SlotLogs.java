package the_fireplace.mechsoldiers.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author The_Fireplace
 */
public class SlotLogs extends Slot {
	IInventory inv;

	public SlotLogs(IInventory inventoryIn, int index, int xPosition,
	                int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		inv = inventoryIn;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return ArrayUtils.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID("logWood"));
	}
}
