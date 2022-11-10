package com.sniskus.vip;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sniskus.score.annotation.PluginInfo;
import com.sniskus.score.commands.CommandDistributor;
import com.sniskus.vip.commands.Give;
import com.sniskus.vip.commands.Help;
import com.sniskus.vip.commands.Reload;
import com.sniskus.vip.core.GenericEvents;
import com.sniskus.vip.core.TransactionManager;
import com.sniskus.vip.data.FileManager;
import com.sniskus.vip.data.StringHolder;
import com.sniskus.vip.expansions.PlaceholderAPI;
import com.sniskus.vip.util.ChatUtils;

import net.milkbowl.vault.economy.Economy;
import ru.soknight.peconomy.PEconomy;
import ru.soknight.peconomy.api.PEconomyAPI;

@PluginInfo(niceName = "Custom Trades", promoteMCOP = false)
public class CustomTrades extends JavaPlugin {
	public static final ConsoleCommandSender CONSOLE = Bukkit.getServer().getConsoleSender();
	private static CustomTrades instance;
	public static final PlaceholderAPI PAPI = new PlaceholderAPI();

	public static CommandDistributor COMMAND_DISTRIBUTOR;
	public static final String COMMAND_LABEL = "trades";

	public static CustomTrades getInstance() {
		return instance;
	}

	{
		if (instance == null)
			instance = this;
		else
			ChatUtils.sendConsoleMessage("&c" + StringHolder.PLUGIN_NAME
					+ " might not work with reloads! Please restart your server or use &e/" + StringHolder.PLUGIN_NAME
					+ " reload &cinstead!");
	}

	@Override
	public void onEnable() {
		ChatUtils.sendConsoleMessage(StringHolder.CONSOLE_CHAT_LINE, false);
		ChatUtils.sendConsoleMessage("", false);
		ChatUtils.sendConsoleMessage("&b" + StringHolder.PLUGIN_NAME + " &aenabled", false);
		ChatUtils.sendConsoleMessage("", false);
		ChatUtils.sendConsoleMessage(StringHolder.CONSOLE_CHAT_LINE, false);
		if (!setupEconomy() || getServer().getPluginManager().getPlugin("SCore") == null) {
			ChatUtils.sendConsoleMessage("&cMissing vital dependencies, plugin disabled!", false);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		COMMAND_DISTRIBUTOR = new CommandDistributor(this, COMMAND_LABEL);
		COMMAND_DISTRIBUTOR.registerCommand(new Help());
		COMMAND_DISTRIBUTOR.registerCommand(new Reload());
		COMMAND_DISTRIBUTOR.registerCommand(new Give());

		FileManager.init();
		TransactionManager.init();

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new GenericEvents(), this);
		pm.registerEvents(new TransactionManager(), this);

		int off = 20 * 60 * 10;
		new SaveLoop().runTaskTimer(this, off, off);
	}

	private static Economy econ;

	public static Economy getEconomy() {
		return econ;
	}

	public static PEconomyAPI getPEconomy() {
		return PEconomyAPI.get();
	}

	private static String VAULT_CURRENCY;

	public static String getPEVaultCurrency() {
		return VAULT_CURRENCY;
	}

	private boolean setupEconomy() {
		PluginManager pm = getServer().getPluginManager();
		if (pm.getPlugin("Vault") == null || pm.getPlugin("PEconomy") == null)
			return false;
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();

		PEconomy p = (PEconomy) Bukkit.getServer().getPluginManager().getPlugin("PEconomy");
		VAULT_CURRENCY = p == null ? null
				: YamlConfiguration.loadConfiguration(new File(p.getDataFolder(), "currencies.yml"))
						.getString("vault.currency");

		return econ != null;
	}

	@Override
	public void onDisable() {
		ChatUtils.sendConsoleMessage(StringHolder.CONSOLE_CHAT_LINE, false);
		ChatUtils.sendConsoleMessage("", false);
		ChatUtils.sendConsoleMessage("&b" + StringHolder.PLUGIN_NAME + " &cdisabled", false);
		ChatUtils.sendConsoleMessage("", false);
		ChatUtils.sendConsoleMessage(StringHolder.CONSOLE_CHAT_LINE, false);
		TransactionManager.save();
	}

	private class SaveLoop extends BukkitRunnable {

		@Override
		public void run() {
			ChatUtils.sendConsoleMessage("&eSaving data, expect lag...");
			TransactionManager.save();
			ChatUtils.sendConsoleMessage("&eAll files have been saved!");
		}
	}
}