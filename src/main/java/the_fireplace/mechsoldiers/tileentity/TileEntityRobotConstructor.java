package the_fireplace.mechsoldiers.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.registry.PartRegistry;
import the_fireplace.mechsoldiers.util.EnumPartType;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.items.ItemOverlordsSeal;

/**
 * @author The_Fireplace
 */
public class TileEntityRobotConstructor extends TileEntity implements ISidedInventory {
    private ItemStack[] inventory;

    public TileEntityRobotConstructor() {
        inventory = new ItemStack[5];
    }

    public void constructRobot(){
        if(getStackInSlot(1) == null || getStackInSlot(2) == null || getStackInSlot(3) == null || getStackInSlot(4) != null)
            return;
        ItemStack robotBox = new ItemStack(MechSoldiers.robot_box);
        NBTTagCompound robotData = new NBTTagCompound();
        if(getStackInSlot(0) != null && getStackInSlot(0).getTagCompound() != null && getStackInSlot(0).getTagCompound().getString("Owner") != null) {
            robotData.setString("OwnerUUID", getStackInSlot(0).getTagCompound().getString("Owner"));
        }else{
            robotData.setString("OwnerUUID", "0b1ec5ad-cb2a-43b7-995d-889320eb2e5b");
        }
        ItemStack oneBrain = getStackInSlot(1);
        oneBrain.stackSize=1;
        ItemStack oneSkeleton = getStackInSlot(2);
        oneSkeleton.stackSize=1;
        ItemStack oneJoints = getStackInSlot(3);
        oneJoints.stackSize=1;
        robotData.setTag("RobotBrain", oneBrain.writeToNBT(new NBTTagCompound()));
        robotData.setTag("RobotSkeleton", oneSkeleton.writeToNBT(new NBTTagCompound()));
        robotData.setTag("RobotJoints", oneJoints.writeToNBT(new NBTTagCompound()));

        robotBox.setTagCompound(robotData);

        setInventorySlotContents(4, robotBox);

        for(int i=1;i<4;i++){
            if(getStackInSlot(i).stackSize > 1)
                getStackInSlot(i).stackSize--;
            else
                setInventorySlotContents(i, null);
        }

        if(getStackInSlot(0) != null){
            if(getStackInSlot(0).getItem() instanceof ItemOverlordsSeal){
                if(((ItemOverlordsSeal)getStackInSlot(0).getItem()).isConsumable())
                    if(getStackInSlot(0).stackSize > 1)
                        getStackInSlot(0).stackSize--;
                    else
                        setInventorySlotContents(0, null);
            }
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, getBlockMetadata(), getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag(){
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public String getName() {
        return Overlord.proxy.translateToLocal("tile.robot_constructor.name");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation("tile.robot_constructor.name");
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack is = getStackInSlot(index);
        if (is != null) {
            if (is.stackSize <= count) {
                setInventorySlotContents(index, null);
            } else {
                is = is.splitStack(count);
                markDirty();
            }
        }
        return is;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack is = getStackInSlot(index);
        setInventorySlotContents(index, null);
        return is;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory[index] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        //return (index == 0 && PartRegistry.isPartOfType(stack, EnumPartType.BRAIN)) || (index == 1 && PartRegistry.isPartOfType(stack, EnumPartType.SKELETON)) || (index == 2 && PartRegistry.isPartOfType(stack, EnumPartType.JOINTS)) || (index == 3 && AugmentRegistry.getAugment(stack) != null) || index == 4 || index == 5;
        return (index == 1 && PartRegistry.isPartOfType(stack, EnumPartType.BRAIN)) || (index == 2 && PartRegistry.isPartOfType(stack, EnumPartType.SKELETON)) || (index == 3 && PartRegistry.isPartOfType(stack, EnumPartType.JOINTS)) || (index == 0 && stack.getItem() instanceof ItemOverlordsSeal);
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.length; ++i) {
            inventory[i] = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList list = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack is = getStackInSlot(i);
            if (is != null) {
                NBTTagCompound item = new NBTTagCompound();

                item.setByte("SlotRobotConstructor", (byte) i);
                is.writeToNBT(item);

                list.appendTag(item);
            }
        }
        compound.setTag("ItemsRobotConstructor", list);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList list = (NBTTagList) compound.getTag("ItemsRobotConstructor");
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound item = (NBTTagCompound) list.get(i);
                int slot = item.getByte("SlotRobotConstructor");
                if (slot >= 0 && slot < getSizeInventory()) {
                    setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
                }
            }
        } else {
            Overlord.logWarn("List was null when reading TileEntityRobotConstructor NBTTagCompound");
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.EAST || side == EnumFacing.WEST || side == EnumFacing.NORTH || side == EnumFacing.SOUTH || side == EnumFacing.UP) {
            return new int[]{0, 1, 2, 3, 4, 5};
        }else if (side == EnumFacing.DOWN) {
            return new int[]{6};
        }
        return null;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        if (stack != null) {
            if (index >= 0 &&  index < 6) {
                if(this.isItemValidForSlot(index, stack))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (stack != null)
            if (index == 6)
                return true;
        return false;
    }

    IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
    IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
    IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
            else
                return (T) handlerSide;
        return super.getCapability(capability, facing);
    }
}

