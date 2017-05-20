package the_fireplace.mechsoldiers.container;

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

public class ContainerMetalPartConstructor extends Container
{
    private final IInventory tileConstructor;
    private int cookTime;
    private int totalCookTime;
    private int furnaceBurnTime;
    private int currentItemBurnTime;
    private int heldWaterAmount;

    public ContainerMetalPartConstructor(InventoryPlayer playerInventory, IInventory furnaceInventory)
    {
        this.tileConstructor = furnaceInventory;
        this.addSlotToContainer(new Slot(furnaceInventory, 0, 56, 17));
        this.addSlotToContainer(new Slot(furnaceInventory, 1, 34, 17));
        this.addSlotToContainer(new SlotMPConstructorFuel(furnaceInventory, 2, 56, 53));
        this.addSlotToContainer(new SlotOutput(furnaceInventory, 3, 116, 35));
        this.addSlotToContainer(new Slot(furnaceInventory, 4, 7, 7));
        this.addSlotToContainer(new SlotOutput(furnaceInventory, 5, 116, 60));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tileConstructor);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener icontainerlistener = this.listeners.get(i);

            if (this.cookTime != this.tileConstructor.getField(2))
            {
                icontainerlistener.sendProgressBarUpdate(this, 2, this.tileConstructor.getField(2));
            }

            if (this.furnaceBurnTime != this.tileConstructor.getField(0))
            {
                icontainerlistener.sendProgressBarUpdate(this, 0, this.tileConstructor.getField(0));
            }

            if (this.currentItemBurnTime != this.tileConstructor.getField(1))
            {
                icontainerlistener.sendProgressBarUpdate(this, 1, this.tileConstructor.getField(1));
            }

            if (this.totalCookTime != this.tileConstructor.getField(3))
            {
                icontainerlistener.sendProgressBarUpdate(this, 3, this.tileConstructor.getField(3));
            }

            if (this.heldWaterAmount != this.tileConstructor.getField(4))
            {
                icontainerlistener.sendProgressBarUpdate(this, 4, this.tileConstructor.getField(4));
            }
        }

        this.cookTime = this.tileConstructor.getField(2);
        this.furnaceBurnTime = this.tileConstructor.getField(0);
        this.currentItemBurnTime = this.tileConstructor.getField(1);
        this.totalCookTime = this.tileConstructor.getField(3);
        this.heldWaterAmount = this.tileConstructor.getField(4);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        this.tileConstructor.setField(id, data);
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.tileConstructor.isUsableByPlayer(playerIn);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    @Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 2)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index != 1 && index != 0)
            {
                if (TileEntityPartConstructor.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
                    {
                        return null;
                    }
                }
                else if (index >= 3 && index < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return null;
                    }
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return null;
                } else if (!this.mergeItemStack(itemstack1, 0, 1, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }
}