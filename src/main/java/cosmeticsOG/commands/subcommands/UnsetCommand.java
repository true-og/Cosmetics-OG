package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.database.Database;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.player.PlayerState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

// Allows for the removal of a cosmetic from a specified player.
public class UnsetCommand extends Command {

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() < 2 || args.size() > 3) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_UNSET_USAGE.getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.logToConsole(Message.COMMAND_UNSET_USAGE.getValue());
            }

            return false;
        }

        Player player = getPlayer(sender, args.get(0));
        if (player == null) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender,
                        Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(0)));

            } else {

                Utils.logToConsole(
                        Message.COMMAND_ERROR_UNKNOWN_PLAYER.getValue().replace("{1}", args.get(0)));
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

        boolean tellPlayer = true;
        if (args.size() >= 3) {

            tellPlayer = Boolean.valueOf(args.get(2));
        }

        String hatLabel = args.get(1);

        // Check to see if this player is wearing a hat with this label.
        PlayerState playerState = core.getPlayerState(player.getPlayer());

        if (!playerState.getActiveHats().stream().anyMatch(hat -> hat.getLabel().equalsIgnoreCase(hatLabel))) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender,
                        Message.COMMAND_UNSET_NOT_WEARING.getValue().replace("{1}", player.getName()));

            } else {

                Utils.logToConsole(Message.COMMAND_UNSET_NOT_WEARING.getValue().replace("{1}", player.getName()));
            }

            return false;
        }

        Database database = core.getDatabase();
        Hat hat = database.getHatFromLabel(hatLabel);
        if (hat == null) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender,
                        Message.COMMAND_UNSET_LABEL_ERROR.getValue().replace("{1}", hatLabel));

            } else {

                Utils.logToConsole(Message.COMMAND_UNSET_LABEL_ERROR.getValue().replace("{1}", hatLabel));
            }

            return false;
        }

        core.getPlayerState(player).removeHat(hat);
        if (tellPlayer) {

            player.sendMessage(Message.COMMAND_UNSET_SUCCESS.getValue().replace("{1}", hat.getDisplayName()));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        switch (args.size()) {
            case 1: {
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
            case 2: {
                List<String> labels = new ArrayList<String>();
                for (Hat h : core.getPlayerState(sender.getPlayer()).getActiveHats()) {

                    labels.add(h.getLabel());
                }

                return labels;
            }
            case 3: {
                return Arrays.asList("true", "false");
            }
        }

        return Collections.singletonList("");
    }

    @Override
    public String getName() {

        return "unset";
    }

    @Override
    public String getArgumentName() {

        return "unset";
    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_UNSET_USAGE;
    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_UNSET_DESCRIPTION;
    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_UNSET;
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
