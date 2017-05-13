package the_fireplace.mechsoldiers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
import the_fireplace.mechsoldiers.blocks.BlockRobotBox;
import the_fireplace.mechsoldiers.blocks.BlockRobotConstructor;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.items.ItemBrain;
import the_fireplace.mechsoldiers.items.ItemJoints;
import the_fireplace.mechsoldiers.items.ItemSkeleton;
import the_fireplace.mechsoldiers.network.CommonProxy;
import the_fireplace.mechsoldiers.network.MSGuiHandler;
import the_fireplace.mechsoldiers.network.PacketDispatcher;
import the_fireplace.mechsoldiers.registry.MechCraftingRecipes;
import the_fireplace.mechsoldiers.registry.PartRegistry;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotBox;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotConstructor;
import the_fireplace.mechsoldiers.util.ComponentDamageGeneric;
import the_fireplace.mechsoldiers.util.ComponentDamagePotato;
import the_fireplace.mechsoldiers.util.IBrain;
import the_fireplace.overlord.Overlord;

/**
 * @author The_Fireplace
 */
@Mod(modid=MechSoldiers.MODID, name=MechSoldiers.MODNAME, dependencies = "required-after:overlord@[2.3.*,)", version = "${version}")
public class MechSoldiers {
    public static final String MODID = "mechsoldiers";
    public static final String MODNAME = "Mechanical Soldiers";

    /*TODO before initial release:
    Add a Robot Skeleton GUI
    Improve Gold Skeleton textures
    Add crafting recipes for everything(Including new machines to assemble parts)
    Un-comment the skeleton crate's zeros
    Add brain textures
     */
    /*TODO anytime:
    Make the Robot Builder consume a log for each crate
    Add JEI integration for the machines that make parts
    Add a tooltip to the crate saying what is in it
    Add robots to the Overlord guide book
    Add integration with other mods for parts made of copper, steel, bronze, etc.
    Add integration with weed mods for 420 joints.
    Add splash text(perhaps in Overlord's splash text addition)
    Advancements(Once 1.12 comes along)
     */

    @Mod.Instance(MODID)
    public static MechSoldiers instance;

    @SidedProxy(clientSide = "the_fireplace."+MODID+".client.ClientProxy", serverSide = "the_fireplace."+MODID+".network.CommonProxy")
    public static CommonProxy proxy;

    public static final Item skeleton_iron = new ItemSkeleton("iron", 125);
    public static final Item skeleton_gold = new ItemSkeleton("gold", 16);
    public static final Item skeleton_wood = new ItemSkeleton("wood", 30);
    public static final Item joints_iron = new ItemJoints("iron", 125);
    public static final Item joints_gold = new ItemJoints("gold", 16);
    public static final Item brain_copper_redstone = new ItemBrain("copper_redstone", 24);
    public static final Item brain_gold_redstone = new ItemBrain("gold_redstone", 64);

    public static final Block robot_constructor = new BlockRobotConstructor("robot_constructor");
    public static final Block robot_box = new BlockRobotBox("robot_box");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        PacketDispatcher.registerPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new MSGuiHandler());
        new PartRegistry();
        GameRegistry.register(skeleton_iron);
        GameRegistry.register(skeleton_gold);
        GameRegistry.register(skeleton_wood);
        GameRegistry.register(joints_iron);
        GameRegistry.register(joints_gold);
        GameRegistry.register(brain_copper_redstone);
        GameRegistry.register(brain_gold_redstone);

        GameRegistry.register(robot_box);
        ItemBlock robotBoxItem = new ItemBlock(robot_box);
        robotBoxItem.setMaxStackSize(1);
        robotBoxItem.setRegistryName(robot_box.getRegistryName());
        GameRegistry.register(robotBoxItem);
        GameRegistry.register(robot_constructor);
        GameRegistry.register(new ItemBlock(robot_constructor).setRegistryName(robot_constructor.getRegistryName()));

        GameRegistry.registerTileEntity(TileEntityRobotConstructor.class, "robot_constructor");
        GameRegistry.registerTileEntity(TileEntityRobotBox.class, "robot_box");

        PartRegistry.registerSkeleton(skeleton_iron, ComponentDamageGeneric.getInstance(), "iron", new ResourceLocation(Overlord.MODID, "textures/entity/iron_skeleton.png"));
        PartRegistry.registerSkeleton(skeleton_gold, ComponentDamageGeneric.getInstance(), "gold", new ResourceLocation(MODID, "textures/entity/gold_skeleton.png"));
        PartRegistry.registerSkeleton(skeleton_wood, ComponentDamageGeneric.getInstance(), "wood", new ResourceLocation(MODID, "textures/entity/wood_skeleton.png"));
        PartRegistry.registerJoints(joints_iron, ComponentDamageGeneric.getInstance(), "iron", new ResourceLocation(MODID, "textures/entity/iron_joints.png"));
        PartRegistry.registerJoints(joints_gold, ComponentDamageGeneric.getInstance(), "gold", new ResourceLocation(MODID, "textures/entity/gold_joints.png"));
        PartRegistry.registerBrain(brain_copper_redstone, (IBrain)brain_copper_redstone, ComponentDamageGeneric.getInstance(), "copper_redstone");
        PartRegistry.registerBrain(brain_gold_redstone, (IBrain)brain_gold_redstone, ComponentDamageGeneric.getInstance(), "gold_redstone");
        PartRegistry.registerPotatoBrain(Items.POTATO, 0, ComponentDamagePotato.getInstance(), "potato");
        PartRegistry.registerPotatoBrain(Items.BAKED_POTATO, 0, ComponentDamagePotato.getInstance(), "baked_potato");

        int eid=-1;
        EntityRegistry.registerModEntity(/*new ResourceLocation(MODID+":mechanical_skeleton"), */EntityMechSkeleton.class, "mechanical_skeleton", ++eid, instance, 128, 2, false);

        proxy.registerClient();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        MechCraftingRecipes.register();
    }

    @SideOnly(Side.CLIENT)
    public void registerItemRenders(){
        rmm(skeleton_gold);
        rmm(skeleton_iron);
        rmm(skeleton_wood);
        rmm(joints_gold);
        rmm(joints_iron);
        rmm(brain_copper_redstone);
        rmm(brain_gold_redstone);

        rmm(robot_constructor);
        rmm(robot_box);
    }

    @SideOnly(Side.CLIENT)
    private void rmm(Block b) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(MODID+":" + b.getUnlocalizedName().substring(5), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    private void rmm(Item i) {
        ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(MODID+":" + i.getUnlocalizedName().substring(5), "inventory"));
    }
}
