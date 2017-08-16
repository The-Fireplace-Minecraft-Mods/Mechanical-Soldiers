package the_fireplace.mechsoldiers.registry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.overlord.Overlord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author The_Fireplace
 */
public class MechCraftingRecipes {
	public static ItemStack furnace = new ItemStack(Blocks.FURNACE);

	public static ItemStack robot_constructor = new ItemStack(MechSoldiers.robot_constructor);
	public static ItemStack part_stainer = new ItemStack(MechSoldiers.part_stainer);
	public static ItemStack mini_tank = new ItemStack(MechSoldiers.mini_tank);
	public static ItemStack blueprint = new ItemStack(MechSoldiers.blueprint);
	public static ItemStack iron_skeleton = new ItemStack(MechSoldiers.skeleton_iron);
	public static ItemStack steel_skeleton = new ItemStack(MechSoldiers.skeleton_steel);
	public static ItemStack invar_skeleton = new ItemStack(MechSoldiers.skeleton_invar);
	public static ItemStack bronze_skeleton = new ItemStack(MechSoldiers.skeleton_bronze);
	public static ItemStack gold_skeleton = new ItemStack(MechSoldiers.skeleton_gold);
	public static ItemStack iron_joints = new ItemStack(MechSoldiers.joints_iron);
	public static ItemStack steel_joints = new ItemStack(MechSoldiers.joints_steel);
	public static ItemStack invar_joints = new ItemStack(MechSoldiers.joints_invar);
	public static ItemStack bronze_joints = new ItemStack(MechSoldiers.joints_bronze);
	public static ItemStack gold_joints = new ItemStack(MechSoldiers.joints_gold);
	public static ItemStack gold_redstone_cpu = new ItemStack(MechSoldiers.cpu_gold_redstone);
	public static ItemStack copper_redstone_cpu = new ItemStack(MechSoldiers.cpu_copper_redstone);
	public static ItemStack electrum_redstone_cpu = new ItemStack(MechSoldiers.cpu_electrum_redstone);

	public static void register() {
		MetalMeltRecipes.instance().addMeltingRecipe(iron_skeleton, "ingotIron", "blockIron");
		MetalMeltRecipes.instance().addMeltingRecipe(steel_skeleton, "ingotSteel", "blockSteel");
		MetalMeltRecipes.instance().addMeltingRecipe(invar_skeleton, "ingotInvar", "blockInvar");
		MetalMeltRecipes.instance().addMeltingRecipe(bronze_skeleton, "ingotBronze", "blockBronze");
		MetalMeltRecipes.instance().addMeltingRecipe(gold_skeleton, "ingotGold", "blockGold");
		MetalMeltRecipes.instance().addMeltingRecipe(iron_joints, "nuggetIron", "ingotIron", MetalMeltRecipes.WATER_COST_JOINTS);
		MetalMeltRecipes.instance().addMeltingRecipe(gold_joints, "nuggetGold", "ingotGold", MetalMeltRecipes.WATER_COST_JOINTS);
		MetalMeltRecipes.instance().addMeltingRecipe(steel_joints, "nuggetSteel", "ingotSteel", MetalMeltRecipes.WATER_COST_JOINTS);
		MetalMeltRecipes.instance().addMeltingRecipe(invar_joints, "nuggetInvar", "ingotInvar", MetalMeltRecipes.WATER_COST_JOINTS);
		MetalMeltRecipes.instance().addMeltingRecipe(bronze_joints, "nuggetBronze", "ingotBronze", MetalMeltRecipes.WATER_COST_JOINTS);

		CPUMeltRecipes.instance().addCPURecipe(gold_redstone_cpu, "ingotGold", "dustRedstone");
		CPUMeltRecipes.instance().addCPURecipe(copper_redstone_cpu, "ingotCopper", "dustRedstone");
		CPUMeltRecipes.instance().addCPURecipe(electrum_redstone_cpu, "ingotElectrum", "dustRedstone");
	}
}
