package cosmeticsOG.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.database.Database;
import cosmeticsOG.database.properties.Group;
import cosmeticsOG.locale.Message;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.AbstractMenu;
import cosmeticsOG.ui.MenuInventory;
import cosmeticsOG.ui.StaticMenu;
import cosmeticsOG.ui.StaticMenuManager;

public class MainCommand extends Command {

	@Override
	public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) 
	{
		// Execute this command
		if (args.size() == 0) {

			if (! sender.isPlayer()) {

				Utils.logToConsole(Message.COMMAND_ERROR_PLAYER_ONLY.getValue());
				return false;

			}

			List<Group> groups = core.getDatabase().getGroups(true);
			String defaultMenu = "";
			boolean usingGroupMenu = false;

			// Check for a players group menu first
			for (Group g : groups) {

				if (sender.hasPermission(Permission.GROUP.append(g.getName()))) {

					usingGroupMenu = true;
					defaultMenu = g.getDefaultMenu();

				}

			}

			// Use default menu if nothing was found
			if (defaultMenu.equals("")) {

				defaultMenu = SettingsManager.DEFAULT_MENU.getString();

			}

			String menuName = defaultMenu.contains(".") ? defaultMenu.split("\\.")[0] : defaultMenu;
			Database database = core.getDatabase();
			PlayerState playerState = core.getPlayerState(sender.getPlayer());
			MenuInventory inventory = database.loadInventory(menuName, playerState);

			if (inventory == null) {

				if (usingGroupMenu) {

					Utils.cosmeticsOGPlaceholderMessage(sender.getPlayer(), Message.COMMAND_ERROR_UNKNOWN_GROUP_MENU.getValue().replace("{1}", menuName));

				}
				else {

					// TODO: Figure out why the menu isn't in the database.
					Player player = sender.getPlayer();
					Utils.cosmeticsOGPlaceholderMessage(player, "Does the menu exist in the database?: " + String.valueOf(database.menuExists(menuName)));
					Utils.cosmeticsOGPlaceholderMessage(player, Message.COMMAND_ERROR_UNKNOWN_MENU.getValue().replace("{1}", menuName));

				}

				return false;

			}

			StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
			AbstractMenu menu = new StaticMenu(core, staticManager, sender.getPlayer(), inventory);

			staticManager.addMenu(menu);
			menu.open();

			return true;

		}
		// Find and execute a sub-command.
		else {

			String cmd = args.get(0);
			if (! subCommands.containsKey(cmd)) {

				if(sender.isPlayer()) {

					Utils.cosmeticsOGPlaceholderMessage(sender.getPlayer(), Message.COMMAND_ERROR_UNKNOWN.getValue());

				}
				else {

					Utils.logToConsole(Message.COMMAND_ERROR_UNKNOWN.getValue());

				}

				return false;

			}

			args.remove(0);
			return subCommands.get(cmd).onCommand(core, sender, label, args);

		}

	}

	@Override
	public String getName() {
		return "main";
	}

	@Override
	public String getArgumentName () {
		return "h";
	}

	@Override 
	public Message getUsage () {
		return Message.COMMAND_MAIN_USAGE;
	}

	@Override
	public Message getDescription() {
		return Message.COMMAND_MAIN_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {
		return Permission.COMMAND_MAIN;
	}

	@Override
	public boolean showInHelp() {
		return true;
	}

	@Override
	public boolean isPlayerOnly() {
		return false;
	}

}