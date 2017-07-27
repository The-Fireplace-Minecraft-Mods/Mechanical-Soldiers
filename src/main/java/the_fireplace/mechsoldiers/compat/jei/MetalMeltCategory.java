package the_fireplace.mechsoldiers.compat.jei;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MetalMeltCategory implements IRecipeCategory {

	@Nonnull
	private final IDrawable background;

	public MetalMeltCategory(IGuiHelper guiHelper) {
		super();
		ResourceLocation location = new ResourceLocation(MechSoldiers.MODID, "textures/gui/metal_part_constructor.png");
		background = guiHelper.createDrawable(location, 38, 10, 105, 62);
	}

	@Override
	public String getUid() {
		return "mechsoldiers.metal_melt";
	}

	@Override
	public String getTitle() {
		return Overlord.proxy.translateToLocal("jei.metal_melt");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Nullable
	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {

	}

	@Override
	public String getModName() {
		return MechSoldiers.MODNAME;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 27, 6);
		guiItemStacks.init(1, true, 5, 6);
		guiItemStacks.init(3, false, 77, 24);

		guiItemStacks.set(ingredients);
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Lists.newArrayList();
	}
}
