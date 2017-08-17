package the_fireplace.mechsoldiers.blocks;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import the_fireplace.mechsoldiers.blocks.BlockRobotBox;
import the_fireplace.mechsoldiers.blocks.BlockRobotConstructor;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DispenseBehaviorPlaceCrate extends BehaviorDefaultDispenseItem {
	@Override
	public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
		Block block = Block.getBlockFromItem(stack.getItem());
		BlockPos placePos = source.getBlockPos().offset(enumfacing);
		if(source.getWorld().mayPlace(block, placePos, true, enumfacing, null)){
			if(placeBlockAt(stack, block, source.getWorld(), placePos, enumfacing, 0.5f, 0.5f, 0.5f, block.getStateForPlacement(source.getWorld(), placePos, enumfacing, 0.5f, 0.5f, 0.5f, stack.getMetadata(), new EntityVillager(source.getWorld()), EnumHand.MAIN_HAND)))
				return ItemStack.EMPTY;
		}
		return stack;
	}

	public boolean placeBlockAt(ItemStack stack, Block block, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
	{
		if (!world.setBlockState(pos, newState, 11)) return false;

		IBlockState state = world.getBlockState(pos);
		if(block instanceof BlockRobotBox)
			((BlockRobotBox) block).skellyData = stack.getTagCompound();
		world.setTileEntity(pos, block.createTileEntity(world, state));
		if (state.getBlock() == block)
		{
			ItemBlock.setTileEntityNBT(world, null, pos, stack);
			block.onBlockPlacedBy(world, pos, state, null, stack);
		}

		return true;
	}
}
