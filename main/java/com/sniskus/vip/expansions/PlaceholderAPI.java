package com.sniskus.vip.expansions;

import org.bukkit.entity.Player;

import com.sniskus.score.expansion.Expansion;

public class PlaceholderAPI extends Expansion {

	public PlaceholderAPI() {
		super("PlaceholderAPI");
	}

	public String setPlaceholders(Player p, String s) {
		return isEnabled() ? me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, s) : s;
	}
}