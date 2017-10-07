package the_fireplace.mechsoldiers.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import the_fireplace.mechsoldiers.MechSoldiers;

@Mod.EventBusSubscriber
public class LootHandler {
	public static final ResourceLocation drive_table = new ResourceLocation(MechSoldiers.MODID, "drive_table");
	public static final ResourceLocation skeleton_table = new ResourceLocation(MechSoldiers.MODID, "skeleton_table");
	public static final ResourceLocation blueprint_table = new ResourceLocation(MechSoldiers.MODID, "blueprint_table");

	public LootHandler() {
		LootTableList.register(drive_table);
		LootTableList.register(blueprint_table);
		LootTableList.register(skeleton_table);
	}

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent event) {
		if (event.getName().toString().equals("minecraft:chests/nether_bridge") || event.getName().toString().equals("minecraft:chests/stronghold_library"))
			event.getTable().addPool(new LootPool(new LootEntry[]{new LootEntryTable(drive_table, 1, 1, new LootCondition[]{}, drive_table.toString())}, new LootCondition[]{}, new RandomValueRange(1), new RandomValueRange(0), drive_table.toString()));
		if (event.getName().toString().equals("minecraft:chests/nether_bridge") || event.getName().toString().equals("minecraft:chests/stronghold_library") || event.getName().toString().equals("minecraft:chests/end_city_treasure") || event.getName().toString().equals("lostcities:chests/raildungeonchest"))
			event.getTable().addPool(new LootPool(new LootEntry[]{new LootEntryTable(blueprint_table, 1, 1, new LootCondition[]{}, blueprint_table.toString())}, new LootCondition[]{}, new RandomValueRange(1), new RandomValueRange(0), blueprint_table.toString()));
		if (event.getName().toString().equals("minecraft:chests/jungle_temple") || event.getName().toString().equals("minecraft:chests/desert_pyramid") || event.getName().toString().equals("minecraft:chests/end_city_treasure"))
			event.getTable().addPool(new LootPool(new LootEntry[]{new LootEntryTable(skeleton_table, 1, 1, new LootCondition[]{}, skeleton_table.toString())}, new LootCondition[]{}, new RandomValueRange(1, event.getName().toString().equals("minecraft:chests/end_city_treasure") ? 3 : 1), new RandomValueRange(0), skeleton_table.toString()));
	}
}
