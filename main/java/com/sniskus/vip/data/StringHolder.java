package com.sniskus.vip.data;

import com.sniskus.score.data.file.custom.ColorSelector;
import com.sniskus.vip.CustomTrades;

public class StringHolder {
	private static final CustomTrades PLUGIN = CustomTrades.getInstance();

	// Plugin strings
	public static final String VERSION = PLUGIN.getDescription().getVersion();
	public static final String AUTHOR = "Sniskus";
	public static final String PLUGIN_NAME = "CustomTrades";

	// Messages
	public static final String CHAT_VERSION = "&8" + PLUGIN_NAME + " Version " + VERSION;
	public static final String CHAT_LINE = ColorSelector
			.translateColors("&7&m                                             ");
	public static final String CONSOLE_CHAT_LINE = ColorSelector
			.translateColors("&7---------------------------------------------");

	// Get the prefix
	public static String getPrefix() {
		return ColorSelector.translateColors(FileManager.getSettings().getString("Prefix"));
	}
}