package the_fireplace.mechsoldiers.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.container.ContainerPartPainter;
import the_fireplace.mechsoldiers.container.ContainerRobotConstructor;
import the_fireplace.mechsoldiers.network.packets.TeSetField;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartPainter;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.CreateSkeletonMessage;

import java.awt.*;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class GuiPartPainter extends GuiContainer implements GuiPageButtonList.GuiResponder, GuiSlider.FormatHelper {
	public static final ResourceLocation texture = new ResourceLocation(MechSoldiers.MODID, "textures/gui/part_painter.png");
	private TileEntityPartPainter te;
	private EntityPlayer playerUsing;

	private GuiButton paintPart;
	private GuiSlider redSlider;
	private GuiSlider greenSlider;
	private GuiSlider blueSlider;

	public GuiPartPainter(InventoryPlayer invPlayer, TileEntityPartPainter entity) {
		super(new ContainerPartPainter(invPlayer, entity));
		xSize = 175;
		ySize = 165;
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		width = res.getScaledWidth();
		height = res.getScaledHeight();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		te = entity;
		playerUsing = invPlayer.player;
	}

	@Override
	public void initGui() {
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		width = res.getScaledWidth();
		height = res.getScaledHeight();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		this.buttonList.clear();
		this.buttonList.add(paintPart = new GuiButton(0, guiLeft + 6, guiTop + 27, 20, 20, ""));
		this.buttonList.add(redSlider = new GuiSlider(this, 1, guiLeft+27, guiTop+16, "Red", 0, 255, te.getRed(), this));
		redSlider.width=114;
		this.buttonList.add(greenSlider = new GuiSlider(this, 2, guiLeft+27, guiTop+38, "Green", 0, 255, te.getGreen(), this));
		greenSlider.width=114;
		this.buttonList.add(blueSlider = new GuiSlider(this, 3, guiLeft+27, guiTop+60, "Blue", 0, 255, te.getBlue(), this));
		blueSlider.width=114;
		paintPart.enabled = false;
		super.initGui();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawString(fontRenderer, String.valueOf(te.getRed()/85), 149-fontRenderer.getStringWidth(String.valueOf(te.getRed()/85)), 22, te.hasEnoughRed() ? Color.GREEN.getRGB() : Color.RED.getRGB());
		this.drawString(fontRenderer, String.valueOf(te.getGreen()/85), 149-fontRenderer.getStringWidth(String.valueOf(te.getGreen()/85)), 44, te.hasEnoughGreen() ? Color.GREEN.getRGB() : Color.RED.getRGB());
		this.drawString(fontRenderer, String.valueOf(te.getBlue()/85), 149-fontRenderer.getStringWidth(String.valueOf(te.getBlue()/85)), 66, te.hasEnoughBlue() ? Color.GREEN.getRGB() : Color.RED.getRGB());

		drawRect(27, 5, 170, 12, new Color(te.getRed(), te.getGreen(), te.getBlue()).getRGB());
	}

	@Override
	public void updateScreen() {
		paintPart.enabled = isButtonEnabled();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button == paintPart) {
				PacketDispatcher.sendToServer(new CreateSkeletonMessage(te.getPos()));
			}
		}
	}

	private boolean isButtonEnabled() {
		return !te.getStackInSlot(0).isEmpty() && te.hasEnoughRed() && te.hasEnoughGreen() && te.hasEnoughBlue() && te.getStackInSlot(4).isEmpty();
	}

	@Override
	public void setEntryValue(int id, boolean value) {

	}

	@Override
	public void setEntryValue(int id, float value) {
		switch(id){
			case 1:
				the_fireplace.mechsoldiers.network.PacketDispatcher.sendToServer(new TeSetField(te.getPos(), 0, (int)value));
				break;
			case 2:
				the_fireplace.mechsoldiers.network.PacketDispatcher.sendToServer(new TeSetField(te.getPos(), 1, (int)value));
				break;
			case 3:
				the_fireplace.mechsoldiers.network.PacketDispatcher.sendToServer(new TeSetField(te.getPos(), 2, (int)value));
				break;
		}
	}

	@Override
	public void setEntryValue(int id, String value) {

	}

	@Override
	public String getText(int id, String name, float value) {
		return "";
	}
}

