package the_fireplace.mechsoldiers.items;

import net.minecraft.item.Item;
import the_fireplace.mechsoldiers.MechSoldiers;
import the_fireplace.overlord.Overlord;

/**
 * @author The_Fireplace
 */
public class ItemSkeleton extends Item {

    private String material;

    public ItemSkeleton(String material, int durability){
        setUnlocalizedName("skeleton_"+material);
        setRegistryName("skeleton_"+material);
        setMaxDamage(durability);
        this.material=material;
        setCreativeTab(MechSoldiers.TAB_ROBOT_PARTS);
    }
}
