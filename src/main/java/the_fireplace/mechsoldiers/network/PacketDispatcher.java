package the_fireplace.mechsoldiers.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.network.packets.RequestPartsMessage;
import the_fireplace.mechsoldiers.network.packets.SetPartsMessage;
import the_fireplace.mechsoldiers.network.packets.TeSetField;

/**
 * @author The_Fireplace
 */
public final class PacketDispatcher {
	private static byte packetId = 0;

	private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(MechSoldiers.MODID);

	public static final void registerPackets() {
		registerMessage(RequestPartsMessage.Handler.class, RequestPartsMessage.class, Side.SERVER);
		registerMessage(SetPartsMessage.Handler.class, SetPartsMessage.class, Side.CLIENT);
		registerMessage(TeSetField.Handler.class, TeSetField.class, Side.SERVER);
	}

	@SuppressWarnings("unchecked")
	private static void registerMessage(Class handlerClass, Class messageClass, Side side) {
		dispatcher.registerMessage(handlerClass, messageClass, packetId++, side);
	}

	//Wrapper methods
	public static final void sendTo(IMessage message, EntityPlayerMP player) {
		dispatcher.sendTo(message, player);
	}

	public static final void sendToAll(IMessage message) {
		dispatcher.sendToAll(message);
	}

	public static final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		dispatcher.sendToAllAround(message, point);
	}

	public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
		dispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
	}

	public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
		dispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, range));
	}

	public static final void sendToDimension(IMessage message, int dimensionId) {
		dispatcher.sendToDimension(message, dimensionId);
	}

	public static final void sendToServer(IMessage message) {
		dispatcher.sendToServer(message);
	}
}
