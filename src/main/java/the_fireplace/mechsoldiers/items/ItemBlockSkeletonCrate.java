package the_fireplace.mechsoldiers.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.UUID;

import static the_fireplace.overlord.Overlord.proxy;

public class ItemBlockSkeletonCrate extends ItemBlock {
	public ItemBlockSkeletonCrate(Block block) {
		super(block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			if(nbt.hasKey("OwnerUUID") && playerIn.world.getPlayerEntityByUUID(UUID.fromString(nbt.getString("OwnerUUID"))) != null)
				tooltip.add(proxy.translateToLocal("tooltip.owner")+' '+playerIn.world.getPlayerEntityByUUID(UUID.fromString(nbt.getString("OwnerUUID"))).getDisplayNameString());
			if(nbt.hasKey("RobotCPU"))
				tooltip.add(proxy.translateToLocal("color.gold")+proxy.translateToLocal("tooltip.robot_cpu") + ' ' + ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("RobotCPU")).getDisplayName());
			if(nbt.hasKey("RobotSkeleton"))
				tooltip.add(proxy.translateToLocal("color.gold")+proxy.translateToLocal("tooltip.robot_skeleton")+' '+ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("RobotSkeleton")).getDisplayName());
			if(nbt.hasKey("RobotJoints"))
				tooltip.add(proxy.translateToLocal("color.gold")+proxy.translateToLocal("tooltip.robot_joints")+' '+ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("RobotJoints")).getDisplayName());
		}
	}
}
