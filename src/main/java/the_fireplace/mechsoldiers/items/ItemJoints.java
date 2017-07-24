package the_fireplace.mechsoldiers.items;

import net.minecraft.item.Item;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.overlord.Overlord;

/**
 * @author The_Fireplace
 */
public class ItemJoints extends Item {

    public ItemJoints(String material, int durability){
        setUnlocalizedName("joints_"+material);
        setRegistryName("joints_"+material);
        setMaxDamage(durability);
        setCreativeTab(MechSoldiers.TAB_ROBOT_PARTS);
    }
}
