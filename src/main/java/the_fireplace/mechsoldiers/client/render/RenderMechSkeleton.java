package the_fireplace.mechsoldiers.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.network.packets.RequestPartsMessage;
import the_fireplace.mechsoldiers.registry.PartRegistry;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.RequestAugmentMessage;
import the_fireplace.overlord.tools.RenderTools;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
@SideOnly(Side.CLIENT)
public class RenderMechSkeleton extends RenderBiped<EntityMechSkeleton> {
	public RenderMechSkeleton(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelMechSkeleton(), 0.5F);
		this.addLayer(new LayerJoints(this));
	}

	@Override
	public void transformHeldFull3DItemLayer() {
		GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityMechSkeleton entity) {
		if (!entity.cachedClientAugment)
			PacketDispatcher.sendToServer(new RequestAugmentMessage(entity));
		if (!entity.cachedClientParts)
			the_fireplace.mechsoldiers.network.PacketDispatcher.sendToServer(new RequestPartsMessage(entity));
		return PartRegistry.getTexLocation(entity.getSkeleton());
	}

	@Override
	public void doRender(@Nonnull EntityMechSkeleton entityMechSkeleton, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entityMechSkeleton, x, y, z, entityYaw, partialTicks);

		if (Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null)
			if (Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Overlord.crown) {
				if (!entityMechSkeleton.cachedClientAugment)
					PacketDispatcher.sendToServer(new RequestAugmentMessage(entityMechSkeleton));
				if (!entityMechSkeleton.cachedClientParts)
					the_fireplace.mechsoldiers.network.PacketDispatcher.sendToServer(new RequestPartsMessage(entityMechSkeleton));
				RenderTools.renderItemStackOverEntity(entityMechSkeleton, entityMechSkeleton.getAugmentDisplayStack(), this, partialTicks, x, y, z);
			}
	}
}
