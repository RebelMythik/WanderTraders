package com.sniskus.vip.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.sniskus.score.data.file.custom.ColorSelector;
import com.sniskus.score.data.file.serializer.Serializer;
import com.sniskus.score.tags.DataTag;
import com.sniskus.score.util.NumberUtils;
import com.sniskus.score.util.StringUtils;
import com.sniskus.vip.CustomTrades;
import com.sniskus.vip.data.FileManager;

public class ItemManager {
	private static final CustomTrades PLUGIN = CustomTrades.getInstance();
	public static final DataTag<Double, Double> CHANCE = new DataTag<>("Chance", PLUGIN, Serializer.DOUBLE);
	public static final DataTag<Double, Double> PRICE = new DataTag<>("Price", PLUGIN, Serializer.DOUBLE);
	public static final DataTag<String, String> PAYMENT_METHOD = new DataTag<>("PaymentMethod", PLUGIN,
			Serializer.STRING);
	public static final DataTag<String, String> ITEM_ID = new DataTag<>("ItemID", PLUGIN, Serializer.STRING);
	public static final DataTag<String, String> CURRENCY = new DataTag<>("Currency", PLUGIN, Serializer.STRING);
	public static final DataTag<Boolean, Byte> BOUGHT = new DataTag<>("Bought", CustomTrades.getInstance(),
			Serializer.BOOLEAN);

	public static List<ItemStack> getItems() {
		List<ItemStack> list = new ArrayList<>();
		for (String s : FileManager.getSettings().getConfigurationSection("Trades").getKeys(false))
			list.add(getItem(s));
		return list;
	}

	public static ItemStack getItem(String id) {
		FileConfiguration cfg = FileManager.getSettings();
		for (String s : cfg.getConfigurationSection("Trades").getKeys(false)) {
			if (!s.equalsIgnoreCase(id))
				continue;

			ConfigurationSection c = cfg.getConfigurationSection("Trades." + s);
			ItemStack i = new ItemStack(Material.valueOf(StringUtils.Format.ENUM.format(c.getString("Item"))));

			i.setAmount(c.getInt("Amount"));

			ItemMeta m = i.getItemMeta();
			m.setDisplayName(ColorSelector.translateColors(c.getString("Name")));
			List<String> lore = c.getStringList("Lore").stream().map(l -> ColorSelector.translateColors(l))
					.collect(Collectors.toList());
			if (lore.size() != 0)
				m.setLore(lore);
			CHANCE.set(m, c.getDouble("Chance"));
			PRICE.set(m, c.getDouble("Price"));
			PAYMENT_METHOD.set(m, c.getString("PaymentMethod", "").replaceAll("\\s+", ""));
			CURRENCY.set(m, "Vault".equalsIgnoreCase(PAYMENT_METHOD.get(m)) ? CustomTrades.getPEVaultCurrency()
					: c.getString("CurrencyID"));
			ITEM_ID.set(m, s);
			BOUGHT.set(m, false);

			String[] ench = c.getString("Enchantment", "").split(",");
			if (ench.length != 2 && i.getType() == Material.ENCHANTED_BOOK)
				((EnchantmentStorageMeta) m).addStoredEnchant(Enchantment.getByKey(NamespacedKey.minecraft(ench[0])),
						NumberUtils.parseInt(ench[1]) - 1, true);

			i.setItemMeta(m);
			return i;
		}
		return null;
	}
}