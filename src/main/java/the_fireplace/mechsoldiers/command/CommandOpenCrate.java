package the_fireplace.mechsoldiers.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import the_fireplace.mechsoldiers.tileentity.TileEntityRobotBox;

import javax.annotation.Nonnull;

public class CommandOpenCrate extends CommandBase {
	@Nonnull
	@Override
	public String getName() {
		return "opencrate";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
		if (sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			BlockPos pos = null;
			try {
				pos = new BlockPos(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]));
			} catch (Exception e) {
				sender.sendMessage(new TextComponentString(getUsage(sender)));
			}
			if(pos != null) {
				TileEntity te = player.world.getTileEntity(pos);
				if (te != null && te instanceof TileEntityRobotBox)
					((TileEntityRobotBox) te).instBreak();
			}
		}
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender icommandsender) {
		return "/opencrate <x> <y> <z>";
	}
}
