package the_fireplace.mechsoldiers.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * @author The_Fireplace
 */
public class ComponentDamageGeneric extends ComponentDamageBehavior {
	private static ComponentDamageGeneric instance;

	public static ComponentDamageGeneric getInstance() {
		if (instance == null)
			instance = new ComponentDamageGeneric();
		return instance;
	}

	@Override
	public ItemStack getDamagedItemStack(ItemStack itemToDamage, DamageSource source, float amount, String material, EntityLivingBase entity) {
		switch (material.toLowerCase()) {
			case "wood":
				if (source.isFireDamage())
					amount *= 2.0;
				itemToDamage.damageItem((int)Math.ceil(amount), entity);
				return itemToDamage;
			case "iron":
				if (source == DamageSource.drown)
					amount *= 2.5;
				itemToDamage.damageItem((int)Math.ceil(amount), entity);
				return itemToDamage;
			case "bronze":
				if (source == DamageSource.lava)
					amount *= 1.2;
				itemToDamage.damageItem((int)Math.ceil(amount), entity);
				return itemToDamage;
			case "copper_redstone":
				if (source == DamageSource.drown)
					amount *= 1.25;
			case "gold_redstone":
				if (source == DamageSource.lightningBolt)
					amount *= 3.0;
				if (source == DamageSource.drown)
					amount *= 1.5;
			case "gold":
			case "copper":
				if (source.isFireDamage())
					amount *= 1.1;
			default:
				itemToDamage.damageItem((int)Math.ceil(amount), entity);
				return itemToDamage;
		}
	}
}
