package the_fireplace.mechsoldiers.blocks;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotBox;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

/**
 * @author The_Fireplace
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockRobotBox extends Block implements ITileEntityProvider {
	NBTTagCompound skellyData = null;

	public BlockRobotBox(String name) {
		super(Material.WOOD);
		setUnlocalizedName(name);
		setRegistryName(name);
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		return type.equals("axe");
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		skellyData = stack.getTagCompound();
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, stack);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if (skellyData == null && !worldIn.isRemote)
			Overlord.logError("Error: Skeleton Data for a crate was null!");
		return new TileEntityRobotBox(skellyData, worldIn.rand.nextInt(24/*000*/) + 12/*000*/);//TODO: Remove noted out zeroes before release
	}

	@Override
	public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileEntityRobotBox) {
			EntityItem brain = new EntityItem(worldIn);
			ItemStack brainStack = ((TileEntityRobotBox) tileentity).getBrain();
			if (brainStack.isItemStackDamageable())
				brainStack.setItemDamage(Math.round(brainStack.getMaxDamage() * ((TileEntityRobotBox) tileentity).getCompletion()));
			brain.setEntityItemStack(brainStack);
			brain.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
			worldIn.spawnEntity(brain);
			brain.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);

			EntityItem joints = new EntityItem(worldIn);
			ItemStack jointStack = ((TileEntityRobotBox) tileentity).getJoints();
			if (jointStack.isItemStackDamageable())
				jointStack.setItemDamage(Math.round(jointStack.getMaxDamage() * ((TileEntityRobotBox) tileentity).getCompletion()));
			joints.setEntityItemStack(jointStack);
			joints.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
			worldIn.spawnEntity(joints);
			joints.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);

			EntityItem skeleton = new EntityItem(worldIn);
			ItemStack skeletonStack = ((TileEntityRobotBox) tileentity).getSkeleton();
			if (skeletonStack.isItemStackDamageable())
				skeletonStack.setItemDamage(Math.round(skeletonStack.getMaxDamage() * ((TileEntityRobotBox) tileentity).getCompletion()));
			skeleton.setEntityItemStack(skeletonStack);
			skeleton.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
			worldIn.spawnEntity(skeleton);
			skeleton.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}
}
