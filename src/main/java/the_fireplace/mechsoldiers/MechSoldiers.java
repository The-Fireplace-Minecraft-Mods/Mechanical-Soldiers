package the_fireplace.mechsoldiers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import the_fireplace.mechsoldiers.blocks.*;
import the_fireplace.mechsoldiers.compat.IModCompat;
import the_fireplace.mechsoldiers.compat.ie.IECompat;
import the_fireplace.mechsoldiers.compat.top.TOPCompatibility;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.entity.ai.AlienCPU;
import the_fireplace.mechsoldiers.entity.ai.GreenCPU;
import the_fireplace.mechsoldiers.entity.ai.TerminatorCPU;
import the_fireplace.mechsoldiers.entity.ai.WoodCPU;
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
@Mod.EventBusSubscriber
@Mod(modid = MechSoldiers.MODID, name = MechSoldiers.MODNAME, dependencies = "required-after:overlord@[2.3.*,);after:theoneprobe", version = "${version}", updateJSON = "https://bitbucket.org/The_Fireplace/minecraft-mod-updates/raw/master/mechsoldiers.json", acceptedMinecraftVersions = "[1.12,1.13)")
public class MechSoldiers {
	public static final String MODID = "mechsoldiers";
	public static final String MODNAME = "Mechanical Soldiers";

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
	public static final Item cpu_alien = new ItemCPU("alien", 60);
	public static final Item cpu_wood = new ItemCPU("wood", 20);
	public static final Item blueprint = new Item().setUnlocalizedName("blueprint").setRegistryName("blueprint").setCreativeTab(CreativeTabs.MISC);

	public static final Block robot_constructor = new BlockRobotConstructor("robot_constructor");
	public static final Block robot_box = new BlockRobotBox("robot_box");
	public static final Block metal_part_constructor = new BlockMetalPartConstructor(false, "metal_part_constructor").setCreativeTab(Overlord.tabOverlord);
	public static final Block metal_part_constructor_active = new BlockMetalPartConstructor(true, "metal_part_constructor_active");
	public static final Block cpu_melter = new BlockCPUMelter("cpu_melter").setCreativeTab(Overlord.tabOverlord);
	public static final Block part_stainer = new BlockPartStainer("part_stainer");

	public static final BlockHalfMiniTank mini_tank = new BlockHalfMiniTank();
	public static final BlockFullMiniTank full_mini_tank = new BlockFullMiniTank();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		PacketDispatcher.registerPackets();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MSGuiHandler());
		new PartRegistry();

		GameRegistry.registerTileEntity(TileEntityRobotConstructor.class, "robot_constructor");
		GameRegistry.registerTileEntity(TileEntityRobotBox.class, "robot_box");
		GameRegistry.registerTileEntity(TileEntityPartConstructor.class, "metal_part_constructor");
		GameRegistry.registerTileEntity(TileEntityMiniTank.class, "mini_tank");
		GameRegistry.registerTileEntity(TileEntityCPUMelter.class, "cpu_melter");
		GameRegistry.registerTileEntity(TileEntityPartStainer.class, "part_stainer");

		int eid = -1;
		EntityRegistry.registerModEntity(new ResourceLocation(MODID + ":mechanical_skeleton"), EntityMechSkeleton.class, "mechanical_skeleton", ++eid, instance, 128, 2, false);

		proxy.registerClient();
		if (Loader.isModLoaded("theoneprobe"))
			TOPCompatibility.register();
		IModCompat compat;
		if(Loader.isModLoaded("immersiveengineering")){
			compat = new IECompat();
			compat.preInit(event.getSide().isClient());
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		new LootHandler();

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
		PartRegistry.registerCPU(cpu_wood, new WoodCPU(), ComponentDamageGeneric.getInstance(), "wood_cpu");
		PartRegistry.registerCPU(cpu_alien, new AlienCPU(), ComponentDamageGeneric.getInstance(), "gold");
		PartRegistry.registerPotatoCPU(Items.POTATO, 0, ComponentDamagePotato.getInstance(), "potato");
		PartRegistry.registerPotatoCPU(Items.BAKED_POTATO, 0, ComponentDamagePotato.getInstance(), "baked_potato");

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Item.getItemFromBlock(robot_box), new DispenseBehaviorPlaceCrate());
	}

	private static IForgeRegistry<Block> blockRegistry = null;

	public static void registerBlock(Block block) {
		if(blockRegistry == null){
			Overlord.logError("Block registry was null, could not register: "+block.getUnlocalizedName());
			return;
		}
		blockRegistry.register(block);
	}

	private static IForgeRegistry<Item> itemRegistry = null;

	public static void registerItem(Item item) {
		if(itemRegistry == null){
			Overlord.logError("Item registry was null, could not register: "+item.getUnlocalizedName());
			return;
		}
		itemRegistry.register(item);
	}

	public static void registerItemForBlock(Block block) {
		if(itemRegistry == null){
			Overlord.logError("Item registry was null, could not register: "+block.getUnlocalizedName());
			return;
		}
		itemRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@SubscribeEvent
	public static void itemRegistry(RegistryEvent.Register<Item> event) {
		itemRegistry = event.getRegistry();
		registerItem(skeleton_iron);
		registerItem(skeleton_steel);
		registerItem(skeleton_bronze);
		registerItem(skeleton_invar);
		registerItem(skeleton_term);
		registerItem(skeleton_gold);
		registerItem(skeleton_wood);
		registerItem(joints_iron);
		registerItem(joints_steel);
		registerItem(joints_bronze);
		registerItem(joints_invar);
		registerItem(joints_gold);
		registerItem(cpu_copper_redstone);
		registerItem(cpu_gold_redstone);
		registerItem(cpu_electrum_redstone);
		registerItem(cpu_terminator);
		registerItem(cpu_green);
		registerItem(cpu_wood);
		registerItem(cpu_alien);
		registerItem(blueprint);

		registerItem(new ItemBlock(robot_box).setMaxStackSize(1).setRegistryName(robot_box.getRegistryName()));
		registerItemForBlock(robot_constructor);
		registerItemForBlock(metal_part_constructor);
		registerItemForBlock(cpu_melter);
		registerItemForBlock(part_stainer);
		registerItem(new ItemBlockMiniTank(mini_tank, mini_tank, full_mini_tank).setRegistryName("mini_tank"));

		MechCraftingRecipes.register();
	}

	@SubscribeEvent
	public static void blockRegistry(RegistryEvent.Register<Block> event) {
		blockRegistry = event.getRegistry();
		registerBlock(robot_box);
		registerBlock(robot_constructor);
		registerBlock(metal_part_constructor);
		registerBlock(cpu_melter);
		registerBlock(metal_part_constructor_active);
		registerBlock(part_stainer);

		registerBlock(mini_tank);
		registerBlock(full_mini_tank);
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemRenders() {
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
		rmm(cpu_wood);
		rmm(cpu_alien);
		rmm(blueprint);

		rmm(robot_constructor);
		rmm(robot_box);
		rmm(metal_part_constructor);
		rmm(mini_tank);
		rmm(cpu_melter);
		rmm(part_stainer);
		IStateMapper robot_constructor_mapper = new StateMap.Builder().ignore(BlockRobotConstructor.TRIGGERED).build();
		ModelLoader.setCustomStateMapper(robot_constructor, robot_constructor_mapper);
		IStateMapper mini_tank_mapper = new StateMap.Builder().ignore(BlockMiniTank.VARIANT_PROPERTY).build();
		ModelLoader.setCustomStateMapper(mini_tank, mini_tank_mapper);
		IStateMapper full_mini_tank_mapper = new StateMap.Builder().ignore(BlockMiniTank.VARIANT_PROPERTY, BlockSlab.HALF).build();
		ModelLoader.setCustomStateMapper(full_mini_tank, full_mini_tank_mapper);
	}

	@SideOnly(Side.CLIENT)
	private static void rmm(Block b) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(MODID + ":" + b.getUnlocalizedName().substring(5), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void rmm(Item i) {
		ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(MODID + ":" + i.getUnlocalizedName().substring(5), "inventory"));
	}
}
