package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.database.properties.Group;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;

// Allows for the deletion of Groups from the database.
public class DeleteGroupCommand extends Command {

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

        String groupName = args.get(0);

        boolean found = false;
        List<Group> groups = core.getDatabase().getGroups(false);

        for (Group g : groups) {

            if (g.getName().equals(groupName)) {

                found = true;
                break;

            }

        }

        if (!found) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender,
                        Message.COMMAND_ERROR_UNKNOWN_GROUP.replace("{1}", groupName));

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_UNKNOWN_GROUP.replace("{1}", groupName));

            }

            return false;

        }

        core.getDatabase().deleteGroup(groupName);

        if (sender.isPlayer()) {

            Utils.cosmeticsOGPlaceholderMessage((Player) sender,
                    Message.COMMAND_REMOVE_GROUP_SUCCESS.replace("{1}", groupName));

        } else {

            Utils.logToConsole(Message.COMMAND_REMOVE_GROUP_SUCCESS.replace("{1}", groupName));

        }

        return true;

    }

    @Override
    public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() == 1) {

            List<String> groups = new ArrayList<String>();
            for (Group g : core.getDatabase().getGroups(false)) {

                groups.add(g.getName());

            }

            return groups;

        }

        return Arrays.asList("");

    }

    @Override
    public String getName() {

        return "remove group";

    }

    @Override
    public String getArgumentName() {

        return "remove";

    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_REMOVE_GROUP_USAGE;

    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_REMOVE_GROUP_DESCRIPTION;

    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_GROUP_REMOVE;

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
