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
	public static ItemStack glass_pane = new ItemStack(Blocks.GLASS_PANE);
	public static ItemStack furnace = new ItemStack(Blocks.FURNACE);

	public static ItemStack robot_constructor = new ItemStack(MechSoldiers.robot_constructor);
	public static ItemStack part_stainer = new ItemStack(MechSoldiers.part_stainer);
	public static ItemStack mpc = new ItemStack(MechSoldiers.metal_part_constructor);
	public static ItemStack cpum = new ItemStack(MechSoldiers.cpu_melter);
	public static ItemStack mini_tank = new ItemStack(MechSoldiers.mini_tank);
	public static ItemStack blueprint = new ItemStack(MechSoldiers.blueprint);
	public static ItemStack blueprint2 = new ItemStack(MechSoldiers.blueprint, 2);
	public static ItemStack wood_skeleton = new ItemStack(MechSoldiers.skeleton_wood);
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
		addRecipe(robot_constructor, " d ", "drd", "ddd", 'd', "gemDiamond", 'r', new ItemStack(Overlord.skeleton_maker));
		addRecipe(wood_skeleton, "sls", "sss", "sss", 's', "stickWood", 'l', "logWood");
		addRecipe(mini_tank, "ggg", "g g", "ggg", 'g', glass_pane);
		addRecipe(mpc, "ifi", "iti", 'i', "ingotIron", 'f', furnace, 't', mini_tank);
		addRecipe(cpum, "ift", "ibi", "tfi", 'i', "ingotIron", 'f', furnace, 't', mini_tank, 'b', blueprint);
		addShapelessRecipe(blueprint2, blueprint, "paper", "dyeBlue", "dyeWhite", "nuggetIron");
		addRecipe(part_stainer, "sbb", "sib", "sbb", 's', "stone", 'i', "ingotIron", 'b', Blocks.STONEBRICK);

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

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static File RECIPE_DIR = null;
	private static final Set<String> USED_OD_NAMES = new TreeSet<>();

	private static void setupDir() {
		if (RECIPE_DIR == null) {
			RECIPE_DIR = Overlord.config.getConfigFile().toPath().resolve("../recipes/").toFile();
		}

		if (!RECIPE_DIR.exists()) {
			RECIPE_DIR.mkdir();
		}
	}

	private static void addRecipe(ItemStack result, Object... components) {
		setupDir();

		// GameRegistry.addShapedRecipe(result, components);

		Map<String, Object> json = new HashMap<>();

		List<String> pattern = new ArrayList<>();
		int i = 0;
		while (i < components.length && components[i] instanceof String) {
			pattern.add((String) components[i]);
			i++;
		}
		json.put("pattern", pattern);

		boolean isOreDict = false;
		Map<String, Map<String, Object>> key = new HashMap<>();
		Character curKey = null;
		for (; i < components.length; i++) {
			Object o = components[i];
			if (o instanceof Character) {
				if (curKey != null)
					throw new IllegalArgumentException("Provided two char keys in a row");
				curKey = (Character) o;
			} else {
				if (curKey == null)
					throw new IllegalArgumentException("Providing object without a char key");
				if (o instanceof String)
					isOreDict = true;
				key.put(Character.toString(curKey), serializeItem(o));
				curKey = null;
			}
		}
		json.put("key", key);
		json.put("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists()) {
			suffix += "_alt";
			f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}

		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addShapelessRecipe(ItemStack result, Object... components)
	{
		setupDir();

		// addShapelessRecipe(result, components);

		Map<String, Object> json = new HashMap<>();

		boolean isOreDict = false;
		List<Map<String, Object>> ingredients = new ArrayList<>();
		for (Object o : components) {
			if (o instanceof String)
				isOreDict = true;
			ingredients.add(serializeItem(o));
		}
		json.put("ingredients", ingredients);
		json.put("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists()) {
			suffix += "_alt";
			f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}


		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Map<String, Object> serializeItem(Object thing) {
		if (thing instanceof Item) {
			return serializeItem(new ItemStack((Item) thing));
		}
		if (thing instanceof Block) {
			return serializeItem(new ItemStack((Block) thing));
		}
		if (thing instanceof ItemStack) {
			ItemStack stack = (ItemStack) thing;
			Map<String, Object> ret = new HashMap<>();
			ret.put("item", stack.getItem().getRegistryName().toString());
			if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
				ret.put("data", stack.getItemDamage());
			}
			if (stack.getCount() > 1) {
				ret.put("count", stack.getCount());
			}

			if (stack.hasTagCompound()) {
				throw new IllegalArgumentException("nbt not implemented");
			}

			return ret;
		}
		if (thing instanceof String) {
			Map<String, Object> ret = new HashMap<>();
			USED_OD_NAMES.add((String) thing);
			ret.put("item", "#" + ((String) thing).toUpperCase(Locale.ROOT));
			return ret;
		}

		throw new IllegalArgumentException("Not a block, item, stack, or od name");
	}

	private static void generateConstants() {
		List<Map<String, Object>> json = new ArrayList<>();
		for (String s : USED_OD_NAMES) {
			Map<String, Object> entry = new HashMap<>();
			entry.put("name", s.toUpperCase(Locale.ROOT));
			entry.put("ingredient", ImmutableMap.of("type", "forge:ore_dict", "ore", s));
			json.add(entry);
		}

		try (FileWriter w = new FileWriter(new File(RECIPE_DIR, "_constants.json"))) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
