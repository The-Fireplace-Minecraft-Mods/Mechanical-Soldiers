package the_fireplace.mechsoldiers.compat.waila;

import mcp.MethodsReturnNonnullByDefault;
import mcp.mobius.waila.api.*;
import the_fireplace.mechsoldiers.blocks.BlockRobotBox;
import the_fireplace.mechsoldiers.entity.EntityMechSkeleton;

@WailaPlugin
@MethodsReturnNonnullByDefault
public class WailaDataProvider implements IWailaPlugin {



	@Override
	public void register(IWailaRegistrar registrar) {
		registrar.registerBodyProvider(new WailaBlockProvider(), BlockRobotBox.class);
		registrar.registerBodyProvider(new WailaEntityProvider(), EntityMechSkeleton.class);
	}
}
