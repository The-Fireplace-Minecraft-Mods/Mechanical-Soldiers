package the_fireplace.mechsoldiers.registry;

import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import the_fireplace.overlord.Overlord;

import java.util.Map;
import java.util.Map.Entry;

@MethodsReturnNonnullByDefault
public class CPUMeltRecipes {
	private static final CPUMeltRecipes CPU_MELT_RECIPES = new CPUMeltRecipes();
	private final Map<ItemStack, Object> smeltingListRight = Maps.newHashMap();
	private final Map<ItemStack, Object> smeltingListLeft = Maps.newHashMap();

	public static CPUMeltRecipes instance() {
		return CPU_MELT_RECIPES;
	}

	private CPUMeltRecipes() {
	}

	public void addCPURecipe(ItemStack output, Object leftInput, Object rightInput) {
		if (rightInput instanceof Item)
			rightInput = new ItemStack((Item) rightInput);
		if (rightInput instanceof Block)
			rightInput = new ItemStack((Block) rightInput);
		if (leftInput instanceof Item)
			leftInput = new ItemStack((Item) leftInput);
		if (leftInput instanceof Block)
			leftInput = new ItemStack((Block) leftInput);
		if (!getMeltingResult(leftInput, rightInput).isEmpty()) {
			Overlord.logInfo("Ignored melting recipe with conflicting input: " + output + " = " + leftInput + " + " + rightInput);
			return;
		}
		this.smeltingListRight.put(output, rightInput);
		this.smeltingListLeft.put(output, leftInput);
	}

	public ItemStack getMeltingResult(Object leftObj, Object rightObj) {
		ItemStack compStackLeft = ItemStack.EMPTY;
		boolean useCompDictLeft = true;
		ItemStack compStackRight = ItemStack.EMPTY;
		boolean useCompDictRight = true;
		if (leftObj instanceof ItemStack) {
			compStackLeft = (ItemStack) leftObj;
			useCompDictLeft = false;
		}
		if (useCompDictLeft && !(leftObj instanceof String))
			throw new IllegalArgumentException("input must be ItemStack or String: " + leftObj.toString());
		if (rightObj instanceof ItemStack) {
			compStackRight = (ItemStack) rightObj;
			useCompDictRight = false;
		}
		if (useCompDictRight && !(rightObj instanceof String))
			throw new IllegalArgumentException("input must be ItemStack or String: " + rightObj.toString());
		for (int i = 0; i < (useCompDictLeft ? OreDictionary.getOres((String) leftObj).size() : 1); i++) {
			if (useCompDictLeft)
				compStackLeft = OreDictionary.getOres((String) leftObj).get(i);
			for (int j = 0; j < (useCompDictRight ? OreDictionary.getOres((String) rightObj).size() : 1); j++) {
				if (useCompDictRight)
					compStackRight = OreDictionary.getOres((String) rightObj).get(j);
				for (Entry<ItemStack, Object> rightListEntry : this.smeltingListRight.entrySet()) {
					for (Entry<ItemStack, Object> leftListEntry : this.smeltingListLeft.entrySet()) {
						Object inputObjRight = rightListEntry.getValue();
						ItemStack inputStackRight = ItemStack.EMPTY;
						boolean useDictRight = true;
						Object inputObjLeft = leftListEntry.getValue();
						ItemStack inputStackLeft = ItemStack.EMPTY;
						boolean useDictLeft = true;
						if (inputObjRight instanceof ItemStack) {
							inputStackRight = (ItemStack) inputObjRight;
							useDictRight = false;
						}
						if (useDictRight && !(inputObjRight instanceof String))
							throw new IllegalArgumentException("input must be ItemStack or String: " + inputObjRight.toString());
						if (inputObjLeft instanceof ItemStack) {
							inputStackLeft = (ItemStack) inputObjLeft;
							useDictLeft = false;
						}
						if (useDictLeft && !(inputObjLeft instanceof String))
							throw new IllegalArgumentException("input must be ItemStack or String: " + inputObjLeft.toString());
						for (int k = 0; k < (useDictRight ? OreDictionary.getOres((String) inputObjRight).size() : 1); k++) {
							if (useDictRight)
								inputStackRight = OreDictionary.getOres((String) inputObjRight).get(k);
							for (int l = 0; l < (useDictLeft ? OreDictionary.getOres((String) inputObjLeft).size() : 1); l++) {
								if (useDictLeft)
									inputStackLeft = OreDictionary.getOres((String) inputObjLeft).get(l);
								if (this.compareItemStacks(compStackLeft, inputStackLeft) && this.compareItemStacks(compStackRight, inputStackRight) && rightListEntry.getKey().equals(leftListEntry.getKey())) {
									return rightListEntry.getKey();
								}
							}
						}
					}
				}
			}
		}

		return ItemStack.EMPTY;
	}

	private boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
		return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == OreDictionary.WILDCARD_VALUE || stack2.getMetadata() == stack1.getMetadata());
	}

	public Map<ItemStack, Object> getSmeltingListRight() {
		return this.smeltingListRight;
	}

	public Map<ItemStack, Object> getSmeltingListLeft() {
		return this.smeltingListLeft;
	}
}