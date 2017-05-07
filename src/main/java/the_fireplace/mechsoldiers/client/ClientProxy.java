package the_fireplace.mechsoldiers.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.mechsoldiers.client.render.MechSkeletonRenderFactory;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;
import the_fireplace.mechsoldiers.network.CommonProxy;

/**
 * @author The_Fireplace
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void registerClient(){
        MechSoldiers.instance.registerItemRenders();
        RenderingRegistry.registerEntityRenderingHandler(EntityMechSkeleton.class, new MechSkeletonRenderFactory());
    }
}
