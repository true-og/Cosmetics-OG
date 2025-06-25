package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

// Allows the command runner to toggle someone else's cosmetics on or off.
public class TogglePlayerCommand extends Command {

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() != 2) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.cosmeticsOGPlaceholderMessage((Player) sender, getUsage().getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.logToConsole(getUsage().getValue());
            }

            return false;
        }

        Player player = getPlayer(sender, args.get(1));
        if (player == null) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender,
                        Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(1)));

            } else {

                Utils.logToConsole(
                        Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(1)));
            }

            return false;
        }

        if (!player.isOnline()) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender,
                        Message.COMMAND_ERROR_OFFLINE_PLAYER.getValue().replace("{1}", player.getName()));

            } else {

                Utils.logToConsole(
                        Message.COMMAND_ERROR_OFFLINE_PLAYER.getValue().replace("{1}", player.getName()));
            }

            return false;
        }

        boolean toggleStatus = StringUtil.getToggleValue(args.get(0));
        core.getPlayerState(player).toggleHats(!toggleStatus);
        if (toggleStatus) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender,
                        Message.COMMAND_TOGGLE_PLAYER_ON.getValue().replace("{1}", player.getName()));

            } else {

                Utils.logToConsole(Message.COMMAND_TOGGLE_PLAYER_ON.getValue().replace("{1}", player.getName()));
            }

        } else {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender,
                        Message.COMMAND_TOGGLE_PLAYER_OFF.getValue().replace("{1}", player.getName()));

            } else {

                Utils.logToConsole(Message.COMMAND_TOGGLE_PLAYER_OFF.getValue().replace("{1}", player.getName()));
            }
        }

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

        return "toggle player";
    }

    @Override
    public String getArgumentName() {

        return "player";
    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_TOGGLE_PLAYER_USAGE;
    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_TOGGLE_PLAYER_DESCRIPTION;
    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_TOGGLE_PLAYER;
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
