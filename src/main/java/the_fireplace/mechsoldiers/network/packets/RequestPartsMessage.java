package the_fireplace.mechsoldiers.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.network.packets.AbstractServerMessageHandler;

/**
 * @author The_Fireplace
 */
public class RequestPartsMessage implements IMessage {

	public int mechSkeletonId;

	public RequestPartsMessage() {
	}

	public RequestPartsMessage(EntityMechSkeleton skeleton) {
		this.mechSkeletonId = skeleton.getEntityId();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		mechSkeletonId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(mechSkeletonId);
	}

	public static class Handler extends AbstractServerMessageHandler<RequestPartsMessage> {
		@Override
		public IMessage handleServerMessage(EntityPlayer player, RequestPartsMessage message, MessageContext ctx) {
			InventoryBasic parts = null;
			if (player.world.getEntityByID(message.mechSkeletonId) != null) {
				if (player.world.getEntityByID(message.mechSkeletonId) instanceof EntityMechSkeleton) {
					if (((EntityMechSkeleton) player.world.getEntityByID(message.mechSkeletonId)).partInventory != null) {
						parts = ((EntityMechSkeleton) player.world.getEntityByID(message.mechSkeletonId)).partInventory;
					}
				} else {
					Overlord.logError("Entity is not a Mech Skeleton. It is " + player.world.getEntityByID(message.mechSkeletonId).toString());
				}
			} else {
				if(!Loader.isModLoaded("theoneprobe"))
					Overlord.logError("Error 404: Mech Skeleton not found: " + message.mechSkeletonId);
			}

			if (parts == null)
				return null;

			NBTTagCompound inventory = new NBTTagCompound();

			NBTTagList mainInv = new NBTTagList();
			for (int i = 0; i < parts.getSizeInventory(); i++) {
				ItemStack is = parts.getStackInSlot(i);
				if (is != null) {
					NBTTagCompound item = new NBTTagCompound();

					item.setByte("SlotSkeletonParts", (byte) i);
					is.writeToNBT(item);

					mainInv.appendTag(item);
				}
			}
			inventory.setTag("SkeletonParts", mainInv);

			return new SetPartsMessage(message.mechSkeletonId, inventory);
		}
	}
}