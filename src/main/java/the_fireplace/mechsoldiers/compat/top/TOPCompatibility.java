package the_fireplace.mechsoldiers.compat.top;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.blocks.BlockRobotBox;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotBox;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TOPCompatibility {
	private static boolean registered;

	public static void register() {
		if (registered)
			return;
		registered = true;
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "the_fireplace.mechsoldiers.compat.top.TOPCompatibility$GetTheOneProbe");
	}

	public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {
		public static ITheOneProbe probe;
		@Nullable
		@Override
		public Void apply(ITheOneProbe theOneProbe) {
			probe = theOneProbe;
			probe.registerProvider(new IProbeInfoProvider() {
				@Override
				public String getID() {
					return MechSoldiers.MODID;
				}

				@Override
				public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
					TileEntity robotBox = world.getTileEntity(data.getPos());
					if (blockState.getBlock() instanceof BlockRobotBox && robotBox instanceof TileEntityRobotBox) {
						if (((TileEntityRobotBox) robotBox).validate(!world.isRemote))
							probeInfo.text(TextFormatting.YELLOW + Overlord.proxy.translateToLocal("mechsoldiers.top.robotbox", (int) (100f * ((TileEntityRobotBox) robotBox).getCompletion())));
					}
				}
			});
			//probe.registerEntityDisplayOverride((mode, probeInfo, player, world, entity, data) -> entity instanceof EntityMechSkeleton);
			probe.registerEntityProvider(new IProbeInfoEntityProvider() {
				@Override
				public String getID() {
					return MechSoldiers.MODID;
				}

				@Override
				public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
					if(entity instanceof EntityMechSkeleton){
						probeInfo.text(ChatFormatting.YELLOW+ Overlord.proxy.translateToLocal("mechsoldiers.top.cpu", ((EntityMechSkeleton) entity).getCPU().getMaxDamage()-((EntityMechSkeleton) entity).getCPU().getItemDamage()+1, ((EntityMechSkeleton) entity).getCPU().getMaxDamage()+1));
						probeInfo.text(ChatFormatting.YELLOW+ Overlord.proxy.translateToLocal("mechsoldiers.top.skeleton", ((EntityMechSkeleton) entity).getSkeleton().getMaxDamage()-((EntityMechSkeleton) entity).getSkeleton().getItemDamage()+1, ((EntityMechSkeleton) entity).getSkeleton().getMaxDamage()+1));
						probeInfo.text(ChatFormatting.YELLOW+ Overlord.proxy.translateToLocal("mechsoldiers.top.joints", ((EntityMechSkeleton) entity).getJoints().getMaxDamage()-((EntityMechSkeleton) entity).getJoints().getItemDamage()+1, ((EntityMechSkeleton) entity).getJoints().getMaxDamage()+1));
					}
				}
			});
			return null;
		}
	}
}
