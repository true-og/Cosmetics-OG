package cosmeticsOG.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.database.type.DatabaseType;
import cosmeticsOG.database.type.yaml.YamlDatabase;
import cosmeticsOG.hooks.VanishHook;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.HatReference;
import cosmeticsOG.player.PlayerState;

public class ConnectionListener implements Listener {

	private final CosmeticsOG core;

	public ConnectionListener (final CosmeticsOG core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin (PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		UUID id = event.getPlayer().getUniqueId();
		PlayerState playerState = core.getNewPlayerState(player);

		// Load equipped hats
		core.getDatabase().loadPlayerEquippedHats(id, (loadedHats) ->
		{
			if (loadedHats instanceof List)
			{
				@SuppressWarnings("unchecked")
				List<Hat> hats = (ArrayList<Hat>)loadedHats;				
				for (Hat hat : hats) {
					playerState.addHat(hat);
				}
			}
		});

		// Load purchased hats
		core.getDatabase().loadPlayerPurchasedHats(id, (purchasedHats) ->
		{
			if (purchasedHats instanceof List)
			{
				@SuppressWarnings("unchecked")
				List<HatReference> hats = (ArrayList<HatReference>)purchasedHats;
				for (HatReference hat : hats) {
					playerState.addPurchasedHat(hat);
				}
			}
		});

		// Load legacy purchased hats
		if (SettingsManager.CHECK_AGAINST_LEGACY_PURCHASES.getBoolean() && core.getDatabaseType() == DatabaseType.YAML)
		{
			YamlDatabase database = (YamlDatabase)core.getDatabase();
			database.loadPlayerLegacyPurchasedHats(id, (legacyHats) ->
			{
				if (legacyHats instanceof List)
				{
					@SuppressWarnings("unchecked")
					List<String> hats = (ArrayList<String>)legacyHats;
					for (String path : hats) {
						playerState.addLegacyPurchasedHat(path);
					}
				}
			}); 
		}

		VanishHook vanishHook = core.getHookManager().getVanishHook();
		if (vanishHook != null)
		{
			if (vanishHook.isVanished(player))
			{
				playerState.getActiveHats().forEach(hat -> hat.setVanished(true));
			}
		}
	}

	@EventHandler
	public void onPlayerQuit (PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		UUID id = event.getPlayer().getUniqueId();
		PlayerState playerState = core.getPlayerState(player);
		List<Hat> activeHats = playerState.getActiveHats();

		core.getDatabase().savePlayerEquippedHats(id, new ArrayList<Hat>(activeHats));
		playerState.clearActiveHats();

		core.removePlayerState(id);
	}
}
