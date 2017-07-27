package the_fireplace.mechsoldiers.compat.jei;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MetalMeltRecipeHandler implements IRecipeWrapperFactory<MetalMeltRecipe> {
	@Override
	public IRecipeWrapper getRecipeWrapper(MetalMeltRecipe recipe) {
		return recipe;
	}
}
