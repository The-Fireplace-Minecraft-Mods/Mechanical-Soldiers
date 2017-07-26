package the_fireplace.mechsoldiers.compat.jei;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CPUMeltRecipeHandler implements IRecipeHandler<CPUMeltRecipe> {
	@Override
	public Class getRecipeClass() {
		return CPUMeltRecipe.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return "mechsoldiers.cpu_melt";
	}

	@Override
	public String getRecipeCategoryUid(CPUMeltRecipe recipe) {
		return "mechsoldiers.cpu_melt";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CPUMeltRecipe recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(CPUMeltRecipe recipe) {
		if (recipe.getOutputs().isEmpty()) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has no outputs. {}", recipeInfo);
			return false;
		}
		int inputCount = 0;
		for (Object input : recipe.getInputs()) {
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
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has no inputs. {}", recipeInfo);
			return false;
		}
		if (inputCount < 2) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe does not have enough inputs. {}", recipeInfo);
			return false;
		}
		return true;
	}
}
