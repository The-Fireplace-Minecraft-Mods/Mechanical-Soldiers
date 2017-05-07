package the_fireplace.mechsoldiers.registry;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import the_fireplace.mechsoldiers.util.ComponentDamageBehavior;
import the_fireplace.mechsoldiers.util.EnumPartType;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

/**
 * @author The_Fireplace
 */
@ParametersAreNonnullByDefault
public final class PartRegistry {
    private static PartRegistry instance;
    private HashMap<ItemStack, EnumPartType> partTypes=Maps.newHashMap();
    private HashMap<ItemStack, ComponentDamageBehavior> partBehaviors=Maps.newHashMap();
    private HashMap<ItemStack, String> partMaterials=Maps.newHashMap();
    private HashMap<ItemStack, ResourceLocation> partTextures=Maps.newHashMap();
    public PartRegistry(){
        if(instance == null)
            instance = this;
    }

    /**
     * Register an item as a part for Mechanical Skeletons
     * @param item
     *  The item to be registered
     * @param type
     *  The part type
     * @param behavior
     *  The behavior instance defining how the item takes damage
     * @param material
     *  The material the item is made of. Used in determining how much damage an item takes.
     * @param texture
     *  The texture to render the part with. Should only be null for the brain.
     */
    public static void registerPart(Item item, EnumPartType type, ComponentDamageBehavior behavior, String material, @Nullable ResourceLocation texture){
        registerPart(item, OreDictionary.WILDCARD_VALUE, type, behavior, material, texture);
    }

    /**
     * @deprecated INTERNAL USE ONLY
     */
    @Deprecated
    public static void registerPart(Item item, int meta, EnumPartType type, ComponentDamageBehavior behavior, String material, @Nullable ResourceLocation texture){
        instance.partTypes.put(new ItemStack(item, 1, meta), type);
        instance.partBehaviors.put(new ItemStack(item, 1, meta), behavior);
        instance.partMaterials.put(new ItemStack(item, 1, meta), material);
        if(texture != null)
            instance.partTextures.put(new ItemStack(item, 1, meta), texture);
    }

    public static boolean isPart(ItemStack part){
        if(part == null)
            return false;
        for(ItemStack stack:instance.partTypes.keySet())
            if(stack.getItem() == part.getItem() && (stack.getMetadata() == part.getMetadata() || part.getMetadata() == OreDictionary.WILDCARD_VALUE))
                return true;
        return false;
    }

    public static boolean isPartOfType(ItemStack part, EnumPartType slotType){
        if(part == null || slotType == null || !isPart(part))
            return false;
        for(ItemStack stack:instance.partTypes.keySet())
            if(stack.getItem() == part.getItem() && (stack.getMetadata() == part.getMetadata() || part.getMetadata() == OreDictionary.WILDCARD_VALUE))
                return instance.partTypes.get(stack) == slotType;
        return false;
    }

    public static ItemStack damagePart(ItemStack part, DamageSource source, float amount, EntityLivingBase entity){
        if(!isPart(part) || !part.isItemStackDamageable())
            return part;
        for(ItemStack stack:instance.partBehaviors.keySet())
            if(stack.getItem() == part.getItem() && (stack.getMetadata() == part.getMetadata() || part.getMetadata() == OreDictionary.WILDCARD_VALUE))
                return instance.partBehaviors.get(stack).getDamagedItemStack(part, source, amount, instance.partMaterials.get(stack), entity);
        return part;
    }

    @Nullable
    public static ResourceLocation getTexLocation(ItemStack part){
        if(!isPart(part))
            return null;
        for(ItemStack stack:instance.partTextures.keySet())
            if(stack.getItem() == part.getItem() && (stack.getMetadata() == part.getMetadata() || part.getMetadata() == OreDictionary.WILDCARD_VALUE))
                return instance.partTextures.get(stack);
        return null;
    }
}
