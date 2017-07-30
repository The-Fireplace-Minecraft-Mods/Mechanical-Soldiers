package the_fireplace.mechsoldiers.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.container.ContainerCPUMelter;
import the_fireplace.mechsoldiers.tileentity.TileEntityCPUMelter;

@SideOnly(Side.CLIENT)
public class GuiCPUMelter extends GuiContainer {
	private static final ResourceLocation CPUM_GUI_TEX = new ResourceLocation(MechSoldiers.MODID, "textures/gui/cpu_melter.png");
	/**
	 * The player inventory bound to this GUI.
	 */
	private final InventoryPlayer playerInventory;
	private final IInventory tileFurnace;

	public GuiCPUMelter(InventoryPlayer playerInv, IInventory furnaceInv) {
		super(new ContainerCPUMelter(playerInv, furnaceInv));
		this.playerInventory = playerInv;
		this.tileFurnace = furnaceInv;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);
		this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		int l = this.getLiquidAmountScaled(44);
		this.drawTexturedModalRect(7, 36 + 44 - l, Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(FluidRegistry.LAVA.getStill().toString()), 26, l);
		mc.getTextureManager().bindTexture(CPUM_GUI_TEX);
		this.drawTexturedModalRect(6, 35, 176, 31, 28, 46);
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(CPUM_GUI_TEX);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

		if (TileEntityCPUMelter.isActive(this.tileFurnace)) {
			int l = this.getCookProgressScaled(24);
			this.drawTexturedModalRect(i + 83, j + 30, 176, 0, 18, 1 + l);
		}
	}

	private int getCookProgressScaled(int pixels) {
		int i = this.tileFurnace.getField(0);
		int j = 5000;
		return 24 - (i != 0 ? i * pixels / j : 0);
	}

	private int getLiquidAmountScaled(int pixels) {
		int i = this.tileFurnace.getField(1);
		int j = this.tileFurnace.getField(2);
		return j != 0 && i != 0 ? i * pixels / j : 0;
	}
}