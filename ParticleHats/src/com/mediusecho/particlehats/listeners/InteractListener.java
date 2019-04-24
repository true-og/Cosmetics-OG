package com.mediusecho.particlehats.listeners;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.ui.Menu;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.ui.StaticMenu;

// TODO: [Opt] Maybe add ability to select a block, let particles be displayed in the world
//

public class InteractListener implements Listener {

	private final Core core;
	
	public InteractListener (final Core core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	@EventHandler
	public void onInteract (PlayerInteractEvent event)
	{
		ItemStack item = event.getItem();
		if (item != null)
		{
			Material checkAgainst = SettingsManager.MENU_OPEN_WITH_ITEM_MATERIAL.getMaterial();
			if (item.getType().equals(checkAgainst))
			{
				Database database = core.getDatabase();
				Player player = event.getPlayer();
				String menuName = "";
				
				if (SettingsManager.MENU_OPEN_WITH_GROUP.getBoolean())
				{
					Map<String, String> groups = database.getGroups(true);
					
					for (Entry<String, String> entry : groups.entrySet())
					{
						if (player.hasPermission(Permission.GROUP.append(entry.getKey()))) {
							menuName = entry.getValue();
						}
					}
				}
				
				else {
					menuName = SettingsManager.MENU_OPEN_DEFAULT_MENU.getString();
				}
				
				if (menuName.equals("")) {
					return;
				}
				
				PlayerState playerState = core.getPlayerState(player.getUniqueId());
				MenuInventory inventory = database.loadInventory(menuName, playerState);
				
				if (inventory == null)
				{
					player.sendMessage(Message.COMMAND_ERROR_UNKNOWN_MENU.getValue().replace("{1}", menuName));
					return;
				}
				
				Menu menu = new StaticMenu(core, player, inventory);
				
				playerState.setGuiState(GuiState.SWITCHING_MENU);
				playerState.setOpenMenu(menu);
				menu.open();
			}
		}
	}
}