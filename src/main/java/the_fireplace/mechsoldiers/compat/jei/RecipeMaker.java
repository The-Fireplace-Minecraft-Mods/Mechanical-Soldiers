package the_fireplace.mechsoldiers.compat.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
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
			if (isRecipeValid(recipe))
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
			inputs.add(input1);
			inputs.add(input2);
			CPUMeltRecipe recipe = new CPUMeltRecipe(inputs, output);
			if (isRecipeValid(recipe))
				recipes.add(recipe);
		}

		return recipes;
	}

	public static boolean isRecipeValid(MetalMeltRecipe recipe) {
		if (recipe.outputs.isEmpty()) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, recipe);
			Log.get().error("Recipe has no outputs. {}", recipeInfo);
			return false;
		}
		int inputCount = 0;
		for (Object input : recipe.input) {
			if (input instanceof List) {
				if (((List) input).isEmpty()) {
					// missing items for an oreDict name. This is normal behavior, but the recipe is invalid.
					return false;
				}
			}
			if (input != null) {
				inputCount++;
			}
		}
		if (inputCount == 0) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, recipe);
			Log.get().error("Recipe has no inputs. {}", recipeInfo);
			return false;
		}
		if (inputCount < 2) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, recipe);
			Log.get().error("Recipe does not have enough inputs. {}", recipeInfo);
			return false;
		}
		return true;
	}

	public static boolean isRecipeValid(CPUMeltRecipe recipe) {
		if (recipe.outputs.isEmpty()) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, recipe);
			Log.get().error("Recipe has no outputs. {}", recipeInfo);
			return false;
		}
		int inputCount = 0;
		for (Object input : recipe.input) {
			if (input instanceof List) {
				if (((List) input).isEmpty()) {
					// missing items for an oreDict name. This is normal behavior, but the recipe is invalid.
					return false;
				}
			}
			if (input != null) {
				inputCount++;
			}
		}
		if (inputCount == 0) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, recipe);
			Log.get().error("Recipe has no inputs. {}", recipeInfo);
			return false;
		}
		if (inputCount < 2) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, recipe);
			Log.get().error("Recipe does not have enough inputs. {}", recipeInfo);
			return false;
		}
		return true;
	}
}
