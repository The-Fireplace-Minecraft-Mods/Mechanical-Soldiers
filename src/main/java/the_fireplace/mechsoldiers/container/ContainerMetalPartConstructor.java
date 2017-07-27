package the_fireplace.mechsoldiers.container;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartConstructor;
import the_fireplace.overlord.container.SlotOutput;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ContainerMetalPartConstructor extends Container {
	private final IInventory tileConstructor;
	private int cookTime;
	private int totalCookTime;
	private int furnaceBurnTime;
	private int currentItemBurnTime;
	private int heldWaterAmount;

	public ContainerMetalPartConstructor(InventoryPlayer playerInventory, IInventory furnaceInventory) {
		this.tileConstructor = furnaceInventory;
		this.addSlotToContainer(new Slot(furnaceInventory, 0, 66, 17));
		this.addSlotToContainer(new Slot(furnaceInventory, 1, 44, 17));
		this.addSlotToContainer(new SlotMPConstructorFuel(furnaceInventory, 2, 56, 53));
		this.addSlotToContainer(new SlotOutput(furnaceInventory, 3, 116, 35));
		this.addSlotToContainer(new Slot(furnaceInventory, 4, 7, 7));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
		}
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		listener.sendAllWindowProperties(this, this.tileConstructor);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.listeners.size(); ++i) {
			IContainerListener icontainerlistener = this.listeners.get(i);

			if (this.cookTime != this.tileConstructor.getField(2)) {
				icontainerlistener.sendWindowProperty(this, 2, this.tileConstructor.getField(2));
			}

			if (this.furnaceBurnTime != this.tileConstructor.getField(0)) {
				icontainerlistener.sendWindowProperty(this, 0, this.tileConstructor.getField(0));
			}

			if (this.currentItemBurnTime != this.tileConstructor.getField(1)) {
				icontainerlistener.sendWindowProperty(this, 1, this.tileConstructor.getField(1));
			}

			if (this.totalCookTime != this.tileConstructor.getField(3)) {
				icontainerlistener.sendWindowProperty(this, 3, this.tileConstructor.getField(3));
			}

			if (this.heldWaterAmount != this.tileConstructor.getField(4)) {
				icontainerlistener.sendWindowProperty(this, 4, this.tileConstructor.getField(4));
			}
		}

		this.cookTime = this.tileConstructor.getField(2);
		this.furnaceBurnTime = this.tileConstructor.getField(0);
		this.currentItemBurnTime = this.tileConstructor.getField(1);
		this.totalCookTime = this.tileConstructor.getField(3);
		this.heldWaterAmount = this.tileConstructor.getField(4);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		this.tileConstructor.setField(id, data);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.tileConstructor.isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int i) {
		Slot slot = getSlot(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack is = slot.getStack();
			ItemStack result = is.copy();

			if (i >= 36) {
				if (!mergeItemStack(is, 0, 36, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(is, 36, 36 + tileConstructor.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}
			if (is.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			slot.onTake(player, is);
			return result;
		}
		return ItemStack.EMPTY;
	}
}