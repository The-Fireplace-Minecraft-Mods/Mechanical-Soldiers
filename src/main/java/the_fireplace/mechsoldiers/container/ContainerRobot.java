package the_fireplace.mechsoldiers.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.overlord.container.SlotAugment;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.RequestAugmentMessage;
import the_fireplace.overlord.registry.AugmentRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author The_Fireplace
 */
@ParametersAreNonnullByDefault
public class ContainerRobot extends Container {
	private EntityMechSkeleton entity;

	public ContainerRobot(InventoryPlayer invPlayer, EntityMechSkeleton entity) {
		this.entity = entity;
		InventoryBasic entityParts = entity.partInventory;
		InventoryBasic armorInv = entity.equipInventory;
		for (int x = 0; x < 9; x++) {
			this.addSlotToContainer(new Slot(invPlayer, x, 8 + x * 18, 142));//player inventory IDs 0 to 8
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new Slot(invPlayer, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));//player inventory IDs 9 to 35
			}
		}

		this.addSlotToContainer(new Slot(armorInv, 0, 6, 6));//Entity Equipment ID 0

		this.addSlotToContainer(new Slot(armorInv, 1, 28, 6) {//Entity Equipment ID 1
			@Override
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return "minecraft:items/empty_armor_slot_shield";
			}
		});

		this.addSlotToContainer(new SlotAugment(armorInv, 2, 6, 28) {
			@Override
			public int getSlotStackLimit() {
				return 1;
			}
		});//Entity Equipment ID 3

		this.addSlotToContainer(new SlotSkeleton(entityParts, 0, 63, 8, false));//Entity Inventory 0
		this.addSlotToContainer(new SlotJoints(entityParts, 1, 63 + 18, 8, false));//Entity Inventory 1
		this.addSlotToContainer(new SlotCPU(entityParts, 2, 63 + 18 + 18, 8, false));//Entity Inventory 2
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return entity.getOwner() != null && entity.getOwner().equals(playerIn);
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
			} else if (AugmentRegistry.getAugment(is) != null && !mergeItemStack(is, 36 + 6, 36 + entity.partInventory.getSizeInventory() + entity.equipInventory.getSizeInventory(), false)) {
				return null;
			} else if (!mergeItemStack(is, 36, 36 + entity.partInventory.getSizeInventory() + entity.equipInventory.getSizeInventory(), false)) {
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

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if (entity.world.isRemote)
			PacketDispatcher.sendToServer(new RequestAugmentMessage(entity));
	}
}