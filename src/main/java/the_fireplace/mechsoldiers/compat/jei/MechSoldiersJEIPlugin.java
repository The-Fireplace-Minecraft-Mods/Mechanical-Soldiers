package the_fireplace.mechsoldiers.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;
import the_fireplace.mechsoldiers.MechSoldiers;
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
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new MetalMeltCategory(guiHelper), new CPUMeltCategory(guiHelper));
	}

	@Override
	public void register(IModRegistry registry) {
		IJeiHelpers helpers = registry.getJeiHelpers();

		registry.handleRecipes(MetalMeltRecipe.class, new MetalMeltRecipeHandler(), "mechsoldiers.metal_melt");
		registry.handleRecipes(CPUMeltRecipe.class, new CPUMeltRecipeHandler(), "mechsoldiers.cpu_melt");
		registry.addRecipeClickArea(GuiMetalPartConstructor.class, 75, 34, 34, 17, "mechsoldiers.metal_melt");
		registry.addRecipeClickArea(GuiCPUMelter.class, 73, 30, 38, 25, "mechsoldiers.cpu_melt");

		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();

		recipeTransferRegistry.addRecipeTransferHandler(ContainerMetalPartConstructor.class, "mechsoldiers.metal_melt", 0, 2, 0, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCPUMelter.class, "mechsoldiers.cpu_melt", 1, 2, 0, 36);

		registry.addRecipeCatalyst(new ItemStack(MechSoldiers.metal_part_constructor), "mechsoldiers.metal_melt");
		registry.addRecipeCatalyst(new ItemStack(MechSoldiers.cpu_melter), "mechsoldiers.cpu_melt");

		registry.addRecipes(RecipeMaker.getMetalMeltRecipes(helpers), "mechsoldiers.metal_melt");
		registry.addRecipes(RecipeMaker.getCPUMeltRecipes(helpers), "mechsoldiers.cpu_melt");
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

	}
}
