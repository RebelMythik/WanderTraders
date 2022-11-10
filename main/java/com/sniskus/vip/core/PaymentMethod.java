package com.sniskus.vip.core;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

import com.sniskus.score.logic.Conditions;
import com.sniskus.vip.CustomTrades;

import net.milkbowl.vault.economy.Economy;
import ru.soknight.peconomy.api.PEconomyAPI;
import ru.soknight.peconomy.database.model.WalletModel;

public enum PaymentMethod {
	VAULT(new Payment() {
		@Override
		public void modBalance(Player p, double x, String currency) {
			Economy e = CustomTrades.getEconomy();
			if (0 < x)
				e.depositPlayer(p, x);
			else
			// Unnecessary to call if x == 0
			if (x < 0)
				e.withdrawPlayer(p, Math.abs(x));
		}

		@Override
		public double getBalance(Player p, String currency) {
			return CustomTrades.getEconomy().getBalance(p);
		}
	}), PECONOMY(new Payment() {
		@Override
		public void modBalance(Player p, double x, String currency) {
			PEconomyAPI api = CustomTrades.getPEconomy();
			WalletModel wallet = api.getWallet(p.getName());
			if (0 < x)
				wallet.setAmount(currency, (float) x);
			else
			// Unnecessary to call if x == 0
			if (x < 0)
				wallet.takeAmount(currency, Math.abs((float) x));
			api.updateWallet(wallet);
		}

		@Override
		public double getBalance(Player p, String currency) {
			return CustomTrades.getPEconomy().getAmount(p.getName(), currency);
		}
	});

	private final Payment p;

	private PaymentMethod(Payment p) {
		this.p = p;
	}

	public void modify(@Nonnull Player p, double x, String currency) {
		this.p.modBalance(Conditions.assertNotNull(p), x, currency);
	}

	public void setBalance(@Nonnull Player p, double x, String currency) {
		this.p.modBalance(Conditions.assertNotNull(p), this.p.getBalance(p, currency) - x, currency);
	}

	public double getBalance(@Nonnull Player p, String currency) {
		return this.p.getBalance(Conditions.assertNotNull(p), currency);
	}

	private interface Payment {
		void modBalance(@Nonnull Player p, double x, String currency);

		double getBalance(@Nonnull Player p, String currency);
	}
}