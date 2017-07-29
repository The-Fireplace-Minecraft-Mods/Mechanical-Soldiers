package the_fireplace.mechsoldiers.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartStainer;
import the_fireplace.overlord.network.packets.AbstractServerMessageHandler;
import the_fireplace.overlord.tileentity.ISkeletonMaker;

/**
 * @author The_Fireplace
 */
public class TeSetField implements IMessage {

	public BlockPos pos;
	public int field;
	public int value;

	public TeSetField() {
	}

	public TeSetField(BlockPos pos, int field, int value) {
		this.pos = pos;
		this.field=field;
		this.value=value;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = new BlockPos(buf.readInt(), buf.readShort(), buf.readInt());
		field = buf.readInt();
		value = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeShort(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(field);
		buf.writeInt(value);
	}

	public static class Handler extends AbstractServerMessageHandler<TeSetField> {
		@Override
		public IMessage handleServerMessage(EntityPlayer player, TeSetField message, MessageContext ctx) {
			FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
				if (player.world.getTileEntity(message.pos) instanceof TileEntityPartStainer) {
					((IInventory) player.world.getTileEntity(message.pos)).setField(message.field, message.value);
				}
			});
			return null;
		}
	}
}