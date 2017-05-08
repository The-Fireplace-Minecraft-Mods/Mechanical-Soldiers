package the_fireplace.mechsoldiers.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotConstructor;
import the_fireplace.overlord.network.packets.AbstractServerMessageHandler;

/**
 * @author The_Fireplace
 */
public class ConstructRobotMessage implements IMessage {

    public BlockPos pos;

    public ConstructRobotMessage() {
    }

    public ConstructRobotMessage(BlockPos pos){
        this.pos=pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readShort(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeShort(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static class Handler extends AbstractServerMessageHandler<ConstructRobotMessage> {
        @Override
        public IMessage handleServerMessage(EntityPlayer player, ConstructRobotMessage message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                if(player.world.getTileEntity(message.pos) instanceof TileEntityRobotConstructor){
                    ((TileEntityRobotConstructor) player.world.getTileEntity(message.pos)).constructRobot();
                }
            });
            return null;
        }
    }
}
