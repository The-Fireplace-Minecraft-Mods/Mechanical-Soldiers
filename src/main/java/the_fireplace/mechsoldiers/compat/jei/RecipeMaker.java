package the_fireplace.mechsoldiers.compat.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import the_fireplace.mechsoldiers.registry.CPUMeltRecipes;
import the_fireplace.mechsoldiers.registry.MetalMeltRecipes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeMaker {
	public static IStackHelper stackHelper;
	@Nonnull
	public static List<MetalMeltRecipe> getMetalMeltRecipes(IJeiHelpers helpers) {
		RecipeMaker.stackHelper = helpers.getStackHelper();
		MetalMeltRecipes furnaceRecipes = MetalMeltRecipes.instance();
		Map<ItemStack, Object> smeltingRight = furnaceRecipes.getSmeltingListRight();
		Map<ItemStack, Object> smeltingLeft = furnaceRecipes.getSmeltingListLeft();

		List<MetalMeltRecipe> recipes = new ArrayList<>();

		for (Map.Entry<ItemStack, Object> itemStackItemStackEntry : smeltingRight.entrySet()) {
			ItemStack output = itemStackItemStackEntry.getKey();
			Object input1 = smeltingLeft.get(output);
			Object input2 = itemStackItemStackEntry.getValue();

			List<Object> inputs = Lists.newLinkedList();
			inputs.add(input2);
			inputs.add(input1);
			MetalMeltRecipe recipe = new MetalMeltRecipe(inputs, output);
			recipes.add(recipe);
		}

		return recipes;
	}
	@Nonnull
	public static List<CPUMeltRecipe> getCPUMeltRecipes(IJeiHelpers helpers) {
		RecipeMaker.stackHelper = helpers.getStackHelper();
		CPUMeltRecipes furnaceRecipes = CPUMeltRecipes.instance();
		Map<ItemStack, Object> smeltingRight = furnaceRecipes.getSmeltingListRight();
		Map<ItemStack, Object> smeltingLeft = furnaceRecipes.getSmeltingListLeft();

		List<CPUMeltRecipe> recipes = new ArrayList<>();

		for (Map.Entry<ItemStack, Object> itemStackItemStackEntry : smeltingRight.entrySet()) {
			ItemStack output = itemStackItemStackEntry.getKey();
			Object input1 = smeltingLeft.get(output);
			Object input2 = itemStackItemStackEntry.getValue();

			List<Object> inputs = Lists.newLinkedList();
			inputs.add(input2);
			inputs.add(input1);
			CPUMeltRecipe recipe = new CPUMeltRecipe(inputs, output);
			recipes.add(recipe);
		}

		return recipes;
	}
}
