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

// Sets a hat on a player.
public class SetCommand extends Command {

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() < 2 || args.size() > 5) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_SET_USAGE.getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.logToConsole(Message.COMMAND_SET_USAGE.getValue());
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

        boolean permanent = true;
        if (args.size() >= 3) {

            permanent = Boolean.valueOf(args.get(2));
        }

        boolean tellPlayer = true;
        if (args.size() >= 4) {

            tellPlayer = Boolean.valueOf(args.get(3));
        }

        int demoTime = 200;
        if (args.size() >= 5) {

            try {

                demoTime = Integer.parseInt(args.get(4));

            } catch (NumberFormatException ignored) {
            }
        }

        String hatLabel = args.get(1);

        // Check to see if this player is already wearing a hat with this label.
        PlayerState playerState = core.getPlayerState(player.getPlayer());
        for (Hat h : playerState.getActiveHats()) {

            if (h.getLabel().equalsIgnoreCase(hatLabel)) {

                if (sender.isPlayer()) {

                    Utils.cosmeticsOGPlaceholderMessage(
                            (Player) sender,
                            Message.COMMAND_SET_ALREADY_SET.getValue().replace("{1}", player.getName()));

                } else {

                    Utils.logToConsole(
                            Message.COMMAND_SET_ALREADY_SET.getValue().replace("{1}", player.getName()));
                }

                return false;
            }
        }

        Database database = core.getDatabase();
        Hat hat = database.getHatFromLabel(hatLabel);
        if (hat == null) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender,
                        Message.COMMAND_SET_LABEL_ERROR.getValue().replace("{1}", hatLabel));

            } else {

                Utils.logToConsole(Message.COMMAND_SET_LABEL_ERROR.getValue().replace("{1}", hatLabel));
            }

            return false;
        }

        hat.setPermanent(permanent);
        if (!permanent) {

            hat.setCanBeSaved(false);
            hat.setDemoDuration(demoTime);
        }

        if (core.getParticleManager().equipHat(player, hat, false)) {

            if (tellPlayer) {

                player.sendMessage(Message.COMMAND_SET_SUCCESS.getValue().replace("{1}", hat.getDisplayName()));
            }
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
                return core.getDatabase().getLabels(false);
            }
            case 3:
            case 4: {
                return Arrays.asList("true", "false");
            }
            case 5: {
                return Collections.singletonList("duration");
            }
        }
        return Collections.singletonList("");
    }

    @Override
    public String getName() {

        return "set";
    }

    @Override
    public String getArgumentName() {

        return "set";
    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_SET_USAGE;
    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_SET_DESCRIPTION;
    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_SET;
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
