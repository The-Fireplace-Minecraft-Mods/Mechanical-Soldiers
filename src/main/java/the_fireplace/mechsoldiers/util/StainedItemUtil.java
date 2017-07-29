package the_fireplace.mechsoldiers.util;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;

public class StainedItemUtil {
	@Nullable
	public static Color getColor(ItemStack stack){
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("StainColor") ? new Color(stack.getTagCompound().getInteger("StainColor")) : null;
	}
}
