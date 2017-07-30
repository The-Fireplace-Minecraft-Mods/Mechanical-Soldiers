package the_fireplace.mechsoldiers.compat.ae2;

import appeng.api.AEApi;
import appeng.api.AEPlugin;
import net.minecraft.item.ItemStack;
import the_fireplace.mechsoldiers.registry.MechCraftingRecipes;
import the_fireplace.overlord.registry.CraftingRecipes;

import java.util.Optional;

@AEPlugin
public class AE2Compat {
	public AE2Compat() {
		addRecipes();
	}

	public void addRecipes() {
		Optional<ItemStack> logicProcessor = AEApi.instance().definitions().materials().logicProcessor().maybeStack(1);
		Optional<ItemStack> storageHousing = AEApi.instance().definitions().materials().emptyStorageCell().maybeStack(1);
		if (logicProcessor.isPresent() && storageHousing.isPresent())
			CraftingRecipes.addShapelessRecipe(MechCraftingRecipes.gold_redstone_cpu, logicProcessor.get(), storageHousing.get());
	}
}
