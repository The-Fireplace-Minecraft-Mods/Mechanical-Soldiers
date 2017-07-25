package the_fireplace.mechsoldiers.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.overlord.container.SlotOutput;
import the_fireplace.overlord.container.SlotSeal;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author The_Fireplace
 */
@ParametersAreNonnullByDefault
public class ContainerRobotConstructor extends Container {
	private IInventory te;

	public ContainerRobotConstructor(InventoryPlayer invPlayer, IInventory entity) {
		this.te = entity;

		for (int x = 0; x < 9; x++) {
			this.addSlotToContainer(new Slot(invPlayer, x, 8 + x * 18, 142));//player inventory IDs 0 to 8
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new Slot(invPlayer, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));//player inventory IDs 9 to 35
			}
		}

		this.addSlotToContainer(new SlotSeal(entity, 0, 6, 6));//tile entity ID 0

		this.addSlotToContainer(new SlotCPU(entity, 1, 48, 26, true));//tile entity ID 1

		this.addSlotToContainer(new SlotSkeleton(entity, 2, 80, 21, true));//tile entity ID 2

		this.addSlotToContainer(new SlotJoints(entity, 3, 112, 26, true));//tile entity ID 3

		this.addSlotToContainer(new SlotOutput(entity, 4, 80, 57));//tile entity ID 4
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		Slot slot = getSlot(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack is = slot.getStack();
			ItemStack result = is.copy();

			if (i >= 36) {
				if (!mergeItemStack(is, 0, 36, false)) {
					return null;
				}
			} else if (!mergeItemStack(is, 36, 36 + te.getSizeInventory(), false)) {
				return null;
			}
			if (is.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
			slot.onPickupFromSlot(player, is);
			return result;
		}
		return null;
	}
}

