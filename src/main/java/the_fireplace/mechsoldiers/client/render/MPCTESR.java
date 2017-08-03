package the_fireplace.mechsoldiers.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.IFluidTank;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartConstructor;

/**
 * @author The_Fireplace
 */
public class MPCTESR extends TileEntitySpecialRenderer<TileEntityPartConstructor> {
	@Override
	public void render(TileEntityPartConstructor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		if (te != null) {

			final IFluidTank fluid = te;

			if (fluid != null && fluid.getFluid() != null && fluid.getFluidAmount() > 0) {

				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();

				FluidRenderer.renderFluid(fluid.getFluid(), te.getPos(), -0.05d, 0.0d, -0.05d, 0.1d, 0.0d, 0.1d, 1.0d, (double) fluid.getFluidAmount() / (double) fluid.getCapacity() * 0.25d, 1.0d, fluid.getFluid().getFluid().getColor());

				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
	}
}
