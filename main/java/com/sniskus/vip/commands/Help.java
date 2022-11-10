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
import com.sniskus.vip.data.StringHolder;
import com.sniskus.vip.util.ChatUtils;

@CommandInfo(command = "help", label = CustomTrades.COMMAND_LABEL)
public class Help extends AbstractCommand {

	@Override
	public void call(@Nonnull CommandSender sender, @Nonnull String[] args, @Nonnull CommandSendEvent event) {
		List<String> messages = new ArrayList<>();
		String chatLine = sender instanceof Player ? StringHolder.CHAT_LINE : StringHolder.CONSOLE_CHAT_LINE;
		messages.add(chatLine);
		messages.add("&bCustom Trades &8&l| &eHelp Page");
		// messages.add("&b[required] | <optional>");
		messages.add("");
		messages.add("&e/trades help");
		messages.add("&e/trades reload");
		messages.add("");
		messages.add(StringHolder.CHAT_VERSION);
		messages.add(chatLine);

		for (String message : messages)
			ChatUtils.sendMessage(message, sender, false);
	}

	@Override
	public @Nonnull List<String> tabcomplete(@Nonnull CommandSender arg0, @Nonnull String[] arg1) {
		return new ArrayList<>();
	}

}