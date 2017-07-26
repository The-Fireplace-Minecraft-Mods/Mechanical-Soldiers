package the_fireplace.mechsoldiers.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import the_fireplace.mechsoldiers.client.gui.GuiCPUMelter;
import the_fireplace.mechsoldiers.client.gui.GuiMetalPartConstructor;
import the_fireplace.mechsoldiers.container.ContainerCPUMelter;
import the_fireplace.mechsoldiers.container.ContainerMetalPartConstructor;

import javax.annotation.ParametersAreNonnullByDefault;

@JEIPlugin
@ParametersAreNonnullByDefault
public class MechSoldiersJEIPlugin implements IModPlugin {
	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {

	}

	@Override
	public void register(IModRegistry registry) {
		IJeiHelpers helpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = helpers.getGuiHelper();
		registry.addRecipeCategories(new MetalMeltCategory(guiHelper), new CPUMeltCategory(guiHelper));

		registry.addRecipeHandlers(new MetalMeltRecipeHandler(), new CPUMeltRecipeHandler());
		registry.addRecipeClickArea(GuiMetalPartConstructor.class, 75, 34, 34, 17, "mechsoldiers.metal_melt");
		registry.addRecipeClickArea(GuiCPUMelter.class, 73, 30, 38, 25, "mechsoldiers.cpu_melt");

		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();

		recipeTransferRegistry.addRecipeTransferHandler(ContainerMetalPartConstructor.class, "mechsoldiers.metal_melt", 0, 2, 0, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCPUMelter.class, "mechsoldiers.cpu_melt", 1, 2, 0, 36);

		//registry.addRecipeCatalyst(new ItemStack(MechSoldiers.metal_part_constructor), "mechsoldiers.metal_melt");

		registry.addRecipes(RecipeMaker.getMetalMeltRecipes(helpers));
		registry.addRecipes(RecipeMaker.getCPUMeltRecipes(helpers));
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

	}
}
