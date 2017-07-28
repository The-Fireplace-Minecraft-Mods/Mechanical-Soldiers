package the_fireplace.mechsoldiers.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.tileentity.TileEntityCPUMelter;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartConstructor;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockCPUMelter extends BlockContainer {

	public BlockCPUMelter(String name) {
		super(Material.ROCK);
		setUnlocalizedName(name);
		//setRegistryName(name);//Handled when registering
		useNeighborBrightness = true;
		setHardness(1.5F);
		setResistance(1.0F);
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileEntityCPUMelter) {
				FMLNetworkHandler.openGui(playerIn, MechSoldiers.MODID, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}

			return true;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCPUMelter();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileEntityCPUMelter) {
				InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityCPUMelter) tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		return Container.calcRedstone(worldIn.getTileEntity(pos));
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
}