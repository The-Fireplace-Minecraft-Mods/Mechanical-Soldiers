package the_fireplace.mechsoldiers.compat.ie;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.IEContent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import the_fireplace.mechsoldiers.compat.IModCompat;
import the_fireplace.mechsoldiers.registry.MechCraftingRecipes;

public class IECompat implements IModCompat {

	public static final Item skeleton_mold = new Item().setUnlocalizedName("mold_skeleton").setRegistryName("mold_skeleton").setCreativeTab(ImmersiveEngineering.creativeTab);
	public static final Item joints_mold = new Item().setUnlocalizedName("mold_joints").setRegistryName("mold_joints").setCreativeTab(ImmersiveEngineering.creativeTab);

	@Override
	public void preInit(boolean isClient) {
		MinecraftForge.EVENT_BUS.register(this);
		if(isClient)
			MinecraftForge.EVENT_BUS.register(new IEClientEvents());
		BlueprintCraftingRecipe.addRecipe("molds", new ItemStack(skeleton_mold), "plateSteel","plateSteel","plateSteel","plateSteel","plateSteel",new ItemStack(IEContent.itemTool, 1, 1));
		BlueprintCraftingRecipe.addRecipe("molds", new ItemStack(joints_mold), "plateSteel","plateSteel","plateSteel","plateSteel","plateSteel",new ItemStack(IEContent.itemTool, 1, 1));

		ItemStack skeletonMold = new ItemStack(skeleton_mold);
		ItemStack jointsMold = new ItemStack(joints_mold);
		MetalPressRecipe.addRecipe(MechCraftingRecipes.iron_skeleton, "blockIron", skeletonMold, 3200);
		MetalPressRecipe.addRecipe(MechCraftingRecipes.gold_skeleton, "blockGold", skeletonMold, 2800);
		MetalPressRecipe.addRecipe(MechCraftingRecipes.steel_skeleton, "blockSteel", skeletonMold, 3200);
		MetalPressRecipe.addRecipe(MechCraftingRecipes.bronze_skeleton, "blockBronze", skeletonMold, 3200);
		MetalPressRecipe.addRecipe(MechCraftingRecipes.invar_skeleton, "blockInvar", skeletonMold, 3200);
		MetalPressRecipe.addRecipe(MechCraftingRecipes.iron_joints, "ingotIron", jointsMold, 1600);
		MetalPressRecipe.addRecipe(MechCraftingRecipes.gold_joints, "ingotGold", jointsMold, 1400);
		MetalPressRecipe.addRecipe(MechCraftingRecipes.bronze_joints, "ingotBronze", jointsMold, 1600);
		MetalPressRecipe.addRecipe(MechCraftingRecipes.invar_joints, "ingotInvar", jointsMold, 1600);
		MetalPressRecipe.addRecipe(MechCraftingRecipes.steel_joints, "ingotSteel", jointsMold, 1600);
	}

	@SubscribeEvent
	public void itemRegister(RegistryEvent.Register<Item> event){
		event.getRegistry().register(skeleton_mold);
		event.getRegistry().register(joints_mold);
	}
}
