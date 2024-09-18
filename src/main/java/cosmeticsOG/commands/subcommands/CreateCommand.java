package cosmeticsOG.commands.subcommands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.database.Database;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.MenuInventory;
import cosmeticsOG.util.StringUtil;

// Creates a new cosmetics GUI menu.
public class CreateCommand extends Command {

	@Override
	public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

		if (args.size() == 1) {

			if (sender.isPlayer()) {

				PlayerState playerState = core.getPlayerState(sender.getPlayer());
				if (playerState.hasEditorOpen()) {

					Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ALREADY_EDITING.getValue());

					return false;

				}

			}

			String unsanitizedMenuName = (args.get(0).contains(".") ? args.get(0).split("\\.")[0] : args.get(0));
			String menuName = StringUtil.sanitizeString(unsanitizedMenuName);
			if (menuName.isEmpty()) {

				if (sender.isPlayer()) {

					Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_CREATE_INVALID.getValue());

				}
				else {

					Utils.logToConsole(Message.COMMAND_CREATE_INVALID.getValue());

				}

				return false;

			}

			Database database = core.getDatabase();

			// "purchase" is a reserved menu name, used for the plugin's purchase menu.
			if (database.menuExists(menuName) || menuName.equalsIgnoreCase("purchase")) {

				if (sender.isPlayer()) {

					Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_MENU_EXISTS.getValue().replace("{1}", menuName));

				}
				else {

					Utils.logToConsole(Message.COMMAND_ERROR_MENU_EXISTS.getValue().replace("{1}", menuName));

				}

				return false;

			}

			database.createMenu(menuName);

			if (! sender.isPlayer()) {

				Utils.logToConsole(Message.COMMAND_CREATE_SUCCESS.replace("{1}", menuName));

				return true;

			}
			else {

				Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_CREATE_SUCCESS.replace("{1}", menuName));

				PlayerState playerState = core.getPlayerState(sender.getPlayer());
				EditorMenuManager editorManager = core.getMenuManagerFactory().getEditorMenuManager(playerState);
				MenuInventory inventory = new MenuInventory(menuName, Utils.legacySerializerAnyCase(menuName), 6, null);

				editorManager.setEditingMenu(inventory);
				editorManager.open();

			}

			return true;

		}

		return false;

	}

	@Override
	public String getName() {

		return "create menu";

	}

	@Override
	public String getArgumentName () {

		return "create";

	}

	@Override
	public Message getUsage() {

		return Message.COMMAND_CREATE_USAGE;

	}

	@Override
	public Message getDescription() {

		return Message.COMMAND_CREATE_DESCRIPTION;

	}

	@Override
	public Permission getPermission() {

		return Permission.COMMAND_CREATE;

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