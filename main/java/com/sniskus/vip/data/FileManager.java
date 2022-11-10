package com.sniskus.vip.data;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sniskus.vip.CustomTrades;

public class FileManager {
	private static final CustomTrades PLUGIN = CustomTrades.getInstance();
	private static final ConsoleCommandSender CONSOLE = CustomTrades.CONSOLE;

	// Folders
	public static final File FOLDER = PLUGIN.getDataFolder();

	// Files
	public static final File SETTINGS = new File(FOLDER, "Settings.yml");
	public static final File DATA = new File(FOLDER, "Data.yml");
	private static FileConfiguration settings;
	private static FileConfiguration data;

	public static void init() {
		CONSOLE.sendMessage(StringHolder.CONSOLE_CHAT_LINE);
		// Create the required folders
		File[] folders = new File[] { FOLDER };
		String[] folderNames = new String[] { "plugin" };
		boolean[] results = new boolean[folders.length];
		for (int i = 0; i < folders.length; i++)
			results[i] = loadFolder(folders[i], folderNames[i]);

		CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
		CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bLoading files..."));
		CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));

		// ----------------------------------------------------------------
		// Initialize files

		// File configurations
		loadFile(SETTINGS);
		loadFile(DATA);
		reloadFileConfigurations();

		CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', StringHolder.CONSOLE_CHAT_LINE));
	}

	// ---------------------------------------------------------------------------------------------
	// Miscellanious functions used in load()

	private static boolean loadFolder(File folder, String folderName) {
		if (folder.exists())
			return false;

		CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
		CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&eAttempting to generate the " + folderName + " folder..."));
		try {
			// Trying to create folder
			folder.mkdirs();
			CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&aThe " + folderName + " folder was successfully created!"));

		} catch (Exception e) {
			// Could not generate a folder
			CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&cThe " + folderName + " folder could not be created!"));
		}
		return true;
	}

	private static FileConfiguration loadFile(File file) {
		String filename = file.getName();
		CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eAttempting to load " + filename + "..."));
		try {
			// Attempting to load/create the file
			if (!file.exists()) {
				FileUtils.copyToFile(PLUGIN.getResource(filename), file);
				CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + filename + " created!"));
			}
			CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + filename + " loaded!"));
			CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
			return YamlConfiguration.loadConfiguration(file);
		} catch (Exception e) {
			// Could not generate the file
			e.printStackTrace();
			CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + filename + " could not be loaded!"));
			CONSOLE.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
			return null;
		}
	}

	// ---------------------------------------------------------------------------------------------

	public static FileConfiguration saveFile(File file, FileConfiguration config) {
		try {
			config.save(file);
			reloadFileConfigurations();
			return YamlConfiguration.loadConfiguration(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void reloadFileConfigurations() {
		settings = YamlConfiguration.loadConfiguration(SETTINGS);
		data = YamlConfiguration.loadConfiguration(DATA);
	}

	public static FileConfiguration getSettings() {
		return settings;
	}

	public static FileConfiguration getData() {
		return data;
	}
}