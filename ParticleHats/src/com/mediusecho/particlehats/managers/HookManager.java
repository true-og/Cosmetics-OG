package com.mediusecho.particlehats.managers;

import javax.annotation.Nullable;

import org.bukkit.plugin.PluginManager;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.hooks.CurrencyHook;
import com.mediusecho.particlehats.hooks.VanishHook;
import com.mediusecho.particlehats.hooks.economy.VaultHook;
import com.mediusecho.particlehats.hooks.vanish.SuperVanishHook;

public class HookManager {

	private final Core core;
	
	private CurrencyHook currencyHook;
	private VanishHook vanishHook;
	
	public HookManager (final Core core)
	{
		this.core = core;
		loadHooks();
	}
	
	public void onReload ()
	{
		loadHooks();
	}
	
	/**
	 * Get this plugin's CurrencyHook
	 * @return
	 */
	@Nullable
	public CurrencyHook getCurrencyHook () {
		return currencyHook;
	}
	
	/**
	 * Get this plugin's VanishHook
	 * @return
	 */
	@Nullable
	public VanishHook getVanishHook () {
		return vanishHook;
	}
	
	private void loadHooks ()
	{
		PluginManager pluginManager = core.getServer().getPluginManager();
		
		// Vault Hook
		if (currencyHook == null && SettingsManager.FLAG_VAULT.getBoolean())
		{
			if (pluginManager.isPluginEnabled("Vault"))
			{
				currencyHook = new VaultHook(core);
				Core.log("hooking into Vault");
			}
		}
		
		// PlayerPoints Hook
		else if (currencyHook == null && SettingsManager.FLAG_PLAYERPOINTS.getBoolean())
		{
			if (pluginManager.isPluginEnabled("PlayerPoints"))
			{
				// TODO: PlayerPointsHook
				Core.log("hooking into PlayerPoints");
			}
		}
		
		else {
			currencyHook = null;
		}
		
		// Vanish Hooks
		if (vanishHook == null && SettingsManager.FLAG_VANISH.getBoolean())
		{
			// SuperVanish
			if (pluginManager.isPluginEnabled("SuperVanish"))
			{
				vanishHook = new SuperVanishHook(core);
				Core.log("hooking into SuperVanish");
			}
			
			// PremiumVanish
			else if (pluginManager.isPluginEnabled("PremiumVanish"))
			{
				vanishHook = new SuperVanishHook(core);
				Core.log("hooking into PremiumVanish");
			}
			
			// VanishNoPacket
			else if (pluginManager.isPluginEnabled("VanishNoPacket"))
			{
				// TODO: VanishNoPacketHook
				Core.log("hooking into VanishNoPacket");
			}
			
			else
			{
				Core.log("Unable to find a supported vanish plugin, disabling vanish support");
			}
		}
		
		else
		{
			if (vanishHook != null && !SettingsManager.FLAG_VANISH.getBoolean())
			{
				vanishHook.unregister();
				vanishHook = null;
			}
		}
	}
}