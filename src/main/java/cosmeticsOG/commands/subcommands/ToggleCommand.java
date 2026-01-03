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
import org.bukkit.entity.Player;

// Allows the command runner to toggle their cosmetics on or off.
public class ToggleCommand extends Command {

    private final TogglePlayerCommand togglePlayerCommand;

    public ToggleCommand() {

        togglePlayerCommand = new TogglePlayerCommand();
        register(togglePlayerCommand);

    }

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() < 1) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.cosmeticsOGPlaceholderMessage((Player) sender, getUsage().getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.logToConsole(getUsage().getValue());

            }

            return false;

        }

        if (args.size() > 1) {

            return togglePlayerCommand.onCommand(core, sender, label, args);

        }

        if (sender.isPlayer()) {

            boolean toggleStatus = StringUtil.getToggleValue(args.get(0));
            core.getPlayerState(sender.getPlayer()).toggleHats(!toggleStatus);
            if (toggleStatus) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_TOGGLE_ON.getValue());

            } else {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_TOGGLE_OFF.getValue());

            }

        }

        return true;

    }

    @Override
    public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() == 2) {

            return togglePlayerCommand.onTabComplete(core, sender, label, args);

        }

        return Arrays.asList("on", "off");

    }

    @Override
    public boolean onCommand(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() == 2) {

            return togglePlayerCommand.onCommand(core, sender, label, args);

        }

        return super.onCommand(core, sender, label, args);

    }

    @Override
    public String getName() {

        return "Toggle";

    }

    @Override
    public String getArgumentName() {

        return "toggle";

    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_TOGGLE_USAGE;

    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_TOGGLE_DESCRIPTION;

    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_TOGGLE;

    }

    @Override
    public boolean showInHelp() {

        return true;

    }

    @Override
    public boolean isPlayerOnly() {

        return true;

    }

}
