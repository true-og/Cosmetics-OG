package com.mediusecho.particlehats.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.GuiState;

public class InventoryListener implements Listener {

	private final Core core;
	
	public InventoryListener (final Core core)
	{
		this.core = core;
		core.getServer().getPluginManager().registerEvents(this, core);
	}
	
	@EventHandler
	public void onInventoryClick (InventoryClickEvent event)
	{
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		
		Player player = (Player)event.getWhoClicked();
		PlayerState playerState = core.getPlayerState(player.getUniqueId());
		playerState.getGuiState().onClick(event, playerState);
		
		Core.debug("clicking, state: " + playerState.getGuiState().toString());
	}
	
	@EventHandler
	public void onInventoryClose (InventoryCloseEvent event)
	{
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		
		Player player = (Player)event.getPlayer();
		PlayerState playerState = core.getPlayerState(player.getUniqueId());
		playerState.getGuiState().onClose(playerState);
		
		Core.debug("----- closing");
	}
	
	@EventHandler
	public void onInventoryOpen (InventoryOpenEvent event)
	{
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		
		Player player = (Player)event.getPlayer();
		PlayerState playerState = core.getPlayerState(player.getUniqueId());
		GuiState guiState = playerState.getGuiState();
		
		if (guiState == GuiState.SWITCHING_MENU) {
			playerState.setGuiState(GuiState.ACTIVE);
		}
		
		else if (guiState == GuiState.SWITCHING_EDITOR) {
			playerState.setGuiState(GuiState.EDITOR);
		}
		
		Core.debug("----- opening");
	}
}
