package the_fireplace.mechsoldiers.compat.ticon;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import the_fireplace.mechsoldiers.compat.IModCompat;
import the_fireplace.mechsoldiers.registry.MechCraftingRecipes;
import the_fireplace.mechsoldiers.registry.PartRegistry;
import the_fireplace.mechsoldiers.util.EnumPartType;

public class TiconCompat implements IModCompat {

	public static final Item skeleton_cast = new Item().setUnlocalizedName("cast_skeleton").setRegistryName("cast_skeleton").setCreativeTab(TinkerRegistry.tabSmeltery);
	public static final Item joints_cast = new Item().setUnlocalizedName("cast_joints").setRegistryName("cast_joints").setCreativeTab(TinkerRegistry.tabSmeltery);
	public static final Item skeleton_cast_clay = new Item().setUnlocalizedName("clay_cast_skeleton").setRegistryName("clay_cast_skeleton").setCreativeTab(TinkerRegistry.tabSmeltery);
	public static final Item joints_cast_clay = new Item().setUnlocalizedName("clay_cast_joints").setRegistryName("clay_cast_joints").setCreativeTab(TinkerRegistry.tabSmeltery);

	@Override
	public void preInit(boolean isClient) {
		MinecraftForge.EVENT_BUS.register(this);
		if(isClient)
			MinecraftForge.EVENT_BUS.register(new TiconClientEvents());
	}

	@Override
	public void init() {
		ItemStack skeletonCast = new ItemStack(skeleton_cast);
		ItemStack jointsCast = new ItemStack(joints_cast);
		ItemStack skeletonCastClay = new ItemStack(skeleton_cast_clay);
		ItemStack jointsCastClay = new ItemStack(joints_cast_clay);
		if(TinkerFluids.iron != null && FluidRegistry.isFluidRegistered(TinkerFluids.iron)){
			TinkerRegistry.registerMelting(MechCraftingRecipes.iron_joints, TinkerFluids.iron, Material.VALUE_Ingot);
			TinkerRegistry.registerMelting(MechCraftingRecipes.iron_skeleton, TinkerFluids.iron, Material.VALUE_Block);
			TinkerRegistry.registerTableCasting(MechCraftingRecipes.iron_joints, jointsCast, TinkerFluids.iron, Material.VALUE_Ingot);
			TinkerRegistry.registerTableCasting(MechCraftingRecipes.iron_skeleton, skeletonCast, TinkerFluids.iron, Material.VALUE_Block);

			if(Config.claycasts) {
				TinkerRegistry.registerTableCasting(new CastingRecipe(MechCraftingRecipes.iron_skeleton, RecipeMatch.of(skeletonCastClay), new FluidStack(TinkerFluids.iron, Material.VALUE_Block), true, false));
				TinkerRegistry.registerTableCasting(new CastingRecipe(MechCraftingRecipes.iron_joints, RecipeMatch.of(jointsCastClay), new FluidStack(TinkerFluids.iron, Material.VALUE_Ingot), true, false));
			}
		}
		if(TinkerFluids.gold != null && FluidRegistry.isFluidRegistered(TinkerFluids.gold)){
			TinkerRegistry.registerMelting(MechCraftingRecipes.gold_joints, TinkerFluids.gold, Material.VALUE_Ingot);
			TinkerRegistry.registerMelting(MechCraftingRecipes.gold_skeleton, TinkerFluids.gold, Material.VALUE_Block);
			TinkerRegistry.registerTableCasting(MechCraftingRecipes.gold_joints, jointsCast, TinkerFluids.gold, Material.VALUE_Ingot);
			TinkerRegistry.registerTableCasting(MechCraftingRecipes.gold_skeleton, skeletonCast, TinkerFluids.gold, Material.VALUE_Block);

			if(Config.claycasts) {
				TinkerRegistry.registerTableCasting(new CastingRecipe(MechCraftingRecipes.gold_skeleton, RecipeMatch.of(skeletonCastClay), new FluidStack(TinkerFluids.gold, Material.VALUE_Block), true, false));
				TinkerRegistry.registerTableCasting(new CastingRecipe(MechCraftingRecipes.gold_joints, RecipeMatch.of(jointsCastClay), new FluidStack(TinkerFluids.gold, Material.VALUE_Ingot), true, false));
			}
			//Casts
			for(FluidStack fs : TinkerSmeltery.castCreationFluids) {
				for(ItemStack stack : PartRegistry.getPartsOfType(EnumPartType.SKELETON))
					TinkerRegistry.registerTableCasting(new CastingRecipe(skeletonCast, RecipeMatch.of(stack), fs, true, true));
				for(ItemStack stack : PartRegistry.getPartsOfType(EnumPartType.JOINTS))
					TinkerRegistry.registerTableCasting(new CastingRecipe(jointsCast, RecipeMatch.of(stack), fs, true, true));
			}
			if(Config.claycasts)
				for(FluidStack fs : TinkerSmeltery.clayCreationFluids) {
					for(ItemStack stack : PartRegistry.getPartsOfType(EnumPartType.SKELETON))
						TinkerRegistry.registerTableCasting(new CastingRecipe(skeletonCastClay, RecipeMatch.of(stack), fs, true, true));
					for(ItemStack stack : PartRegistry.getPartsOfType(EnumPartType.JOINTS))
						TinkerRegistry.registerTableCasting(new CastingRecipe(jointsCastClay, RecipeMatch.of(stack), fs, true, true));
				}
		}
		if(TinkerFluids.steel != null && FluidRegistry.isFluidRegistered(TinkerFluids.steel)){
			TinkerRegistry.registerMelting(MechCraftingRecipes.steel_joints, TinkerFluids.steel, Material.VALUE_Ingot);
			TinkerRegistry.registerMelting(MechCraftingRecipes.steel_skeleton, TinkerFluids.steel, Material.VALUE_Block);
			TinkerRegistry.registerTableCasting(MechCraftingRecipes.steel_joints, jointsCast, TinkerFluids.steel, Material.VALUE_Ingot);
			TinkerRegistry.registerTableCasting(MechCraftingRecipes.steel_skeleton, skeletonCast, TinkerFluids.steel, Material.VALUE_Block);

			if(Config.claycasts) {
				TinkerRegistry.registerTableCasting(new CastingRecipe(MechCraftingRecipes.steel_skeleton, RecipeMatch.of(skeletonCastClay), new FluidStack(TinkerFluids.steel, Material.VALUE_Block), true, false));
				TinkerRegistry.registerTableCasting(new CastingRecipe(MechCraftingRecipes.steel_joints, RecipeMatch.of(jointsCastClay), new FluidStack(TinkerFluids.steel, Material.VALUE_Ingot), true, false));
			}
		}
		if(TinkerFluids.bronze != null && FluidRegistry.isFluidRegistered(TinkerFluids.bronze)){
			TinkerRegistry.registerMelting(MechCraftingRecipes.bronze_joints, TinkerFluids.bronze, Material.VALUE_Ingot);
			TinkerRegistry.registerMelting(MechCraftingRecipes.bronze_skeleton, TinkerFluids.bronze, Material.VALUE_Block);
			TinkerRegistry.registerTableCasting(MechCraftingRecipes.bronze_joints, jointsCast, TinkerFluids.bronze, Material.VALUE_Ingot);
			TinkerRegistry.registerTableCasting(MechCraftingRecipes.bronze_skeleton, skeletonCast, TinkerFluids.bronze, Material.VALUE_Block);

			if(Config.claycasts) {
				TinkerRegistry.registerTableCasting(new CastingRecipe(MechCraftingRecipes.bronze_skeleton, RecipeMatch.of(skeletonCastClay), new FluidStack(TinkerFluids.bronze, Material.VALUE_Block), true, false));
				TinkerRegistry.registerTableCasting(new CastingRecipe(MechCraftingRecipes.bronze_joints, RecipeMatch.of(jointsCastClay), new FluidStack(TinkerFluids.bronze, Material.VALUE_Ingot), true, false));
			}
		}
		//TODO: Invar
	}

	@SubscribeEvent
	public void itemRegister(RegistryEvent.Register<Item> event){
		event.getRegistry().register(skeleton_cast);
		event.getRegistry().register(joints_cast);
		event.getRegistry().register(skeleton_cast_clay);
		event.getRegistry().register(joints_cast_clay);
	}
}
