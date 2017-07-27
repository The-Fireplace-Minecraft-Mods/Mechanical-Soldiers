package the_fireplace.mechsoldiers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.mechsoldiers.blocks.*;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.entity.ai.GreenCPU;
import the_fireplace.mechsoldiers.entity.ai.TerminatorCPU;
import the_fireplace.mechsoldiers.items.*;
import the_fireplace.mechsoldiers.network.CommonProxy;
import the_fireplace.mechsoldiers.network.MSGuiHandler;
import the_fireplace.mechsoldiers.network.PacketDispatcher;
import the_fireplace.mechsoldiers.registry.MechCraftingRecipes;
import the_fireplace.mechsoldiers.registry.PartRegistry;
import the_fireplace.mechsoldiers.tileentity.*;
import the_fireplace.mechsoldiers.util.ComponentDamageGeneric;
import the_fireplace.mechsoldiers.util.ComponentDamagePotato;
import the_fireplace.mechsoldiers.util.ICPU;
import the_fireplace.mechsoldiers.util.LootHandler;
import the_fireplace.overlord.Overlord;

/**
 * @author The_Fireplace
 */
@Mod(modid = MechSoldiers.MODID, name = MechSoldiers.MODNAME, dependencies = "required-after:overlord@[2.3.*,)", version = "${version}", updateJSON = "https://bitbucket.org/The_Fireplace/minecraft-mod-updates/raw/master/mechsoldiers.json")
public class MechSoldiers {
	public static final String MODID = "mechsoldiers";
	public static final String MODNAME = "Mechanical Soldiers";

    /*TODO:
    Add splash text(1.12 only)
    Advancements(1.12 only)
     */

	@Mod.Instance(MODID)
	public static MechSoldiers instance;

	@SidedProxy(clientSide = "the_fireplace." + MODID + ".client.ClientProxy", serverSide = "the_fireplace." + MODID + ".network.CommonProxy")
	public static CommonProxy proxy;

	public static final CreativeTabs TAB_ROBOT_PARTS = new CreativeTabs("robot_parts") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(joints_steel);
		}
	};

	public static final Item skeleton_iron = new ItemSkeleton("iron", 55);
	public static final Item skeleton_steel = new ItemSkeleton("steel", 66);
	public static final Item skeleton_invar = new ItemSkeleton("invar", 33);
	public static final Item skeleton_bronze = new ItemSkeleton("bronze", 44);
	public static final Item skeleton_term = new ItemSkeleton("term", 70);
	public static final Item skeleton_gold = new ItemSkeleton("gold", 13);
	public static final Item skeleton_wood = new ItemSkeleton("wood", 20);
	public static final Item joints_iron = new ItemJoints("iron", 116);
	public static final Item joints_steel = new ItemJoints("steel", 140);
	public static final Item joints_invar = new ItemJoints("invar", 75);
	public static final Item joints_bronze = new ItemJoints("bronze", 96);
	public static final Item joints_gold = new ItemJoints("gold", 18);
	public static final Item cpu_copper_redstone = new ItemCPU("copper_redstone", 24);
	public static final Item cpu_gold_redstone = new ItemCPU("gold_redstone", 64);
	public static final Item cpu_electrum_redstone = new ItemCPU("electrum_redstone", 50);
	public static final Item cpu_terminator = new ItemCPU("terminator", 32);
	public static final Item cpu_green = new ItemCPU("green", 40);
	public static final Item blueprint = new Item().setUnlocalizedName("blueprint").setRegistryName("blueprint").setCreativeTab(CreativeTabs.MISC);

	public static final Block robot_constructor = new BlockRobotConstructor("robot_constructor");
	public static final Block robot_box = new BlockRobotBox("robot_box");
	public static final Block metal_part_constructor = new BlockMetalPartConstructor(false, "metal_part_constructor").setCreativeTab(Overlord.tabOverlord);
	public static final Block metal_part_constructor_active = new BlockMetalPartConstructor(true, "metal_part_constructor_active").setRegistryName("metal_part_constructor_active");
	public static final Block cpu_melter = new BlockCPUMelter("cpu_melter").setCreativeTab(Overlord.tabOverlord);

	public static final BlockHalfMiniTank mini_tank = new BlockHalfMiniTank();
	public static final BlockFullMiniTank full_mini_tank = new BlockFullMiniTank();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		PacketDispatcher.registerPackets();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MSGuiHandler());
		new PartRegistry();
		GameRegistry.register(skeleton_iron);
		GameRegistry.register(skeleton_steel);
		GameRegistry.register(skeleton_bronze);
		GameRegistry.register(skeleton_invar);
		GameRegistry.register(skeleton_term);
		GameRegistry.register(skeleton_gold);
		GameRegistry.register(skeleton_wood);
		GameRegistry.register(joints_iron);
		GameRegistry.register(joints_steel);
		GameRegistry.register(joints_bronze);
		GameRegistry.register(joints_invar);
		GameRegistry.register(joints_gold);
		GameRegistry.register(cpu_copper_redstone);
		GameRegistry.register(cpu_gold_redstone);
		GameRegistry.register(cpu_electrum_redstone);
		GameRegistry.register(cpu_terminator);
		GameRegistry.register(cpu_green);
		GameRegistry.register(blueprint);

		GameRegistry.register(robot_box);
		ItemBlock robotBoxItem = new ItemBlockSkeletonCrate(robot_box);
		robotBoxItem.setMaxStackSize(1);
		robotBoxItem.setRegistryName(robot_box.getRegistryName());
		GameRegistry.register(robotBoxItem);
		Overlord.instance.registerBlock(robot_constructor);
		Overlord.instance.registerBlock(metal_part_constructor);
		Overlord.instance.registerBlock(cpu_melter);
		GameRegistry.register(metal_part_constructor_active);

		GameRegistry.register(mini_tank);
		GameRegistry.register(full_mini_tank);
		GameRegistry.register(new ItemBlockMiniTank(mini_tank, mini_tank, full_mini_tank).setRegistryName("mini_tank"));

		GameRegistry.registerTileEntity(TileEntityRobotConstructor.class, "robot_constructor");
		GameRegistry.registerTileEntity(TileEntityRobotBox.class, "robot_box");
		GameRegistry.registerTileEntity(TileEntityPartConstructor.class, "metal_part_constructor");
		GameRegistry.registerTileEntity(TileEntityMiniTank.class, "mini_tank");
		GameRegistry.registerTileEntity(TileEntityCPUMelter.class, "cpu_melter");

		PartRegistry.registerSkeleton(skeleton_iron, ComponentDamageGeneric.getInstance(), "iron", new ResourceLocation(Overlord.MODID, "textures/entity/iron_skeleton.png"));
		PartRegistry.registerSkeleton(skeleton_term, ComponentDamageGeneric.getInstance(), "iron", new ResourceLocation(MODID, "textures/entity/terminator_skeleton.png"));
		PartRegistry.registerSkeleton(skeleton_invar, ComponentDamageGeneric.getInstance(), "iron", new ResourceLocation(MODID, "textures/entity/invar_skeleton.png"));
		PartRegistry.registerSkeleton(skeleton_steel, ComponentDamageGeneric.getInstance(), "iron", new ResourceLocation(MODID, "textures/entity/steel_skeleton.png"));
		PartRegistry.registerSkeleton(skeleton_bronze, ComponentDamageGeneric.getInstance(), "bronze", new ResourceLocation(MODID, "textures/entity/bronze_skeleton.png"));
		PartRegistry.registerSkeleton(skeleton_gold, ComponentDamageGeneric.getInstance(), "gold", new ResourceLocation(MODID, "textures/entity/gold_skeleton.png"));
		PartRegistry.registerSkeleton(skeleton_wood, ComponentDamageGeneric.getInstance(), "wood", new ResourceLocation(MODID, "textures/entity/wood_skeleton.png"));
		PartRegistry.registerJoints(joints_iron, ComponentDamageGeneric.getInstance(), "iron", new ResourceLocation(MODID, "textures/entity/iron_joints.png"));
		PartRegistry.registerJoints(joints_invar, ComponentDamageGeneric.getInstance(), "iron", new ResourceLocation(MODID, "textures/entity/invar_joints.png"));
		PartRegistry.registerJoints(joints_steel, ComponentDamageGeneric.getInstance(), "iron", new ResourceLocation(MODID, "textures/entity/steel_joints.png"));
		PartRegistry.registerJoints(joints_bronze, ComponentDamageGeneric.getInstance(), "bronze", new ResourceLocation(MODID, "textures/entity/bronze_joints.png"));
		PartRegistry.registerJoints(joints_gold, ComponentDamageGeneric.getInstance(), "gold", new ResourceLocation(MODID, "textures/entity/gold_joints.png"));
		PartRegistry.registerCPU(cpu_copper_redstone, (ICPU) cpu_copper_redstone, ComponentDamageGeneric.getInstance(), "copper_redstone");
		PartRegistry.registerCPU(cpu_gold_redstone, (ICPU) cpu_gold_redstone, ComponentDamageGeneric.getInstance(), "gold_redstone");
		PartRegistry.registerCPU(cpu_electrum_redstone, (ICPU) cpu_electrum_redstone, ComponentDamageGeneric.getInstance(), "gold_redstone");
		PartRegistry.registerCPU(cpu_terminator, new TerminatorCPU(), ComponentDamageGeneric.getInstance(), "iron");
		PartRegistry.registerCPU(cpu_green, new GreenCPU(), ComponentDamageGeneric.getInstance(), "copper");
		PartRegistry.registerPotatoCPU(Items.POTATO, 0, ComponentDamagePotato.getInstance(), "potato");
		PartRegistry.registerPotatoCPU(Items.BAKED_POTATO, 0, ComponentDamagePotato.getInstance(), "baked_potato");

		int eid = -1;
		EntityRegistry.registerModEntity(new ResourceLocation(MODID+":mechanical_skeleton"), EntityMechSkeleton.class, "mechanical_skeleton", ++eid, instance, 128, 2, false);

		proxy.registerClient();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MechCraftingRecipes.register();
		new LootHandler();
	}

	@SideOnly(Side.CLIENT)
	public void registerItemRenders() {
		rmm(skeleton_gold);
		rmm(skeleton_iron);
		rmm(skeleton_steel);
		rmm(skeleton_invar);
		rmm(skeleton_bronze);
		rmm(skeleton_term);
		rmm(skeleton_wood);
		rmm(joints_gold);
		rmm(joints_iron);
		rmm(joints_steel);
		rmm(joints_invar);
		rmm(joints_bronze);
		rmm(cpu_copper_redstone);
		rmm(cpu_gold_redstone);
		rmm(cpu_electrum_redstone);
		rmm(cpu_terminator);
		rmm(cpu_green);
		rmm(blueprint);

		rmm(robot_constructor);
		rmm(robot_box);
		rmm(metal_part_constructor);
		rmm(mini_tank);
		rmm(cpu_melter);
		IStateMapper mini_tank_mapper = new StateMap.Builder().ignore(BlockMiniTank.VARIANT_PROPERTY).build();
		IStateMapper full_mini_tank_mapper = new StateMap.Builder().ignore(BlockMiniTank.VARIANT_PROPERTY, BlockSlab.HALF).build();
		ModelLoader.setCustomStateMapper(mini_tank, mini_tank_mapper);
		ModelLoader.setCustomStateMapper(full_mini_tank, full_mini_tank_mapper);
	}

	@SideOnly(Side.CLIENT)
	private void rmm(Block b) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(MODID + ":" + b.getUnlocalizedName().substring(5), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	private void rmm(Item i) {
		ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(MODID + ":" + i.getUnlocalizedName().substring(5), "inventory"));
	}
}
