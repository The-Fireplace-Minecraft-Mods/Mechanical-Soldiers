package the_fireplace.mechsoldiers.items;

import net.minecraft.item.Item;
import the_fireplace.overlord.Overlord;

/**
 * @author The_Fireplace
 */
public class ItemJoints extends Item {

    private String material;

    public ItemJoints(String material, int durability){
        setUnlocalizedName("joints_"+material);
        setRegistryName("joints_"+material);
        setMaxDamage(durability);
        this.material=material;
        setCreativeTab(Overlord.tabOverlord);
    }
}
