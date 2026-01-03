package cosmeticsOG.managers;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.hooks.CurrencyHook;
import cosmeticsOG.hooks.VanishHook;
import cosmeticsOG.hooks.economy.TokenManagerHook;
import cosmeticsOG.hooks.economy.VaultHook;
import org.bukkit.plugin.PluginManager;

public class HookManager {

    private final CosmeticsOG core;

    private CurrencyHook currencyHook;
    private VanishHook vanishHook;

    public HookManager(final CosmeticsOG core) {

        this.core = core;

        loadHooks();

    }

    public void onReload() {

        loadHooks();

    }

    /**
     * Get this plugin's CurrencyHook
     * 
     * @return
     */
    public CurrencyHook getCurrencyHook() {

        return currencyHook;

    }

    /**
     * Get this plugin's VanishHook
     * 
     * @return
     */
    public VanishHook getVanishHook() {

        return vanishHook;

    }

    private void loadHooks() {

        PluginManager pluginManager = core.getServer().getPluginManager();

        // Vault Hook.
        if (SettingsManager.FLAG_VAULT.getBoolean()) {

            if (currencyHook != null && currencyHook instanceof VaultHook) {

                return;

            }

            if (pluginManager.isPluginEnabled("Vault")) {

                currencyHook = new VaultHook(core);

                Utils.logToConsole("Hooking into Vault...");

            } else {

                Utils.logToConsole("WARNING: Could not find Vault, disabling economy support!");

                SettingsManager.FLAG_VAULT.addOverride(false);

                currencyHook = null;

            }

        }

        // TokenManager Hook.
        else if (SettingsManager.FLAG_TOKEN_MANAGER.getBoolean()) {

            if (currencyHook != null && currencyHook instanceof TokenManagerHook) {

                return;

            }

            if (pluginManager.isPluginEnabled("TokenManager")) {

                currencyHook = new TokenManagerHook();

                Utils.logToConsole("Hooking into TokenManager...");

            } else {

                Utils.logToConsole("WARNING: Could not find TokenManager, disabling economy support!");

                SettingsManager.FLAG_TOKEN_MANAGER.addOverride(false);

                currencyHook = null;

            }

        }

        // Vanish Hooks.
        if (vanishHook == null && SettingsManager.FLAG_VANISH.getBoolean()) {

            // TODO: TrueOG vanish hook.

        } else {

            if (vanishHook != null && !SettingsManager.FLAG_VANISH.getBoolean()) {

                vanishHook.unregister();
                vanishHook = null;

            }

        }

    }

}
