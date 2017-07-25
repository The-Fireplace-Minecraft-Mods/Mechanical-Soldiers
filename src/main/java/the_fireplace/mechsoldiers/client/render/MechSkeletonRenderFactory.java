package the_fireplace.mechsoldiers.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;

/**
 * @author The_Fireplace
 */
public class MechSkeletonRenderFactory implements IRenderFactory<EntityMechSkeleton> {
	@SuppressWarnings("unchecked")
	@Override
	public Render createRenderFor(RenderManager manager) {
		return new RenderMechSkeleton(manager);
	}
}
