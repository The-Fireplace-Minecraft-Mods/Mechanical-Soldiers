package the_fireplace.mechsoldiers.compat.ie;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import the_fireplace.mechsoldiers.MechSoldiers;

public class IEClientEvents {
	@SubscribeEvent
	public void modelRegister(ModelRegistryEvent event){
		MechSoldiers.rmm(IECompat.joints_mold);
		MechSoldiers.rmm(IECompat.skeleton_mold);
	}
}
