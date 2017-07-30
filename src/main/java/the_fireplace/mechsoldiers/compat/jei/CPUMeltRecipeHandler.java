package the_fireplace.mechsoldiers.compat.jei;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CPUMeltRecipeHandler implements IRecipeWrapperFactory<CPUMeltRecipe> {
	@Override
	public IRecipeWrapper getRecipeWrapper(CPUMeltRecipe recipe) {
		return recipe;
	}
}
