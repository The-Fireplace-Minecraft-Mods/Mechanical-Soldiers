package the_fireplace.mechsoldiers.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.container.ContainerPartStainer;
import the_fireplace.mechsoldiers.network.packets.TeSetField;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartStainer;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.CreateSkeletonMessage;

import java.awt.*;

/**
 * @author The_Fireplace
 */
public class GuiPartStainer extends GuiContainer implements GuiPageButtonList.GuiResponder, GuiSlider.FormatHelper {
	public static final ResourceLocation texture = new ResourceLocation(MechSoldiers.MODID, "textures/gui/part_stainer.png");
	public static final ResourceLocation ink_texture = new ResourceLocation("textures/items/dye_powder_black.png");
	private TileEntityPartStainer te;
	private EntityPlayer playerUsing;

	private GuiButton stainPart;
	private GuiSlider redSlider;
	private GuiSlider greenSlider;
	private GuiSlider blueSlider;

	public GuiPartStainer(InventoryPlayer invPlayer, TileEntityPartStainer entity) {
		super(new ContainerPartStainer(invPlayer, entity));
		xSize = 175;
		ySize = 177;
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
		this.buttonList.add(stainPart = new GuiButton(0, guiLeft + 6, guiTop + 33, 20, 20, ""));
		this.buttonList.add(redSlider = new GuiSlider(this, 1, guiLeft + 27, guiTop + 28, "Red", 0, 255, te.getRed(), this));
		redSlider.width = 114;
		this.buttonList.add(greenSlider = new GuiSlider(this, 2, guiLeft + 27, guiTop + 50, "Green", 0, 255, te.getGreen(), this));
		greenSlider.width = 114;
		this.buttonList.add(blueSlider = new GuiSlider(this, 3, guiLeft + 27, guiTop + 72, "Blue", 0, 255, te.getBlue(), this));
		blueSlider.width = 114;
		stainPart.enabled = false;
		super.initGui();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.drawDefaultBackground();
		GlStateManager.color(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawString(fontRenderer, String.valueOf(te.getFinalRedCost()), 149 - fontRenderer.getStringWidth(String.valueOf(te.getFinalRedCost())), 34, te.hasEnoughRed() ? Color.GREEN.getRGB() : Color.RED.getRGB());
		this.drawString(fontRenderer, String.valueOf(te.getFinalGreenCost()), 149 - fontRenderer.getStringWidth(String.valueOf(te.getFinalGreenCost())), 56, te.hasEnoughGreen() ? Color.GREEN.getRGB() : Color.RED.getRGB());
		this.drawString(fontRenderer, String.valueOf(te.getFinalBlueCost()), 149 - fontRenderer.getStringWidth(String.valueOf(te.getFinalBlueCost())), 78, te.hasEnoughBlue() ? Color.GREEN.getRGB() : Color.RED.getRGB());

		drawRect(27, 5, 148, 24, new Color(te.getRed(), te.getGreen(), te.getBlue()).getRGB());

		if (te.getStackInSlot(5).isEmpty()) {
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
			Minecraft.getMinecraft().getTextureManager().bindTexture(ink_texture);
			drawModalRectWithCustomSizedTexture(152, 8, 0, 0, 16, 16, 16, 16);
			GlStateManager.resetColor();
			GlStateManager.disableBlend();
		}
	}

	@Override
	public void updateScreen() {
		stainPart.enabled = te.canSpawnSkeleton();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button == stainPart) {
				PacketDispatcher.sendToServer(new CreateSkeletonMessage(te.getPos()));
			}
		}
	}

	@Override
	public void setEntryValue(int id, boolean value) {

	}

	@Override
	public void setEntryValue(int id, float value) {
		switch (id) {
			case 1:
				the_fireplace.mechsoldiers.network.PacketDispatcher.sendToServer(new TeSetField(te.getPos(), 0, (int) value));
				break;
			case 2:
				the_fireplace.mechsoldiers.network.PacketDispatcher.sendToServer(new TeSetField(te.getPos(), 1, (int) value));
				break;
			case 3:
				the_fireplace.mechsoldiers.network.PacketDispatcher.sendToServer(new TeSetField(te.getPos(), 2, (int) value));
				break;
		}
	}

	@Override
	public void setEntryValue(int id, String value) {

	}

	@Override
	public String getText(int id, String name, float value) {
		return Overlord.proxy.translateToLocal(name);
	}
}

