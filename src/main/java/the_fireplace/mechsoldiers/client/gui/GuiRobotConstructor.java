package the_fireplace.mechsoldiers.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.container.ContainerRobotConstructor;
import the_fireplace.mechsoldiers.network.PacketDispatcher;
import the_fireplace.mechsoldiers.network.packets.ConstructRobotMessage;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotConstructor;
import the_fireplace.overlord.Overlord;

import java.awt.*;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class GuiRobotConstructor extends GuiContainer {
	public static final ResourceLocation texture = new ResourceLocation(MechSoldiers.MODID, "textures/gui/robot_constructor.png");
	public static final ResourceLocation overlords_seal_texture = new ResourceLocation(Overlord.MODID, "textures/items/overlords_seal.png");
	private TileEntityRobotConstructor te;
	private EntityPlayer playerUsing;

	private GuiButton createSkeleton;

	public GuiRobotConstructor(InventoryPlayer invPlayer, TileEntityRobotConstructor entity) {
		super(new ContainerRobotConstructor(invPlayer, entity));
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
		this.buttonList.add(createSkeleton = new GuiButton(0, guiLeft + 6, guiTop + 60, 60, 20, I18n.format("skeleton_maker.create")));
		createSkeleton.enabled = false;
		super.initGui();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1F, 1F, 1F, 1F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.drawCenteredString(fontRendererObj, getWarning(), xSize / 2, -10, Color.PINK.getRGB());
		if (te.getStackInSlot(0) == null) {
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
			Minecraft.getMinecraft().getTextureManager().bindTexture(overlords_seal_texture);
			drawModalRectWithCustomSizedTexture(6, 6, 0, 0, 16, 16, 16, 16);
			GlStateManager.resetColor();
			GlStateManager.disableBlend();
		}
	}

	@Override
	public void updateScreen() {
		createSkeleton.enabled = isButtonEnabled();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == 0) {
				PacketDispatcher.sendToServer(new ConstructRobotMessage(te.getPos()));
			}
		}
	}

	private boolean isButtonEnabled() {
		return te.getStackInSlot(1) != null && te.getStackInSlot(2) != null && te.getStackInSlot(3) != null && te.getStackInSlot(4) == null;
	}

	private String getWarning() {
		if (te.getStackInSlot(0) == null) {
			return I18n.format("skeleton_maker.warning.unclaimed");
		} else {
			if (te.getStackInSlot(0).getTagCompound() == null) {
				return I18n.format("skeleton_maker.warning.unclaimed");
			} else {
				if (te.getStackInSlot(0).getTagCompound().getString("Owner").isEmpty()) {
					return I18n.format("skeleton_maker.warning.unclaimed");
				} else {
					EntityPlayer player = te.getWorld().getPlayerEntityByUUID(UUID.fromString(te.getStackInSlot(0).getTagCompound().getString("Owner")));
					if (player != null && player.equals(playerUsing)) {
						return "";
					} else {
						return I18n.format("skeleton_maker.warning.notmine", te.getStackInSlot(0).getTagCompound().getString("OwnerName"));
					}
				}
			}
		}
	}
}

