package the_fireplace.mechsoldiers.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fluids.IFluidTank;
import the_fireplace.mechsoldiers.blocks.BlockHalfMiniTank;
import the_fireplace.mechsoldiers.blocks.BlockMiniTank;
import the_fireplace.mechsoldiers.tileentity.TileEntityMiniTank;

/**
 * @author The_Fireplace
 */
public class MiniTankTESR extends TileEntitySpecialRenderer<TileEntityMiniTank> {
	@Override
	public void renderTileEntityAt(TileEntityMiniTank te, double x, double y, double z, float partialTicks, int destroyStage) {

		if (te != null) {

			final IFluidTank fluid = te;

			if (fluid != null && fluid.getFluid() != null && fluid.getFluidAmount() > 0) {

				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();

				double yOffset = 0.0d;
				if (te.hasWorld()) {
					IBlockState state = te.getWorld().getBlockState(te.getPos());
					Block block = state.getBlock();
					if (block instanceof BlockHalfMiniTank)
						if (state.getValue(BlockMiniTank.HALF).equals(BlockSlab.EnumBlockHalf.TOP))
							yOffset = 0.5d;
				}

				FluidRenderer.renderFluid(fluid.getFluid(), te.getPos(), -0.05d, yOffset, -0.05d, 0.1d, 0.05d, 0.1d, 1.0d, (double) fluid.getFluidAmount() / (double) TileEntityMiniTank.maxCapacity * 0.95d, 1.0d, fluid.getFluid().getFluid().getColor());

				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
	}
}
