package cosmeticsOG.managers;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.hooks.VanishHook;
import cosmeticsOG.hooks.vanish.SuperVanishHook;
import net.trueog.utilitiesog.UtilitiesOG;

public class HookManager {

    private static final String[] VANISH_PLUGIN_NAMES = { "Vanish-OG", "SuperVanish", "PremiumVanish",
            "VanishNoPacket" };

    private final CosmeticsOG core;

    private VanishHook vanishHook;

    public HookManager(final CosmeticsOG core) {

        this.core = core;

        loadHooks();

    }

    public void onReload() {

        loadHooks();

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

        final PluginManager pluginManager = core.getServer().getPluginManager();

        if (pluginManager.isPluginEnabled("DiamondBank-OG")) {

            UtilitiesOG.logToConsole(CosmeticsOG.getPrefix(), "Hooking into DiamondBank-OG...");

        } else {

            SettingsManager.FLAG_DIAMONDBANK.addOverride(false);

            CosmeticsOG.disableSelf("Could not find DiamondBank-OG! Disabling Cosmetics-OG...");

        }

        // Vanish Hooks.
        if (vanishHook == null && SettingsManager.FLAG_VANISH.getBoolean()) {

            final Plugin vanishPlugin = getEnabledVanishPlugin(pluginManager);
            if (vanishPlugin != null) {

                vanishHook = new SuperVanishHook(core, vanishPlugin);

                UtilitiesOG.logToConsole(CosmeticsOG.getPrefix(), "Hooking into " + vanishPlugin.getName() + "...");

            } else {

                UtilitiesOG.logToConsole(CosmeticsOG.getPrefix(),
                        "WARNING: Could not find a supported vanish plugin, disabling vanish support!");

                SettingsManager.FLAG_VANISH.addOverride(false);

            }

        } else {

            if (vanishHook != null && !SettingsManager.FLAG_VANISH.getBoolean()) {

                vanishHook.unregister();
                vanishHook = null;

            }

        }

    }

    private Plugin getEnabledVanishPlugin(PluginManager pluginManager) {

        for (String pluginName : VANISH_PLUGIN_NAMES) {

            if (pluginManager.isPluginEnabled(pluginName)) {

                return pluginManager.getPlugin(pluginName);

            }

        }

        return null;

    }

}
