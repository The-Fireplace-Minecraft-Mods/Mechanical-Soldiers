package the_fireplace.mechsoldiers.registry;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Map.Entry;

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
		if(rightInput instanceof Item)
			rightInput = new ItemStack((Item)rightInput);
		if(rightInput instanceof Block)
			rightInput = new ItemStack((Block) rightInput);
		if(leftInput instanceof Item)
			leftInput = new ItemStack((Item)leftInput);
		if(leftInput instanceof Block)
			leftInput = new ItemStack((Block) leftInput);
		if (getMeltingResult(rightInput, leftInput) != null) {
			FMLLog.info("Ignored melting recipe with conflicting input: " + output + " = " + rightInput + " + " + leftInput);
			return;
		}
		this.smeltingListRight.put(output, rightInput);
		this.smeltingListLeft.put(output, leftInput);
	}

	@Nullable
	public ItemStack getMeltingResult(Object stack1, Object stack2) {
		ItemStack compStack1 = null;
		boolean useCompDict1 = true;
		ItemStack compStack2 = null;
		boolean useCompDict2 = true;
		if(stack1 instanceof ItemStack) {
			compStack1 = (ItemStack) stack1;
			useCompDict1 = false;
		}
		if(useCompDict1 && !(stack1 instanceof String))
			throw new IllegalArgumentException("input must be ItemStack or String: "+stack1.toString());
		if(stack2 instanceof ItemStack) {
			compStack2 = (ItemStack) stack2;
			useCompDict2 = false;
		}
		if(useCompDict2 && !(stack2 instanceof String))
			throw new IllegalArgumentException("input must be ItemStack or String: "+stack2.toString());
		for(int i=0;i<(useCompDict1 ? OreDictionary.getOres((String)stack1).size() : 1);i++) {
			if(useCompDict1)
				compStack1 = OreDictionary.getOres((String)stack1).get(i);
			for(int j=0;j<(useCompDict2 ? OreDictionary.getOres((String)stack2).size() : 1);j++) {
				if(useCompDict2)
					compStack2 = OreDictionary.getOres((String)stack2).get(j);
				for (Entry<ItemStack, Object> entry : this.smeltingListRight.entrySet()) {
					for (Entry<ItemStack, Object> entry2 : this.smeltingListLeft.entrySet()) {
						Object input1 = entry.getValue();
						ItemStack inputStack1 = null;
						boolean useDict1 = true;
						Object input2 = entry2.getValue();
						ItemStack inputStack2 = null;
						boolean useDict2 = true;
						if(input1 instanceof ItemStack) {
							inputStack1 = (ItemStack) input1;
							useDict1 = false;
						}
						if(useDict1 && !(input1 instanceof String))
							throw new IllegalArgumentException("input must be ItemStack or String: "+input1.toString());
						if(input2 instanceof ItemStack) {
							inputStack2 = (ItemStack) input2;
							useDict2 = false;
						}
						if(useDict2 && !(input2 instanceof String))
							throw new IllegalArgumentException("input must be ItemStack or String: "+input2.toString());
						for(int k=0;k<(useDict1 ? OreDictionary.getOres((String)input1).size() : 1);k++) {
							if(useDict1)
								inputStack1 = OreDictionary.getOres((String)input1).get(k);
							for(int l=0;l<(useDict2 ? OreDictionary.getOres((String)input2).size() : 1);l++) {
								if(useDict2)
									inputStack2 = OreDictionary.getOres((String)input2).get(l);
								if (this.compareItemStacks(compStack1, inputStack1) && this.compareItemStacks(compStack2, inputStack2) && entry.getKey().equals(entry2.getKey())) {
									return entry.getKey();
								}
							}
						}
					}
				}
			}
		}

		return null;
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