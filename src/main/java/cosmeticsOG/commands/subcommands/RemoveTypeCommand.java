package cosmeticsOG.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;

// Allows for the removal of images from the database.
public class RemoveTypeCommand extends Command {

	@Override
	public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

		if (args.size() < 1) {

			if (sender.isPlayer()) {

				Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ARGUMENTS.getValue());
				Utils.cosmeticsOGPlaceholderMessage((Player) sender, getUsage().getValue());

			}
			else {

				Utils.logToConsole(Message.COMMAND_ERROR_ARGUMENTS.getValue());
				Utils.logToConsole(getUsage().getValue());

			}

			return false;

		}

		String imageName = args.get(0);
		if (! core.getDatabase().getImages(false).containsKey(imageName)) {

			if (sender.isPlayer()) {

				Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_UNKNOWN_TYPE.replace("{1}", imageName));

			}
			else {

				Utils.logToConsole(Message.COMMAND_ERROR_UNKNOWN_TYPE.replace("{1}", imageName));

			}

			return false;

		}

		if (core.getDatabase().deleteImage(imageName)) {

			if (sender.isPlayer()) {

				Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_REMOVE_TYPE_SUCCESS.replace("{1}", imageName));

			}
			else {

				Utils.logToConsole(Message.COMMAND_REMOVE_TYPE_SUCCESS.replace("{1}", imageName));

			}

			return true;

		}

		return false;

	}

	@Override
	public List<String> tabComplete (CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

		if (args.size() == 1) {

			return new ArrayList<String>(core.getDatabase().getImages(false).keySet());

		}

		return Arrays.asList("");

	}

	@Override
	public String getName() {

		return "remove type";

	}

	@Override
	public String getArgumentName () {

		return "remove";

	}

	@Override
	public Message getUsage() {

		return Message.COMMAND_REMOVE_TYPE_USAGE;

	}

	@Override
	public Message getDescription() {

		return Message.COMMAND_REMOVE_TYPE_DESCRIPTION;
	}

	@Override
	public Permission getPermission() {

		return Permission.COMMAND_TYPE_REMOVE;

	}

	@Override
	public boolean hasWildcardPermission () {

		return true;

	}

	@Override
	public Permission getWildcardPermission () {

		return Permission.COMMAND_TYPE_ALL;

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