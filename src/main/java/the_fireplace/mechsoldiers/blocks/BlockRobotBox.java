package the_fireplace.mechsoldiers.blocks;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
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
    public boolean isToolEffective(String type, IBlockState state)
    {
        return type.equals("axe");
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        skellyData = stack.getTagCompound();
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, stack);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if(skellyData == null)
            Overlord.logError("Error: Skeleton Data for a crate was null!");
        return new TileEntityRobotBox(skellyData, worldIn.rand.nextInt(24000)+12000);
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityRobotBox)
        {
            //TODO: Drop damaged robot parts
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }
}
