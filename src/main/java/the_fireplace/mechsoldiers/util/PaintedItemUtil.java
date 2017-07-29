package the_fireplace.mechsoldiers.util;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;

public class PaintedItemUtil {
	@Nullable
	public static Color getColor(ItemStack stack){
		return stack.hasTagCompound() ? new Color(stack.getTagCompound().getInteger("PaintColor")) : null;
	}
}
