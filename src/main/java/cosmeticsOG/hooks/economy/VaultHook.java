package cosmeticsOG.hooks.economy;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.hooks.CurrencyHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook implements CurrencyHook {

    private Economy economy;
    private boolean economyEnabled;

    public VaultHook(final CosmeticsOG core) {

        RegisteredServiceProvider<Economy> econProvider = core.getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (econProvider != null) {

            economy = econProvider.getProvider();

        }

        economyEnabled = economy != null;

    }

    @Override
    public int getBalance(Player player) {

        return (int) economy.getBalance(player);

    }

    @Override
    public boolean withdraw(Player player, int amount) {

        return economy.withdrawPlayer(player, amount).transactionSuccess();

    }

    @Override
    public boolean isEnabled() {

        return economyEnabled;

    }

}
