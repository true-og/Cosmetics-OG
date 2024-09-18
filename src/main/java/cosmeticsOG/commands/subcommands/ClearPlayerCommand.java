package cosmeticsOG.commands.subcommands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;

// Clears the active hats of a specified player.
public class ClearPlayerCommand extends Command {

	@Override
	public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

		Player player = getPlayer(sender, args.get(0));
		if (player == null) {

			if (sender.isPlayer()) {

				Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(0)));

			}
			else {

				Utils.logToConsole(Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(0)));

			}

			return false;

		}

		if (! player.isOnline()) {

			if (sender.isPlayer()) {

				Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_OFFLINE_PLAYER.getValue().replace("{1}", player.getName()));

			}
			else {

				Utils.logToConsole(Message.COMMAND_ERROR_OFFLINE_PLAYER.getValue().replace("{1}", player.getName()));

			}

			return false;

		}

		core.getPlayerState(player).clearActiveHats();

		if (sender.isPlayer()) {

			Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_CLEAR_PLAYER_SUCCESS.getValue().replace("{1}", player.getName()));

		}
		else {

			Utils.logToConsole(Message.COMMAND_CLEAR_PLAYER_SUCCESS.getValue().replace("{1}", player.getName()));

		}

		return true;

	}

	@Override
	public String getName() {

		return "clear player";

	}

	@Override
	public String getArgumentName () {

		return "player";

	}

	@Override
	public Message getUsage() {

		return Message.COMMAND_CLEAR_PLAYER_USAGE;

	}

	@Override
	public Message getDescription() {

		return Message.COMMAND_CLEAR_PLAYER_DESCRIPTION;

	}

	@Override
	public Permission getPermission() {

		return Permission.COMMAND_CLEAR_PLAYER;

	}

	@Override
	public boolean hasWildcardPermission () {

		return true;

	}

	@Override
	public Permission getWildcardPermission () {

		return Permission.COMMAND_CLEAR_ALL;

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