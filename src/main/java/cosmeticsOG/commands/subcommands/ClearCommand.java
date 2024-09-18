package cosmeticsOG.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;

// Handles the "clear" command to remove active hats for a player.
public class ClearCommand extends Command {

	private final ClearPlayerCommand clearPlayerCommand;

	public ClearCommand () {

		clearPlayerCommand = new ClearPlayerCommand();

		register(clearPlayerCommand);

	}

	@Override
	public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

		// Self.
		if (args.size() == 0) {

			if (! sender.isPlayer()) {

				Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ARGUMENTS.getValue());
				Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_CLEAR_PLAYER_USAGE.getValue());

				return false;

			}

			core.getPlayerState(sender.getPlayer()).clearActiveHats();

			if (sender.isPlayer()) {

				Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_CLEAR_SUCCESS.getValue());

			}
			else {

				Utils.logToConsole(Message.COMMAND_CLEAR_SUCCESS.getValue());

			}

			return true;

		}

		else {

			return clearPlayerCommand.onCommand(core, sender, label, args);

		}

	}

	@Override
	public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

		if (args.size() == 1) {

			if (clearPlayerCommand.hasPermission(sender)) {

				List<String> players = new ArrayList<String>();
				for (Player p : Bukkit.getOnlinePlayers()) {

					players.add(p.getName());

				}

				if (Permission.COMMAND_SELECTORS.hasPermission(sender)) {

					players.add("@p");
					players.add("@r");

				}

				return players;

			}

		}

		return Arrays.asList("");

	}

	@Override
	public String getName() {

		return "clear";

	}

	@Override
	public String getArgumentName () {

		return "clear";

	}

	@Override
	public Message getUsage() {

		return Message.COMMAND_CLEAR_USAGE;

	}

	@Override
	public Message getDescription() {

		return Message.COMMAND_CLEAR_DESCRIPTION;

	}

	@Override
	public Permission getPermission() {

		return Permission.COMMAND_CLEAR;

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