package the_fireplace.mechsoldiers.registry;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Map.Entry;

public class MetalMeltRecipes {
	private static final MetalMeltRecipes METAL_MELT_RECIPES = new MetalMeltRecipes();
	private final Map<ItemStack, Object> smeltingList1 = Maps.newHashMap();
	private final Map<ItemStack, Object> smeltingList2 = Maps.newHashMap();

	public static MetalMeltRecipes instance() {
		return METAL_MELT_RECIPES;
	}

	private MetalMeltRecipes() {
	}

	public void addMeltingRecipe(Block output, Object input1, Object input2) {
		this.addMeltingRecipe(Item.getItemFromBlock(output), input1, input2);
	}

	public void addMeltingRecipe(Item output, Object input1, Object input2) {
		this.addMeltingRecipe(new ItemStack(output, 1, OreDictionary.WILDCARD_VALUE), input1, input2);
	}

	public void addMeltingRecipe(ItemStack output, Object input1, Object input2) {
		if(input1 instanceof Item)
			input1 = new ItemStack((Item)input1);
		if(input1 instanceof Block)
			input1 = new ItemStack((Block) input1);
		if(input2 instanceof Item)
			input2 = new ItemStack((Item)input2);
		if(input2 instanceof Block)
			input2 = new ItemStack((Block) input2);
		if (getMeltingResult(input1, input2) != null) {
			FMLLog.info("Ignored melting recipe with conflicting input: " + output + " = " + input1 + " + " + input2);
			return;
		}
		this.smeltingList1.put(output, input1);
		this.smeltingList2.put(output, input2);
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
			for(int j=0;i<(useCompDict2 ? OreDictionary.getOres((String)stack2).size() : 1);j++) {
				if(useCompDict2)
					compStack2 = OreDictionary.getOres((String)stack2).get(j);
				for (Entry<ItemStack, Object> entry : this.smeltingList1.entrySet()) {
					for (Entry<ItemStack, Object> entry2 : this.smeltingList2.entrySet()) {
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

	public Map<ItemStack, Object> getSmeltingList1() {
		return this.smeltingList1;
	}

	public Map<ItemStack, Object> getSmeltingList2() {
		return this.smeltingList2;
	}
}