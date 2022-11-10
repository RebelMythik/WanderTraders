package com.sniskus.vip.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sniskus.score.data.file.custom.ColorSelector;
import com.sniskus.score.gui.AbstractGUI;
import com.sniskus.score.gui.Icon;
import com.sniskus.score.gui.InventoryBase;
import com.sniskus.score.gui.RefreshMethod;
import com.sniskus.score.gui.RefreshMethod.RefreshPolicy;
import com.sniskus.score.util.CollectionUtils;
import com.sniskus.score.util.InventoryUtils;
import com.sniskus.score.util.StringUtils;
import com.sniskus.vip.core.ItemManager;
import com.sniskus.vip.core.TransactionManager;
import com.sniskus.vip.data.FileManager;

public class TradeGUI extends AbstractGUI {
	private static final Map<UUID, Map<String, Integer>> ITEM_SLOTS = new HashMap<>();
	private static final Map<UUID, Set<Player>> PLAYERS_IN_GUI = new HashMap<>();

	private final UUID id;

	public TradeGUI(UUID id) {
		super("&8" + FileManager.getSettings().getString("GUI.Titles.TraderGUI"), InventoryBase.ofRowBase(3),
				RefreshMethod.ofClickBase(RefreshPolicy.SINGLE));
		this.id = id;
	}

	public UUID getUUID() {
		return id;
	}

	public static Set<Player> getPlayers(UUID id) {
		Set<Player> set = PLAYERS_IN_GUI.get(id);
		return set == null ? new HashSet<>() : new HashSet<>(set);
	}

	@Override
	protected void init(Player player) {
		Set<Player> players = PLAYERS_IN_GUI.get(id);
		if (players == null)
			players = new HashSet<>();
		players.add(player);
		PLAYERS_IN_GUI.put(id, players);

		Map<String, Integer> slots = ITEM_SLOTS.get(id);
		boolean read = slots != null;
		if (!read)
			slots = new HashMap<>();

		List<ItemStack> items = new ArrayList<>(TransactionManager.getItems(id).values());
		for (int k = 0; k < items.size(); k++) {
			ItemStack it = items.get(k);
			String id = ItemManager.ITEM_ID.get(it);

			Icon i = Icon.of(it).addClickAction(e -> TransactionManager.buy(player, it));

			int slot = read ? slots.get(id) : CollectionUtils.random(getEmpty());
			set(slot, i);

			if (!read)
				slots.put(id, slot);
		}
		if (!read)
			ITEM_SLOTS.put(id, slots);

		for (int s : getEmpty())
			set(s, FILLER);
	}

	@Override
	protected void refresh(Player player) {
		for (int i = 0; i < getSize(); i++) {
			Icon ic = get(i);
			if (ic != null && !ic.getItem().equals(FILLER.getItem())) {
				ItemStack it = ic.getItem();

				ic.clearLore();
				List<String> lore = FileManager.getSettings()
						.getStringList("Trades." + ItemManager.ITEM_ID.get(it) + ".Lore");
				for (int k = 0; k < lore.size(); k++)
					lore.set(k, ColorSelector.translateColors(lore.get(k)));

				lore.add("");
				lore.add(ColorSelector.translateColors(
						FileManager.getSettings().getString("GUI.State." + EventState.getState(it, player, id).node)));
				ic.addLore(lore);
			}
		}
	}

	private static final Icon FILLER;

	static {
		FILLER = Icon.of(Material
				.valueOf(StringUtils.Format.ENUM.format(FileManager.getSettings().getString("GUI.Filler.Item"))));
		FILLER.setName(FileManager.getSettings().getString("GUI.Filler.Name"));
	}

	@Override
	public void onClose(Player player) {
		Set<Player> players = PLAYERS_IN_GUI.get(id);
		if (players == null)
			players = new HashSet<>();
		players.remove(player);
		PLAYERS_IN_GUI.put(id, players);
	}

	private enum EventState {
		AVAILABLE("Available"), BOUGHT("AlreadyBought"), EXPENSIVE("CantAfford"), INVENTORY_FULL("InventoryFull");

		private final String node;

		EventState(String node) {
			this.node = node;
		}

		private static EventState getState(ItemStack i, Player p, UUID id) {
			if (InventoryUtils.isFull(p.getInventory()))
				return INVENTORY_FULL;
			if (TransactionManager.isBought(id, i))
				return BOUGHT;
			if (!TransactionManager.canAfford(p, i))
				return EXPENSIVE;
			return AVAILABLE;
		}
	}
}