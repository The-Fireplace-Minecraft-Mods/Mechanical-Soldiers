package the_fireplace.mechsoldiers.compat.waila;

import com.mojang.realmsclient.gui.ChatFormatting;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import net.minecraft.entity.Entity;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.overlord.Overlord;

import javax.annotation.Nonnull;
import java.util.List;

public class WailaEntityProvider implements IWailaEntityProvider {
	@Nonnull
	@Override
	public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		if(entity instanceof EntityMechSkeleton) {
			currenttip.clear();
			currenttip.add(ChatFormatting.BLUE+ Overlord.proxy.translateToLocal("mechsoldiers.top.cpu", ((EntityMechSkeleton) entity).getCPU().getMaxDamage()-((EntityMechSkeleton) entity).getCPU().getItemDamage()+1, ((EntityMechSkeleton) entity).getCPU().getMaxDamage()+1));
			currenttip.add(ChatFormatting.BLUE+ Overlord.proxy.translateToLocal("mechsoldiers.top.skeleton", ((EntityMechSkeleton) entity).getSkeleton().getMaxDamage()-((EntityMechSkeleton) entity).getSkeleton().getItemDamage()+1, ((EntityMechSkeleton) entity).getSkeleton().getMaxDamage()+1));
			currenttip.add(ChatFormatting.BLUE+ Overlord.proxy.translateToLocal("mechsoldiers.top.joints", ((EntityMechSkeleton) entity).getJoints().getMaxDamage()-((EntityMechSkeleton) entity).getJoints().getItemDamage()+1, ((EntityMechSkeleton) entity).getJoints().getMaxDamage()+1));
		}
		return currenttip;
	}
}
