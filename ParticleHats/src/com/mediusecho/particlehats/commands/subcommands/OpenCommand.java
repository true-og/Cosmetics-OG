package com.mediusecho.particlehats.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.commands.Command;
import com.mediusecho.particlehats.commands.Sender;
import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.permission.Permission;
import com.mediusecho.particlehats.player.PlayerState;
import com.mediusecho.particlehats.ui.GuiState;
import com.mediusecho.particlehats.ui.Menu;
import com.mediusecho.particlehats.ui.MenuInventory;
import com.mediusecho.particlehats.ui.StaticMenu;

public class OpenCommand extends Command {

	private final Database database;
	
	public OpenCommand (final Core core)
	{			
		database = core.getDatabase();
	}
	
	@Override
	public List<String> tabCompelete (Core core, Sender sender, String label, ArrayList<String> args)
	{
		if (args.size() == 1) 
		{
			Set<String> menus = database.getMenus(false).keySet();
			List<String> result = new ArrayList<String>();
			
			if (sender.hasPermission(getPermission()))
			{
				for (String menu : menus)
				{
					if (sender.hasPermission(getPermission().append(menu)) || sender.hasPermission(getPermission().append("all"))) {
						result.add(menu);
					}
				}
			}
			
			return result;
		}
		return Arrays.asList("");
	}
	
	@Override
	public boolean execute(Core core, Sender sender, String label, ArrayList<String> args) 
	{		
		// No argument
		if (args.size() == 0)
		{
			sender.sendMessage(Message.COMMAND_ERROR_ARGUMENTS);
			sender.sendMessage(Message.COMMAND_OPEN_USAGE);
			return false;
		}
		
		if (args.size() >= 1)
		{
			PlayerState playerState = core.getPlayerState(sender.getPlayerID());
			
			// Grab the name without any extensions
			String menuName = (args.get(0).contains(".") ? args.get(0).split("\\.")[0] : args.get(0));
			
			Menu menu = playerState.getOpenMenu(menuName);
			if (menu == null)
			{
				Core.debug("cache didnt exist, loading menu " + menuName);
				Database database = core.getDatabase();
				MenuInventory inventory = database.loadInventory(menuName, core.getPlayerState(sender.getPlayerID()));
				
				if (inventory == null)
				{
					sender.sendMessage(Message.COMMAND_ERROR_UNKNOWN_MENU.replace("{1}", menuName));
					return false;
				}
				
				menu = new StaticMenu(core, sender.getPlayer(), inventory);
			}
			
			playerState.setGuiState(GuiState.SWITCHING_MENU);
			playerState.setOpenMenu(menu);
			menu.open();
			
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "open menu";
	}
	
	@Override
	public String getArgumentName () {
		return "open";
	}

	@Override
	public Message getUsage() {
		return Message.COMMAND_OPEN_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_OPEN_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_OPEN;
	}
	
	@Override
	public boolean showInHelp() {
		return true;
	}

	@Override
	public boolean isPlayerOnly() {
		return true;
	}

}
