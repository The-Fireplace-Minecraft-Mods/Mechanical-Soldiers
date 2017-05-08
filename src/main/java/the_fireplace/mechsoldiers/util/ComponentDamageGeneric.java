package the_fireplace.mechsoldiers.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * @author The_Fireplace
 */
public final class ComponentDamageGeneric extends ComponentDamageBehavior {
    public static ComponentDamageGeneric instance;
    public ComponentDamageGeneric(){
        instance = this;
    }

    @Override
    public ItemStack getDamagedItemStack(ItemStack itemToDamage, DamageSource source, float amount, String material, EntityLivingBase entity) {
        switch(material.toLowerCase()){
            case "wood":
                if(source.isFireDamage())
                    amount *= 2;
                itemToDamage.damageItem(Math.round(amount), entity);
                return itemToDamage;
            case "iron":
                if(source == DamageSource.drown)
                    amount *= 1.5;
                itemToDamage.damageItem(Math.round(amount), entity);
                return itemToDamage;
            case "copper_redstone":
            case "gold_redstone":
                if(source == DamageSource.lightningBolt)
                    amount *= 3;
            case "gold":
            case "copper":
                if(source.isFireDamage())
                    amount *= 1.1F;
            default:
                itemToDamage.damageItem(Math.round(amount), entity);
                return itemToDamage;
        }
    }
}
