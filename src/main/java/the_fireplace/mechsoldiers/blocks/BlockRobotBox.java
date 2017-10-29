package the_fireplace.mechsoldiers.blocks;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotBox;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static the_fireplace.overlord.Overlord.proxy;

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
		setHardness(3.0F);
		setResistance(5.0F);
	}

	@Override
	public boolean isToolEffective(String type, IBlockState state) {
		return type.equals("axe");
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		skellyData = placer.getHeldItem(hand).getTagCompound();
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityRobotBox(skellyData, worldIn.rand.nextInt(24000) + 12000);
	}

	@Override
	public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileEntityRobotBox) {
			EntityItem cpu = new EntityItem(worldIn);
			ItemStack cpuStack = ((TileEntityRobotBox) tileentity).getCPU();
			if (cpuStack.isItemStackDamageable())
				cpuStack.setItemDamage(Math.round(cpuStack.getMaxDamage() * ((TileEntityRobotBox) tileentity).getCompletion()));
			cpu.setItem(cpuStack);
			cpu.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
			worldIn.spawnEntity(cpu);
			cpu.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);

			EntityItem joints = new EntityItem(worldIn);
			ItemStack jointStack = ((TileEntityRobotBox) tileentity).getJoints();
			if (jointStack.isItemStackDamageable())
				jointStack.setItemDamage(Math.round(jointStack.getMaxDamage() * ((TileEntityRobotBox) tileentity).getCompletion()));
			joints.setItem(jointStack);
			joints.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
			worldIn.spawnEntity(joints);
			joints.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);

			EntityItem skeleton = new EntityItem(worldIn);
			ItemStack skeletonStack = ((TileEntityRobotBox) tileentity).getSkeleton();
			if (skeletonStack.isItemStackDamageable())
				skeletonStack.setItemDamage(Math.round(skeletonStack.getMaxDamage() * ((TileEntityRobotBox) tileentity).getCompletion()));
			skeleton.setItem(skeletonStack);
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

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack pick = new ItemStack(MechSoldiers.robot_box);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityRobotBox) {
			NBTTagCompound skellyData = new NBTTagCompound();
			skellyData.setTag("RobotCPU", ((TileEntityRobotBox) te).getCPU().writeToNBT(new NBTTagCompound()));
			skellyData.setTag("RobotSkeleton", ((TileEntityRobotBox) te).getSkeleton().writeToNBT(new NBTTagCompound()));
			skellyData.setTag("RobotJoints", ((TileEntityRobotBox) te).getJoints().writeToNBT(new NBTTagCompound()));
			skellyData.setString("OwnerUUID", player.isSneaking() ? player.getUniqueID().toString() : ((TileEntityRobotBox) te).getOwnerId());
			pick.setTagCompound(skellyData);
		}
		return pick;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			if (nbt.hasKey("OwnerUUID") && !nbt.getString("OwnerUUID").isEmpty() && world.getPlayerEntityByUUID(UUID.fromString(nbt.getString("OwnerUUID"))) != null)
				tooltip.add(proxy.translateToLocal("tooltip.owner") + ' ' + world.getPlayerEntityByUUID(UUID.fromString(nbt.getString("OwnerUUID"))).getDisplayNameString());
			if (nbt.hasKey("RobotCPU"))
				tooltip.add(proxy.translateToLocal("color.turq") + proxy.translateToLocal("tooltip.robot_cpu") + ' ' + new ItemStack(nbt.getCompoundTag("RobotCPU")).getDisplayName());
			if (nbt.hasKey("RobotSkeleton"))
				tooltip.add(proxy.translateToLocal("color.turq") + proxy.translateToLocal("tooltip.robot_skeleton") + ' ' + new ItemStack(nbt.getCompoundTag("RobotSkeleton")).getDisplayName());
			if (nbt.hasKey("RobotJoints"))
				tooltip.add(proxy.translateToLocal("color.turq") + proxy.translateToLocal("tooltip.robot_joints") + ' ' + new ItemStack(nbt.getCompoundTag("RobotJoints")).getDisplayName());
		}
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof TileEntityRobotBox)
			return (int)((TileEntityRobotBox) te).getCompletion()*15;
		return 0;
	}
}
