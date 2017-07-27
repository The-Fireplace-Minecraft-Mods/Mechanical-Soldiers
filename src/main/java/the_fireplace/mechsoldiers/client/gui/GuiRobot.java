package the_fireplace.mechsoldiers.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.container.ContainerRobot;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.AttackModeMessage;
import the_fireplace.overlord.network.packets.MovementModeMessage;
import the_fireplace.overlord.network.packets.RequestAugmentMessage;
import the_fireplace.overlord.network.packets.SetSquadMessage;
import the_fireplace.overlord.tools.ArmyUtils;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author The_Fireplace
 */
public class GuiRobot extends GuiContainer {
	public static final ResourceLocation texture = new ResourceLocation(MechSoldiers.MODID, "textures/gui/mech_skeleton.png");
	private EntityMechSkeleton entity;

	private GuiButton attackMode;
	private byte attackModeTimer;
	private GuiButton movementMode;
	private byte movementModeTimer;
	private ArrayList<String> squads;
	private int squadIndex;

	public GuiRobot(InventoryPlayer inventorySlotsIn, EntityMechSkeleton warrior, ArrayList<String> squads) {
		super(new ContainerRobot(inventorySlotsIn, warrior));
		this.entity = warrior;
		xSize = 175;
		ySize = 165;
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		width = res.getScaledWidth();
		height = res.getScaledHeight();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		this.squads = squads;
		squadIndex = squads.indexOf(warrior.getSquad());
	}

	@Override
	public void initGui() {
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		width = res.getScaledWidth();
		height = res.getScaledHeight();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
		this.buttonList.clear();
		this.buttonList.add(attackMode = new GuiButton(0, guiLeft + 102, guiTop + 34, 66, 20, "You should not see this"));
		this.buttonList.add(movementMode = new GuiButton(1, guiLeft + 102, guiTop + 58, 66, 20, "You should not see this"));
		this.buttonList.add(new GuiButton(2, guiLeft + 5, guiTop + 49, 20, 20, "<-"));
		this.buttonList.add(new GuiButton(3, guiLeft + 74, guiTop + 49, 20, 20, "->"));
		setAttackModeText();
		setMovementModeText();
		super.initGui();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == 0) {
				PacketDispatcher.sendToServer(new AttackModeMessage(entity));
				setAttackModeText();
				scheduleAttackModeTextUpdate();
			} else if (button.id == 1) {
				PacketDispatcher.sendToServer(new MovementModeMessage(entity));
				setMovementModeText();
				scheduleMovementModeTextUpdate();
			} else if (button.id == 2) {
				if (squadIndex < 0)
					squadIndex = squads.size() - 1;
				else
					squadIndex--;
			} else if (button.id == 3) {
				if (squadIndex >= squads.size() - 1)
					squadIndex = -1;
				else
					squadIndex++;
			}
			if (squadIndex > -1)
				PacketDispatcher.sendToServer(new SetSquadMessage(entity, squads.get(squadIndex)));
			else
				PacketDispatcher.sendToServer(new SetSquadMessage(entity, ""));
		}
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
		drawRect(26, 49, 73, 69, new Color(0, 0, 0, 100).getRGB());
		this.drawCenteredString(fontRenderer, squadIndex != -1 ? squads.get(squadIndex) : I18n.format("overlord.no_squad"), 50, 55, -1);
	}

	@Override
	public void updateScreen() {
		if (attackModeTimer > 0) {
			attackModeTimer--;
		}
		if (attackModeTimer == 1) {
			setAttackModeText();
		}
		if (movementModeTimer > 0) {
			movementModeTimer--;
		}
		if (movementModeTimer == 1) {
			setMovementModeText();
		}
		super.updateScreen();
		if (!this.entity.isEntityAlive() || this.entity.isDead)
			this.mc.player.closeScreen();
	}

	public void setAttackModeText() {
		attackMode.displayString = ArmyUtils.getAttackModeString(entity.getAttackMode());
	}

	public void setMovementModeText() {
		movementMode.displayString = ArmyUtils.getMovementModeString(entity.getMovementMode());
	}

	public void scheduleAttackModeTextUpdate() {
		attackModeTimer = 5;
	}

	public void scheduleMovementModeTextUpdate() {
		movementModeTimer = 5;
	}

	@Override
	public void onGuiClosed() {
		if (entity.world.isRemote)
			PacketDispatcher.sendToServer(new RequestAugmentMessage(entity));
		super.onGuiClosed();
	}
}
