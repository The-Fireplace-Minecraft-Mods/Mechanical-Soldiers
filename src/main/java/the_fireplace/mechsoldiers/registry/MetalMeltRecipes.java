package the_fireplace.mechsoldiers.registry;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Map.Entry;

public class MetalMeltRecipes
{
    private static final MetalMeltRecipes METAL_MELT_RECIPES = new MetalMeltRecipes();
    private final Map<ItemStack, ItemStack> smeltingList1 = Maps.newHashMap();
    private final Map<ItemStack, ItemStack> smeltingList2 = Maps.newHashMap();

    public static MetalMeltRecipes instance()
    {
        return METAL_MELT_RECIPES;
    }

    private MetalMeltRecipes(){}

    public void addMeltingRecipe(Block output, ItemStack stack1in, ItemStack stack2in)
    {
        this.addMeltingRecipe(Item.getItemFromBlock(output), stack1in, stack2in);
    }

    public void addMeltingRecipe(Item output, ItemStack stack1in, ItemStack stack2in)
    {
        this.addMeltingRecipe(new ItemStack(output, 1, OreDictionary.WILDCARD_VALUE), stack1in, stack2in);
    }

    public void addMeltingRecipe(ItemStack output, ItemStack stack1in, ItemStack stack2in)
    {
        if (getMeltingResult(stack1in, stack2in) != null) { FMLLog.info("Ignored melting recipe with conflicting input: " + output + " = " + stack1in); return; }
        this.smeltingList1.put(output, stack1in);
        this.smeltingList2.put(output, stack2in);
    }

    @Nullable
    public ItemStack getMeltingResult(ItemStack stack1, ItemStack stack2)
    {
        for (Entry<ItemStack, ItemStack> entry : this.smeltingList1.entrySet())
        {
            for (Entry<ItemStack, ItemStack> entry2 : this.smeltingList2.entrySet()) {
                if (this.compareItemStacks(stack1, entry.getValue()) && this.compareItemStacks(stack2, entry2.getValue()) && entry.getKey().equals(entry2.getKey())) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    private boolean compareItemStacks(ItemStack stack1, ItemStack stack2)
    {
        return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == OreDictionary.WILDCARD_VALUE || stack2.getMetadata() == stack1.getMetadata());
    }

    public Map<ItemStack, ItemStack> getSmeltingList1()
    {
        return this.smeltingList1;
    }
    public Map<ItemStack, ItemStack> getSmeltingList2()
    {
        return this.smeltingList2;
    }
}