package the_fireplace.mechsoldiers.blocks;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotConstructor;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.tileentity.ISkeletonMaker;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * @author The_Fireplace
 */
@MethodsReturnNonnullByDefault
public class BlockRobotConstructor extends BlockContainer {
	public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");
	private static boolean keepInventory;
	public BlockRobotConstructor(String name) {
		super(Material.IRON);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Overlord.tabOverlord);
		setHarvestLevel("pickaxe", 2);
		setHardness(5.0F);
		setResistance(10.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TRIGGERED, Boolean.valueOf(false)));
	}

	@Override
	public int tickRate(World worldIn)
	{
		return 4;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.04F, 0F, 0.04F, 0.96F, 0.65F, 0.96F);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
		return new TileEntityRobotConstructor();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote)
			return true;
		else if (!playerIn.isSneaking()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileEntityRobotConstructor)
				FMLNetworkHandler.openGui(playerIn, MechSoldiers.MODID, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		} else
			return false;
	}

	@Override
	public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		if (!keepInventory) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileEntityRobotConstructor) {
				InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		boolean blockIsPowered = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
		boolean blockStateIsPowered = ((Boolean)state.getValue(TRIGGERED)).booleanValue();

		if (blockIsPowered && !blockStateIsPowered)
		{
			worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
			setState(true, worldIn, pos);
		}
		else if (!blockIsPowered && blockStateIsPowered)
			setState(false, worldIn, pos);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		TileEntity te = worldIn.getTileEntity(pos);
		if (!worldIn.isRemote && te instanceof ISkeletonMaker)
			((ISkeletonMaker)te).spawnSkeleton(null);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(TRIGGERED, Boolean.valueOf((meta & 8) > 0));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int i = 0;

		if (((Boolean)state.getValue(TRIGGERED)).booleanValue())
			i |= 8;

		return i;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, TRIGGERED);
	}

	public static void setState(boolean triggered, World worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		keepInventory = true;
		worldIn.setBlockState(pos, MechSoldiers.robot_constructor.getDefaultState().withProperty(TRIGGERED, triggered), 3);
		worldIn.setBlockState(pos, MechSoldiers.robot_constructor.getDefaultState().withProperty(TRIGGERED, triggered), 3);
		keepInventory = false;

		if (tileentity != null) {
			tileentity.validate();
			worldIn.setTileEntity(pos, tileentity);
		}
	}
}

