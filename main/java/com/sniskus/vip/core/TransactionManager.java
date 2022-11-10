package com.sniskus.vip.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sniskus.score.gui.AbstractGUI;
import com.sniskus.score.gui.GUIConversation;
import com.sniskus.score.util.InventoryUtils;
import com.sniskus.score.util.StringUtils;
import com.sniskus.vip.CustomTrades;
import com.sniskus.vip.data.FileManager;
import com.sniskus.vip.gui.TradeGUI;
import com.sniskus.vip.util.ChatUtils;

public class TransactionManager implements Listener {
	private static final Map<UUID, Map<String, ItemStack>> TRADER_ITEMS = new HashMap<>();
	private static final Random RANDOM = new Random();

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		TRADER_ITEMS.remove(event.getEntity().getUniqueId());
	}

	public static Map<String, ItemStack> getItems(UUID id) {
		Map<String, ItemStack> map = TRADER_ITEMS.get(id);
		if (map == null) {
			Map<String, ItemStack> ids = map = new HashMap<>();
			for (ItemStack i : ItemManager.getItems())
				if (RANDOM.nextDouble() * 100 <= ItemManager.CHANCE.get(i))
					ids.put(ItemManager.ITEM_ID.get(i), i);
			TRADER_ITEMS.put(id, map);
		}
		return new HashMap<>(map);
	}

	public static boolean isBought(UUID uid, ItemStack i) {
		return ItemManager.BOUGHT.get(TRADER_ITEMS.get(uid).get(ItemManager.ITEM_ID.get(i)));
	}

	public static void buy(Player p, ItemStack i) {
		AbstractGUI gui = GUIConversation.get(p).getOpenGUI();
		if (!(gui instanceof TradeGUI))
			return;

		UUID uid = ((TradeGUI) gui).getUUID();
		if (isBought(uid, i)) {
			ChatUtils.sendMessage(FileManager.getSettings().getString("GUI.State.AlreadyBought"), p);
			return;
		}

		if (!canAfford(p, i)) {
			ChatUtils.sendMessage(FileManager.getSettings().getString("GUI.State.CantAfford"), p);
			return;
		}

		Inventory inv = p.getInventory();
		if (InventoryUtils.isFull(inv)) {
			ChatUtils.sendMessage(FileManager.getSettings().getString("GUI.State.InventoryFull"), p);
			return;
		}

		PaymentMethod.valueOf(StringUtils.Format.ENUM.format(ItemManager.PAYMENT_METHOD.get(i))).modify(p,
				-ItemManager.PRICE.get(i), ItemManager.CURRENCY.get(i));

		for (String cmd : FileManager.getSettings().getStringList("Trades." + ItemManager.ITEM_ID.get(i) + ".Commands"))
			Bukkit.getServer().dispatchCommand(CustomTrades.CONSOLE, CustomTrades.PAPI.setPlaceholders(p, cmd));
		ItemManager.BOUGHT.set(TRADER_ITEMS.get(uid).get(ItemManager.ITEM_ID.get(i)), true);
	}

	public static boolean canAfford(Player p, ItemStack i) {
		double balance = PaymentMethod.valueOf(StringUtils.Format.ENUM.format(ItemManager.PAYMENT_METHOD.get(i)))
				.getBalance(p, ItemManager.CURRENCY.get(i));
		return ItemManager.PRICE.get(i) <= balance;
	}

	public static void init() {
		FileConfiguration c = FileManager.getData();
		ConfigurationSection cs = c.getConfigurationSection("Traders");

		if (cs == null)
			cs = c.createSection("Traders");

		for (String uid : cs.getKeys(false)) {
			Map<String, ItemStack> map = new HashMap<>();
			for (String id : cs.getConfigurationSection(uid).getKeys(false))
				map.put(id, cs.getItemStack(uid + "." + id));
			TRADER_ITEMS.put(UUID.fromString(uid), map);
		}
	}

	public static void save() {
		FileConfiguration c = FileManager.getData();
		for (Entry<UUID, Map<String, ItemStack>> e1 : TRADER_ITEMS.entrySet())
			for (Entry<String, ItemStack> e2 : e1.getValue().entrySet())
				c.set("Traders." + e1.getKey().toString() + "." + e2.getKey(), e2.getValue());
		FileManager.saveFile(FileManager.DATA, c);
	}
}