package com.sniskus.vip.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sniskus.score.commands.AbstractCommand;
import com.sniskus.score.commands.CommandInfo;
import com.sniskus.score.commands.CommandSendEvent;
import com.sniskus.vip.CustomTrades;
import com.sniskus.vip.core.ItemManager;
import com.sniskus.vip.data.FileManager;
import com.sniskus.vip.util.ChatUtils;

@CommandInfo(command = "give", label = CustomTrades.COMMAND_LABEL)
public class Give extends AbstractCommand {

	@Override
	public void call(@Nonnull CommandSender sender, @Nonnull String[] args, @Nonnull CommandSendEvent event) {
		if (sender instanceof Player
				&& !((Player) sender).hasPermission(FileManager.getSettings().getString("Permissions.Give"))) {
			ChatUtils.sendMessage(FileManager.getSettings().getString("Messages.NoPermission"), sender);
			return;
		}

		Player target = args.length == 2 ? Bukkit.getPlayer(args[1])
				: sender instanceof Player ? (Player) sender : null;
		String id = args.length == 0 ? null : args[0];

		if (id == null || target == null) {
			ChatUtils.sendMessage(FileManager.getSettings().getString("Messages.InvalidArgs"), sender);
			return;
		}

		ItemStack item = ItemManager.getItem(id);

		if (item == null) {
			ChatUtils.sendMessage(FileManager.getSettings().getString("Messages.UnknownItem"), sender);
			return;
		}

		target.getInventory().addItem(item);
		ChatUtils.sendMessage(FileManager.getSettings().getString("Messages.Give").replaceAll("(?i)%item%", id)
				.replaceAll("(?i)%player%", target.getName()), sender);
	}

	@Override
	public @Nonnull List<String> tabcomplete(@Nonnull CommandSender sender, @Nonnull String[] args) {
		if (args.length == 0)
			return new ArrayList<>(FileManager.getSettings().getConfigurationSection("Trades").getKeys(false));
		else
			return new ArrayList<>();
	}

}