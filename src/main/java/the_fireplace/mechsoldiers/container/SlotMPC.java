package the_fireplace.mechsoldiers.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import the_fireplace.mechsoldiers.registry.MetalMeltRecipes;

/**
 * @author The_Fireplace
 */
public class SlotMPC extends Slot {
	IInventory inv;
	String position;

	public SlotMPC(IInventory inventoryIn, int index, int xPosition, int yPosition, String position) {
		super(inventoryIn, index, xPosition, yPosition);
		inv = inventoryIn;
		this.position = position;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return isStackValid(position, stack);
	}

	public static boolean isStackValid(String position, ItemStack stack){
		boolean valid = false;
		switch(position.toLowerCase()){
			case "left":
				if(MetalMeltRecipes.instance().getSmeltingListLeft().containsValue(new ItemStack(stack.getItem(), 1, stack.getMetadata())) || MetalMeltRecipes.instance().getSmeltingListLeft().containsValue(new ItemStack(stack.getItem(), 1, OreDictionary.WILDCARD_VALUE)))
					valid = true;
				else {
					for(int oreId:OreDictionary.getOreIDs(stack))
						if(MetalMeltRecipes.instance().getSmeltingListLeft().containsValue(OreDictionary.getOreName(oreId))) {
							valid = true;
							break;
						}
				}
				return valid;
			case "right":
				if(MetalMeltRecipes.instance().getSmeltingListRight().containsValue(new ItemStack(stack.getItem(), 1, stack.getMetadata())) || MetalMeltRecipes.instance().getSmeltingListRight().containsValue(new ItemStack(stack.getItem(), 1, OreDictionary.WILDCARD_VALUE)))
					valid = true;
				else {
					for(int oreId:OreDictionary.getOreIDs(stack))
						if(MetalMeltRecipes.instance().getSmeltingListRight().containsValue(OreDictionary.getOreName(oreId))) {
							valid = true;
							break;
						}
				}
				return valid;
			default:
				return false;
		}
	}
}
