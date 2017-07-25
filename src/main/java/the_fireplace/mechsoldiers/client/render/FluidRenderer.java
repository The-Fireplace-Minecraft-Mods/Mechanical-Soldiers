package the_fireplace.mechsoldiers.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author The_Fireplace
 */
@SideOnly(Side.CLIENT)
public abstract class FluidRenderer {
	public static void renderFluid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
		final float xt = (float) (pos.getX() - TileEntityRendererDispatcher.staticPlayerX);
		final float yt = (float) (pos.getY() - TileEntityRendererDispatcher.staticPlayerY);
		final float zt = (float) (pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ);

		GlStateManager.translate(xt, yt, zt);

		final Minecraft mc = Minecraft.getMinecraft();
		final Tessellator tess = Tessellator.getInstance();
		final VertexBuffer buff = tess.getBuffer();
		final int brightness = mc.world.getCombinedLight(pos, fluid.getFluid().getLuminosity());

		buff.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.pushMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else
			GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.translate(x, y, z);

		final TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
		final TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

		addQuad(buff, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.DOWN, color, brightness);
		addQuad(buff, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.NORTH, color, brightness);
		addQuad(buff, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.EAST, color, brightness);
		addQuad(buff, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.SOUTH, color, brightness);
		addQuad(buff, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.WEST, color, brightness);
		addQuad(buff, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.UP, color, brightness);
		tess.draw();

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		RenderHelper.enableStandardItemLighting();
	}

	private static void addQuad(VertexBuffer buff, TextureAtlasSprite sprite, double x, double y, double z, double width, double height, double length, EnumFacing face, int color, int brightness) {
		if (sprite == null)
			return;
		final int flv = brightness >> 0x10 & 0xFFFF;
		final int slv = brightness & 0xFFFF;
		Color colorval = new Color(color);
		final int alpha = colorval.getAlpha();
		final int red = colorval.getRed();
		final int green = colorval.getGreen();
		final int blue = colorval.getBlue();

		double minU;
		double maxU;
		double minV;
		double maxV;

		final double size = 16f;

		final double x2 = x + width;
		final double y2 = y + height;
		final double z2 = z + length;

		final double u = x % 1d;
		double u1 = u + width;

		while (u1 > 1f)
			u1 -= 1f;

		final double vy = y % 1d;
		double vy1 = vy + height;

		while (vy1 > 1f)
			vy1 -= 1f;

		final double vz = z % 1d;
		double vz1 = vz + length;

		while (vz1 > 1f)
			vz1 -= 1f;

		switch (face) {

			case DOWN:

			case UP:
				minU = sprite.getInterpolatedU(u * size);
				maxU = sprite.getInterpolatedU(u1 * size);
				minV = sprite.getInterpolatedV(vz * size);
				maxV = sprite.getInterpolatedV(vz1 * size);
				break;

			case NORTH:

			case SOUTH:
				minU = sprite.getInterpolatedU(u1 * size);
				maxU = sprite.getInterpolatedU(u * size);
				minV = sprite.getInterpolatedV(vy * size);
				maxV = sprite.getInterpolatedV(vy1 * size);
				break;

			case WEST:

			case EAST:
				minU = sprite.getInterpolatedU(vz1 * size);
				maxU = sprite.getInterpolatedU(vz * size);
				minV = sprite.getInterpolatedV(vy * size);
				maxV = sprite.getInterpolatedV(vy1 * size);
				break;

			default:
				minU = sprite.getMinU();
				maxU = sprite.getMaxU();
				minV = sprite.getMinV();
				maxV = sprite.getMaxV();
		}

		switch (face) {

			case DOWN:
				buff.pos(x, y, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(flv, slv).endVertex();
				buff.pos(x, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(flv, slv).endVertex();
				break;

			case UP:
				buff.pos(x, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(flv, slv).endVertex();
				buff.pos(x, y2, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y2, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(flv, slv).endVertex();
				break;

			case NORTH:
				buff.pos(x, y, z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(flv, slv).endVertex();
				buff.pos(x, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y, z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(flv, slv).endVertex();
				break;

			case SOUTH:
				buff.pos(x, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y2, z2).color(red, green, blue, alpha).tex(minU, minV).lightmap(flv, slv).endVertex();
				buff.pos(x, y2, z2).color(red, green, blue, alpha).tex(maxU, minV).lightmap(flv, slv).endVertex();
				break;

			case WEST:
				buff.pos(x, y, z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(flv, slv).endVertex();
				buff.pos(x, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(flv, slv).endVertex();
				buff.pos(x, y2, z2).color(red, green, blue, alpha).tex(minU, minV).lightmap(flv, slv).endVertex();
				buff.pos(x, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(flv, slv).endVertex();
				break;

			case EAST:
				buff.pos(x2, y, z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y2, z2).color(red, green, blue, alpha).tex(maxU, minV).lightmap(flv, slv).endVertex();
				buff.pos(x2, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(flv, slv).endVertex();
				break;
		}
	}
}
