package cosmeticsOG.commands.subcommands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;

// Allows for the plugin to be reloaded.
public class ReloadCommand extends Command {

	@Override
	public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {	

		core.onReload();

		if (sender.isPlayer()) {

			Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_RELOAD_SUCCESS.getValue());

		}
		else {

			Utils.logToConsole(Message.COMMAND_RELOAD_SUCCESS.getValue());

		}

		return true;

	}

	@Override
	public String getName() {

		return "reload";

	}

	@Override
	public String getArgumentName () {

		return "reload";

	}

	@Override
	public Message getUsage() {

		return Message.COMMAND_RELOAD_USAGE;

	}

	@Override
	public Message getDescription() {

		return Message.COMMAND_RELOAD_DESCRIPTION;

	}

	@Override
	public Permission getPermission() {

		return Permission.COMMAND_RELOAD;

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