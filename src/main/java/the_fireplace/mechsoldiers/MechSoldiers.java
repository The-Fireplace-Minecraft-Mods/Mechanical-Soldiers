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
import the_fireplace.mechsoldiers.entity.ai.TerminatorCPU;
import the_fireplace.mechsoldiers.items.ItemBlockMiniTank;
import the_fireplace.mechsoldiers.items.ItemCPU;
import the_fireplace.mechsoldiers.items.ItemJoints;
import the_fireplace.mechsoldiers.items.ItemSkeleton;
import the_fireplace.mechsoldiers.network.CommonProxy;
import the_fireplace.mechsoldiers.network.MSGuiHandler;
import the_fireplace.mechsoldiers.network.PacketDispatcher;
import the_fireplace.mechsoldiers.registry.MechCraftingRecipes;
import the_fireplace.mechsoldiers.registry.PartRegistry;
import the_fireplace.mechsoldiers.tileentity.*;
import the_fireplace.mechsoldiers.util.ComponentDamageGeneric;
import the_fireplace.mechsoldiers.util.ComponentDamagePotato;
import the_fireplace.mechsoldiers.util.ICPU;
import the_fireplace.overlord.Overlord;

/**
 * @author The_Fireplace
 */
@Mod(modid = MechSoldiers.MODID, name = MechSoldiers.MODNAME, dependencies = "required-after:overlord@[2.3.*,)", version = "${version}")
public class MechSoldiers {
	public static final String MODID = "mechsoldiers";
	public static final String MODNAME = "Mechanical Soldiers";

    /*TODO before initial release:
    Add a tooltip to the crate saying what is in it
    Add robots to the Overlord guide book
    Add JEI integration for the machines that make parts
    Add AE2 integration
     */
    /*TODO anytime:
    Add integration with other mods for parts made of copper, steel, bronze, etc.
    Add integration with weed mods for 420 joints. 🚬
    Add splash text(1.12 only)
    Advancements(1.12 only)
    Add Buildcraft's chipsets to cpu recipes(1.11.2+)
    Prevent mods that show entity health from showing robot health(Waila?) Nice. Damage Indicators.
     */

	@Mod.Instance(MODID)
	public static MechSoldiers instance;

	@SidedProxy(clientSide = "the_fireplace." + MODID + ".client.ClientProxy", serverSide = "the_fireplace." + MODID + ".network.CommonProxy")
	public static CommonProxy proxy;

	public static final CreativeTabs TAB_ROBOT_PARTS = new CreativeTabs("robot_parts") {
		@Override
		public Item getTabIconItem() {
			return joints_iron;
		}
	};

	public static final Item skeleton_iron = new ItemSkeleton("iron", 125);
	public static final Item skeleton_term = new ItemSkeleton("term", 180);
	public static final Item skeleton_gold = new ItemSkeleton("gold", 16);
	public static final Item skeleton_wood = new ItemSkeleton("wood", 30);
	public static final Item joints_iron = new ItemJoints("iron", 125);
	public static final Item joints_gold = new ItemJoints("gold", 16);
	public static final Item cpu_copper_redstone = new ItemCPU("copper_redstone", 24);
	public static final Item cpu_gold_redstone = new ItemCPU("gold_redstone", 64);
	public static final Item cpu_terminator = new ItemCPU("terminator", 32);
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
		GameRegistry.register(skeleton_term);
		GameRegistry.register(skeleton_gold);
		GameRegistry.register(skeleton_wood);
		GameRegistry.register(joints_iron);
		GameRegistry.register(joints_gold);
		GameRegistry.register(cpu_copper_redstone);
		GameRegistry.register(cpu_gold_redstone);
		GameRegistry.register(cpu_terminator);
		GameRegistry.register(blueprint);

		GameRegistry.register(robot_box);
		ItemBlock robotBoxItem = new ItemBlock(robot_box);
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
		PartRegistry.registerSkeleton(skeleton_gold, ComponentDamageGeneric.getInstance(), "gold", new ResourceLocation(MODID, "textures/entity/gold_skeleton.png"));
		PartRegistry.registerSkeleton(skeleton_wood, ComponentDamageGeneric.getInstance(), "wood", new ResourceLocation(MODID, "textures/entity/wood_skeleton.png"));
		PartRegistry.registerJoints(joints_iron, ComponentDamageGeneric.getInstance(), "iron", new ResourceLocation(MODID, "textures/entity/iron_joints.png"));
		PartRegistry.registerJoints(joints_gold, ComponentDamageGeneric.getInstance(), "gold", new ResourceLocation(MODID, "textures/entity/gold_joints.png"));
		PartRegistry.registerCPU(cpu_copper_redstone, (ICPU) cpu_copper_redstone, ComponentDamageGeneric.getInstance(), "copper_redstone");
		PartRegistry.registerCPU(cpu_gold_redstone, (ICPU) cpu_gold_redstone, ComponentDamageGeneric.getInstance(), "gold_redstone");
		PartRegistry.registerCPU(cpu_terminator, new TerminatorCPU(), ComponentDamageGeneric.getInstance(), "iron");
		PartRegistry.registerPotatoCPU(Items.POTATO, 0, ComponentDamagePotato.getInstance(), "potato");
		PartRegistry.registerPotatoCPU(Items.BAKED_POTATO, 0, ComponentDamagePotato.getInstance(), "baked_potato");

		int eid = -1;
		EntityRegistry.registerModEntity(/*new ResourceLocation(MODID+":mechanical_skeleton"), */EntityMechSkeleton.class, "mechanical_skeleton", ++eid, instance, 128, 2, false);

		proxy.registerClient();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		MechCraftingRecipes.register();
	}

	@SideOnly(Side.CLIENT)
	public void registerItemRenders() {
		rmm(skeleton_gold);
		rmm(skeleton_iron);
		rmm(skeleton_term);
		rmm(skeleton_wood);
		rmm(joints_gold);
		rmm(joints_iron);
		rmm(cpu_copper_redstone);
		rmm(cpu_gold_redstone);
		rmm(cpu_terminator);
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
