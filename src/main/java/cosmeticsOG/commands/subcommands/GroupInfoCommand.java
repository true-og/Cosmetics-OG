package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.database.properties.Group;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

// Displays information to the user about the Groups in the database.
public class GroupInfoCommand extends Command {

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        List<Group> groups = core.getDatabase().getGroups(true);
        String infoTemplate = Message.COMMAND_GROUP_INFO.getValue();

        if (sender.isPlayer()) {

            Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_GROUP_INFO_TIP.getValue());

        } else {

            Utils.logToConsole(Message.COMMAND_GROUP_INFO_TIP.getValue());

        }

        for (Group g : groups) {

            String info = infoTemplate.replace("{1}", g.getName()).replace("{2}", g.getDefaultMenu()).replace("{3}",
                    Integer.toString(g.getWeight()));

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, ("&f> " + info));

            } else {

                Utils.logToConsole("> " + info);

            }

        }

        return false;

    }

    @Override
    public String getName() {

        return "group info";

    }

    @Override
    public String getArgumentName() {

        return "info";

    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_GROUP_INFO_USAGE;

    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_GROUP_INFO_DESCRIPTION;

    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_GROUP_INFO;

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
