package the_fireplace.mechsoldiers.client;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.client.render.CPUMTESR;
import the_fireplace.mechsoldiers.client.render.MPCTESR;
import the_fireplace.mechsoldiers.client.render.MechSkeletonRenderFactory;
import the_fireplace.mechsoldiers.client.render.MiniTankTESR;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.network.CommonProxy;
import the_fireplace.mechsoldiers.tileentity.TileEntityCPUMelter;
import the_fireplace.mechsoldiers.tileentity.TileEntityMiniTank;
import the_fireplace.mechsoldiers.tileentity.TileEntityPartConstructor;

/**
 * @author The_Fireplace
 */
public class ClientProxy extends CommonProxy {
	@Override
	public void registerClient() {
		MechSoldiers.instance.registerItemRenders();
		RenderingRegistry.registerEntityRenderingHandler(EntityMechSkeleton.class, new MechSkeletonRenderFactory());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPartConstructor.class, new MPCTESR());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMiniTank.class, new MiniTankTESR());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCPUMelter.class, new CPUMTESR());
	}
}
