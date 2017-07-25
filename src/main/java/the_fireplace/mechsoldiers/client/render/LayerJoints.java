package the_fireplace.mechsoldiers.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.registry.PartRegistry;

/**
 * @author The_Fireplace
 */
@SuppressWarnings("unchecked")
public class LayerJoints implements LayerRenderer<EntityMechSkeleton> {
	private final RenderLivingBase<?> renderer;
	private ModelMechSkeleton model;

	public LayerJoints(RenderLivingBase<?> renderer) {
		this.renderer = renderer;
		this.model = new ModelMechSkeleton(0.25F, true);
	}

	@Override
	public void doRenderLayer(EntityMechSkeleton skeleton, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.model.setModelAttributes(this.renderer.getMainModel());
		this.model.setLivingAnimations(skeleton, limbSwing, limbSwingAmount, partialTicks);

		ResourceLocation texture = PartRegistry.getTexLocation(skeleton.getJoints());
		if (texture == null)
			return;
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

		model.render(skeleton, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch, scale);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}

