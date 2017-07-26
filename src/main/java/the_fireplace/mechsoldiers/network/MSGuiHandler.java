package the_fireplace.mechsoldiers.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import the_fireplace.mechsoldiers.client.gui.GuiCPUMelter;
import the_fireplace.mechsoldiers.client.gui.GuiMetalPartConstructor;
import the_fireplace.mechsoldiers.client.gui.GuiRobot;
import the_fireplace.mechsoldiers.client.gui.GuiRobotConstructor;
import the_fireplace.mechsoldiers.container.ContainerCPUMelter;
import the_fireplace.mechsoldiers.container.ContainerMetalPartConstructor;
import the_fireplace.mechsoldiers.container.ContainerRobot;
import the_fireplace.mechsoldiers.container.ContainerRobotConstructor;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.tileentity.TileEntityCPUMelter;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartConstructor;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotConstructor;
import the_fireplace.overlord.tools.Squads;

/**
 * @author The_Fireplace
 */
public final class MSGuiHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
			case 0:
				if (entity != null && (entity instanceof TileEntityRobotConstructor || entity instanceof TileEntityPartConstructor || entity instanceof TileEntityCPUMelter)) {
					if (entity instanceof TileEntityRobotConstructor)
						return new ContainerRobotConstructor(player.inventory, (IInventory) entity);
					else if(entity instanceof TileEntityPartConstructor)
						return new ContainerMetalPartConstructor(player.inventory, (IInventory) entity);
					else //if(entity instanceof TileEntityCPUMelter)
						return new ContainerCPUMelter(player.inventory, (IInventory) entity);
				} else {
					return null;
				}
			default:
				if (world.getEntityByID(ID) != null) {
					if (world.getEntityByID(ID) instanceof EntityMechSkeleton) {
						return new ContainerRobot(player.inventory, (EntityMechSkeleton) world.getEntityByID(ID));
					}
				}
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
			case 0:
				if (entity != null && (entity instanceof TileEntityRobotConstructor || entity instanceof TileEntityPartConstructor || entity instanceof TileEntityCPUMelter)) {
					if (entity instanceof TileEntityRobotConstructor)
						return new GuiRobotConstructor(player.inventory, (TileEntityRobotConstructor) entity);
					else if (entity instanceof TileEntityPartConstructor)
						return new GuiMetalPartConstructor(player.inventory, (IInventory) entity);
					else //if(entity instanceof TileEntityCPUMelter)
						return new GuiCPUMelter(player.inventory, (IInventory) entity);
				} else {
					return null;
				}
			default:
				if (world.getEntityByID(ID) != null) {
					if (world.getEntityByID(ID) instanceof EntityMechSkeleton) {
						return new GuiRobot(player.inventory, (EntityMechSkeleton) world.getEntityByID(ID), Squads.getInstance().getSquadsFor(player.getUniqueID()));
					}
				}
				return null;
		}
	}
}
