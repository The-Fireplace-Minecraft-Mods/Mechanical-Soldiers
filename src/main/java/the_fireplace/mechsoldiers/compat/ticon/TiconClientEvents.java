package the_fireplace.mechsoldiers.compat.ticon;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import the_fireplace.mechsoldiers.MechSoldiers;

public class TiconClientEvents {
	@SubscribeEvent
	public void modelRegister(ModelRegistryEvent event){
		MechSoldiers.rmm(TiconCompat.joints_cast);
		MechSoldiers.rmm(TiconCompat.skeleton_cast);
		MechSoldiers.rmm(TiconCompat.joints_cast_clay);
		MechSoldiers.rmm(TiconCompat.skeleton_cast_clay);
	}
}
