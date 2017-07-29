package the_fireplace.mechsoldiers.util;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class CommonEvents {
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
		IBlockState state = event.getWorld().getBlockState(event.getPos());
		if(state.getBlock() == Blocks.CAULDRON && state.getValue(BlockCauldron.LEVEL) > 0 && StainedItemUtil.getColor(event.getItemStack()) != null){
			event.getItemStack().getTagCompound().removeTag("StainColor");
		}
	}
}
