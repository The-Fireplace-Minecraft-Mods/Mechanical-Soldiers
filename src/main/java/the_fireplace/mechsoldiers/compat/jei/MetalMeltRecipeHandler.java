package the_fireplace.mechsoldiers.compat.jei;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MetalMeltRecipeHandler implements IRecipeWrapperFactory<MetalMeltRecipe> {
	@Override
	public IRecipeWrapper getRecipeWrapper(MetalMeltRecipe recipe) {
		return recipe;
	}
}
