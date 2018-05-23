package the_fireplace.mechsoldiers.compat.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotBox;
import the_fireplace.overlord.Overlord;

import java.util.List;

public class WailaBlockProvider implements IWailaDataProvider {
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return new ItemStack(accessor.getBlockState().getBlock());
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		TileEntity robotBox = accessor.getTileEntity();
		if (robotBox instanceof TileEntityRobotBox)
			if (((TileEntityRobotBox) robotBox).validate(false)) {
				currenttip.add(TextFormatting.YELLOW + Overlord.proxy.translateToLocal("mechsoldiers.top.robotbox", (int) (100f * ((TileEntityRobotBox) robotBox).getCompletion())));
			} else {
				currenttip.add(TextFormatting.DARK_RED + Overlord.proxy.translateToLocal("mechsoldiers.top.robotbox.invalid"));
			}

		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		return tag;
	}
}
