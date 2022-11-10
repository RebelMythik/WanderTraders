package com.sniskus.vip.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sniskus.score.commands.AbstractCommand;
import com.sniskus.score.commands.CommandInfo;
import com.sniskus.score.commands.CommandSendEvent;
import com.sniskus.vip.CustomTrades;
import com.sniskus.vip.data.FileManager;
import com.sniskus.vip.util.ChatUtils;

@CommandInfo(command = "reload", label = CustomTrades.COMMAND_LABEL)
public class Reload extends AbstractCommand {

	@Override
	public void call(@Nonnull CommandSender sender, @Nonnull String[] args, @Nonnull CommandSendEvent event) {
		if (sender instanceof Player
				&& !((Player) sender).hasPermission(FileManager.getSettings().getString("Permissions.Reload"))) {
			ChatUtils.sendMessage(FileManager.getSettings().getString("Messages.NoPermission"), sender);
			return;
		}

		FileManager.init();
		ChatUtils.sendMessage(FileManager.getSettings().getString("Messages.Reloaded"), sender);
	}

	@Override
	public @Nonnull List<String> tabcomplete(@Nonnull CommandSender arg0, @Nonnull String[] arg1) {
		return new ArrayList<>();
	}

}