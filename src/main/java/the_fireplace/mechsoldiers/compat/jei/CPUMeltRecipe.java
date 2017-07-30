package the_fireplace.mechsoldiers.compat.jei;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CPUMeltRecipe implements IRecipeWrapper {
	@Nonnull
	final List<List<ItemStack>> input;
	@Nonnull
	final List<ItemStack> outputs;

	public CPUMeltRecipe(List<Object> input, ItemStack output) {
		this.input = RecipeMaker.stackHelper.expandRecipeItemStackInputs(input);
		this.outputs = Collections.singletonList(output);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, input);
		ingredients.setOutputLists(ItemStack.class, Collections.singletonList(outputs));
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Lists.newArrayList();
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}
}
