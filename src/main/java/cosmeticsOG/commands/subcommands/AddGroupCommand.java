package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.database.properties.Group;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;

// Handles the "add group" command to create a new group in the system.
public class AddGroupCommand extends Command {

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() < 2) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.cosmeticsOGPlaceholderMessage((Player) sender, getUsage().getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.logToConsole(getUsage().getValue());
            }

            return false;
        }

        String groupName = args.get(0);
        String defaultMenu = args.get(1);

        int weight = 0;

        if (args.size() >= 3) {

            weight = StringUtil.toInt(args.get(2), 0);
        }

        boolean found = false;
        List<Group> groups = core.getDatabase().getGroups(false);
        for (Group g : groups) {

            if (g.getName().equals(groupName)) {

                found = true;

                break;
            }
        }

        if (found) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender, Message.COMMAND_ERROR_GROUP_EXISTS.replace("{1}", groupName));

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_GROUP_EXISTS.replace("{1}", groupName));
            }

            return false;
        }

        core.getDatabase().addGroup(groupName, defaultMenu, weight);

        if (sender.isPlayer()) {

            Utils.cosmeticsOGPlaceholderMessage(
                    (Player) sender, Message.COMMAND_ADD_GROUP_SUCCESS.replace("{1}", groupName));

        } else {

            Utils.logToConsole(Message.COMMAND_ADD_GROUP_SUCCESS.replace("{1}", groupName));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        switch (args.size()) {
            case 1:
                return Arrays.asList("name");
            case 2:
                return new ArrayList<String>(core.getDatabase().getMenus(false).keySet());
            case 3:
                return Arrays.asList("weight");
        }

        return Arrays.asList("");
    }

    @Override
    public String getName() {

        return "add group";
    }

    @Override
    public String getArgumentName() {

        return "add";
    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_ADD_GROUP_USAGE;
    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_ADD_GROUP_DESCRIPTION;
    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_GROUP_ADD;
    }

    @Override
    public boolean hasWildcardPermission() {

        return true;
    }

    @Override
    public Permission getWildcardPermission() {

        return Permission.COMMAND_GROUP_ALL;
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
