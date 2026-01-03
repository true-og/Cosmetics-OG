package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.AbstractMenu;
import cosmeticsOG.ui.StaticMenuManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

// Allows for opening a cosmetics menu on behalf of another player.
public class OpenPlayerCommand extends Command {

    private final OpenCommand parent;

    public OpenPlayerCommand(final OpenCommand parent) {

        this.parent = parent;

    }

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() <= 1) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, getUsage().getValue());

            } else {

                Utils.logToConsole(getUsage().getValue());

            }

            return false;

        }

        Player targetPlayer = getPlayer(sender, args.get(1));
        if (targetPlayer == null) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender,
                        Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(1)));

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(1)));

            }

            return false;

        }

        if (!targetPlayer.isOnline()) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender,
                        Message.COMMAND_ERROR_OFFLINE_PLAYER.getValue().replace("{1}", targetPlayer.getName()));

            } else {

                Utils.logToConsole(
                        Message.COMMAND_ERROR_OFFLINE_PLAYER.getValue().replace("{1}", targetPlayer.getName()));

            }

            return false;

        }

        PlayerState playerState = core.getPlayerState(targetPlayer.getPlayer());
        if (playerState.hasEditorOpen()) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender,
                        Message.COMMAND_OPEN_PLAYER_EDITING.replace("{1}", targetPlayer.getName()));

            } else {

                Utils.logToConsole(Message.COMMAND_OPEN_PLAYER_EDITING.replace("{1}", targetPlayer.getName()));

            }

            return false;

        }

        AbstractMenu menu = parent.getRequestedMenu(playerState, args.get(0), sender, targetPlayer);
        if (menu == null) {

            return false;

        }

        StaticMenuManager staticManager = (StaticMenuManager) playerState.getMenuManager();
        staticManager.addMenu(menu);

        menu.open();

        return true;

    }

    @Override
    public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() == 2) {

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

        return Arrays.asList("");

    }

    @Override
    public String getName() {

        return "open menu for player";

    }

    @Override
    public String getArgumentName() {

        return "player";

    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_OPEN_PLAYER_USAGE;

    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_OPEN_PLAYER_DESCRIPTION;

    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_OPEN_PLAYER;

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
