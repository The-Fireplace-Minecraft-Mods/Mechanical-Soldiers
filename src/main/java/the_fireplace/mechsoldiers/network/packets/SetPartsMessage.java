package the_fireplace.mechsoldiers.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.network.packets.AbstractClientMessageHandler;

/**
 * @author The_Fireplace
 */
public class SetPartsMessage implements IMessage {

	public int skeleton;
	public NBTTagCompound inventory;

	public SetPartsMessage() {
	}

	public SetPartsMessage(int skeleton, NBTTagCompound inventory) {
		this.skeleton = skeleton;
		this.inventory = inventory;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		skeleton = buf.readInt();
		inventory = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(skeleton);
		ByteBufUtils.writeTag(buf, inventory);
	}

	public static class Handler extends AbstractClientMessageHandler<SetPartsMessage> {
		@Override
		public IMessage handleClientMessage(EntityPlayer player, SetPartsMessage message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				if (player.world.getEntityByID(message.skeleton) != null && player.world.getEntityByID(message.skeleton) instanceof EntityMechSkeleton) {
					NBTTagList mainInv = (NBTTagList) message.inventory.getTag("SkeletonParts");
					if (mainInv != null) {
						for (int i = 0; i < mainInv.tagCount(); i++) {
							NBTTagCompound item = (NBTTagCompound) mainInv.get(i);
							int slot = item.getByte("SlotSkeletonParts");
							if (slot >= 0 && slot < ((EntityMechSkeleton) player.world.getEntityByID(message.skeleton)).partInventory.getSizeInventory()) {
								((EntityMechSkeleton) player.world.getEntityByID(message.skeleton)).partInventory.setInventorySlotContents(slot, new ItemStack(item));
							}
						}
						((EntityMechSkeleton) player.world.getEntityByID(message.skeleton)).cachedClientParts = true;
					} else {
						Overlord.logWarn("List was null when reading Mechanical Skeleton's Inventory Packet");
					}
				}
			});
			return null;
		}
	}
}
