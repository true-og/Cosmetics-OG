package cosmeticsOG.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.editor.MetaState;
import cosmeticsOG.locale.Message;
import cosmeticsOG.player.PlayerState;

// Allows a player to modify or set metadata related to an open editor in the game.
public class MetaCommand extends EditCommand {

	public MetaCommand(final CosmeticsOG core) {

		super(core);

	}

	@Override
	public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

		if (sender.isPlayer()) {

			Player player = sender.getPlayer();
			PlayerState playerState = core.getPlayerState(player);
			if (! playerState.hasEditorOpen()) {

				Utils.cosmeticsOGPlaceholderMessage(player, Message.META_ERROR.getValue());

				return false;

			}

			if (args.size() == 0) {

				Utils.cosmeticsOGPlaceholderMessage(player, Message.COMMAND_ERROR_ARGUMENTS.getValue());
				Utils.cosmeticsOGPlaceholderMessage(player, Message.COMMAND_META_USAGE.replace("{1}", playerState.getMetaState().getSuggestion()));

				return false;

			}

			EditorMenuManager editorManager = core.getMenuManagerFactory().getEditorMenuManager(playerState);
			MetaState metaState = playerState.getMetaState();

			if (args.size() == 1 && args.get(0).equalsIgnoreCase("cancel")) {

				editorManager.reopen();

			}
			else {

				metaState.onMetaSet(editorManager, player, args);

			}

		}
		else {

			Utils.logToConsole(Message.COMMAND_ERROR_PLAYER_ONLY.getValue());

		}

		return true;

	}

	@Override
	public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

		if (sender.isPlayer()) {

			PlayerState playerState = core.getPlayerState(sender.getPlayer());

			if (args.size() == 1) {

				return Arrays.asList(playerState.getMetaState().getSuggestion(), "cancel");

			}
			else {

				return Arrays.asList(playerState.getMetaState().getSuggestion());

			}

		}

		return Arrays.asList("");

	}

	@Override
	public String getName() {

		return "meta";

	}

	@Override
	public String getArgumentName() {

		return "meta";

	}

	@Override
	public Message getUsage() {

		return Message.COMMAND_META_USAGE;

	}

	@Override
	public Message getDescription() {

		return Message.COMMAND_META_DESCRIPTION;

	}

}