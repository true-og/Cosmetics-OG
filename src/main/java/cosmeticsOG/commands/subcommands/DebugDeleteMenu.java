package cosmeticsOG.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;

// Allows for the deletion of menus from the database.
public class DebugDeleteMenu extends Command {

	@Override
	public List<String> tabComplete (CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

		return new ArrayList<String>(core.getDatabase().getMenus(false).keySet());

	}

	@Override
	public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

		String menuName = args.get(0);
		core.getDatabase().deleteMenu(menuName);

		return false;

	}

	@Override
	public String getName() {

		return "delete";

	}

	@Override
	public String getArgumentName () {

		return "delete";

	}

	@Override
	public Message getUsage() {

		return Message.COMMAND_ARGUMENT_NONE;

	}

	@Override
	public Message getDescription() {

		return Message.COMMAND_ARGUMENT_NONE;

	}

	@Override
	public Permission getPermission() {

		return Permission.COMMAND_ALL;

	}

	@Override
	public boolean showInHelp() {

		return false;

	}

	@Override
	public boolean isPlayerOnly() {

		return true;

	}

}