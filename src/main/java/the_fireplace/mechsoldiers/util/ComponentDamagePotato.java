package the_fireplace.mechsoldiers.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

/**
 * @author The_Fireplace
 */
public final class ComponentDamagePotato extends ComponentDamageBehavior {
	private static ComponentDamagePotato instance;

	public static ComponentDamagePotato getInstance() {
		if (instance == null)
			new ComponentDamagePotato();
		return instance;
	}

	public ComponentDamagePotato() {
		instance = this;
	}

	@Override
	public ItemStack getDamagedItemStack(ItemStack itemToDamage, DamageSource source, float amount, String material, EntityLivingBase entity) {
		switch (material.toLowerCase()) {
			case "potato":
				if (source.isFireDamage())
					itemToDamage = new ItemStack(Items.BAKED_POTATO, itemToDamage.getCount(), itemToDamage.getMetadata());
				return itemToDamage;
			default:
				return itemToDamage;
		}
	}
}
