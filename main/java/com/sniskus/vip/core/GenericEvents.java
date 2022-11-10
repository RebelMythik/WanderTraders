package com.sniskus.vip.core;

import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.sniskus.score.gui.GUIConversation;
import com.sniskus.vip.gui.TradeGUI;

public class GenericEvents implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		Entity e = event.getRightClicked();

		if (e.getType() != EntityType.WANDERING_TRADER)
			return;

		event.setCancelled(true);
		new TradeGUI(event.getRightClicked().getUniqueId()).open(event.getPlayer());
	}

	@EventHandler
	public void onVillagerKill(EntityDeathEvent event) {
		Set<Player> players = TradeGUI.getPlayers(event.getEntity().getUniqueId());
		if (players == null)
			return;
		for (Player p : players)
			GUIConversation.get(p).exit();
	}
}