package com.sniskus.vip.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sniskus.score.data.file.custom.ColorSelector;
import com.sniskus.vip.data.StringHolder;

public class ChatUtils {

	public static void sendMessage(String s, Player player) {
		sendMessage(s, player, true);
	}

	public static void sendMessage(String s, Player player, boolean prefix) {
		com.sniskus.score.util.ChatUtils.sendMessage(ColorSelector.translateColors(s), player,
				(prefix ? StringHolder.getPrefix() : ""));
	}

	public static void sendConsoleMessage(String s) {
		sendConsoleMessage(s, true);
	}

	public static void sendConsoleMessage(String s, boolean prefix) {
		com.sniskus.score.util.ChatUtils.sendConsoleMessage(ColorSelector.translateColors(s),
				(prefix ? StringHolder.getPrefix() : ""));
	}

	public static void sendMessage(String s, CommandSender sender) {
		sendMessage(s, sender, true);
	}

	public static void sendMessage(String s, CommandSender sender, boolean prefix) {
		if (sender instanceof Player)
			sendMessage(s, (Player) sender, prefix);
		else
			sendConsoleMessage(s, prefix);
	}
}