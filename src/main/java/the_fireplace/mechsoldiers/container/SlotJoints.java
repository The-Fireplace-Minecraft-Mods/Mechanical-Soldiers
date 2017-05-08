package the_fireplace.mechsoldiers.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import the_fireplace.mechsoldiers.registry.PartRegistry;
import the_fireplace.mechsoldiers.util.EnumPartType;

/**
 * @author The_Fireplace
 */
public class SlotJoints extends Slot {
    IInventory inv;
    boolean usable;

    public SlotJoints(IInventory inventoryIn, int index, int xPosition,
                      int yPosition, boolean usable) {
        super(inventoryIn, index, xPosition, yPosition);
        inv = inventoryIn;
        this.usable=usable;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return usable && PartRegistry.isPartOfType(stack, EnumPartType.JOINTS);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return usable;
    }
}
