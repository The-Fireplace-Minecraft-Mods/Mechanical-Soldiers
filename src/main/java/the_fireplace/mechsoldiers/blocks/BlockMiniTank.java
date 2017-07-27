package the_fireplace.mechsoldiers.blocks;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.tileentity.TileEntityMiniTank;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class BlockMiniTank extends BlockSlab implements ITileEntityProvider {

	public static final PropertyBool VARIANT_PROPERTY = PropertyBool.create("variant");

	public BlockMiniTank() {
		super(Material.GLASS);
		if (!this.isDouble())
			setCreativeTab(Overlord.tabOverlord);
		setHardness(2.0F);
		setResistance(10.0F);
		this.useNeighborBrightness = true;
		setUnlocalizedName("mini_tank");
		setRegistryName(isDouble() ? "full_mini_tank" : "mini_tank");

		IBlockState blockState = this.blockState.getBaseState();
		blockState = blockState.withProperty(VARIANT_PROPERTY, false);
		if (!this.isDouble()) {
			blockState = blockState.withProperty(HALF, EnumBlockHalf.BOTTOM);
		}
		setDefaultState(blockState);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityMiniTank();
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack)
	{
		return false;
	}

	@Override
	public String getUnlocalizedName(int meta) {
		return this.getUnlocalizedName();
	}

	@Override
	public IProperty<?> getVariantProperty() {
		return VARIANT_PROPERTY;
	}

	@Override
	public final IBlockState getStateFromMeta(final int meta) {
		IBlockState state = this.getDefaultState();
		state = state.withProperty(VARIANT_PROPERTY, false);
		if (!this.isDouble()) {
			EnumBlockHalf value = EnumBlockHalf.BOTTOM;
			if ((meta & 8) != 0) {
				value = EnumBlockHalf.TOP;
			}

			state = state.withProperty(HALF, value);
		}
		return state;
	}

	@Override
	public final int getMetaFromState(final IBlockState state) {
		if (this.isDouble()) {
			return 0;
		}
		if (state.getValue(HALF) == EnumBlockHalf.TOP) {
			return 8;
		} else {
			return 0;
		}
	}

	@Override
	public final int damageDropped(final IBlockState state) {
		return 0;
	}

	@Override
	public final Item getItemDropped(IBlockState state, Random rand, int unused) {
		return Item.getItemFromBlock(MechSoldiers.mini_tank);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public final ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(MechSoldiers.mini_tank);
	}

	@Override
	protected final BlockStateContainer createBlockState() {
		if (this.isDouble()) {
			return new BlockStateContainer(this, VARIANT_PROPERTY);
		} else {
			return new BlockStateContainer(this, VARIANT_PROPERTY, HALF);
		}
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityMiniTank) {
			return ((TileEntityMiniTank) tile).getFluidAmount() * 15 / ((TileEntityMiniTank) tile).getCapacity();
		}
		return 0;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityMiniTank && !player.isSneaking()) {
			boolean interact = FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
			if(interact)
				tile.markDirty();
			return interact;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		return false;
	}
}
