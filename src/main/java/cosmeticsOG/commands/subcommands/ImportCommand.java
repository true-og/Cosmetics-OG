package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.configuration.CustomConfig;
import cosmeticsOG.database.type.DatabaseType;
import cosmeticsOG.database.type.mysql.MySQLDatabase;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;

// Allows for the importing of a custom menu from a YAML file into the database.
public class ImportCommand extends Command {

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() < 1) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_IMPORT_USAGE.getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.logToConsole(Message.COMMAND_IMPORT_USAGE.getValue());
            }

            return false;
        }

        if (core.getDatabaseType().equals(DatabaseType.YAML)) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ADD_TYPE_ERROR.getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ADD_TYPE_ERROR.getValue());
            }

            return false;
        }

        String menuName = args.get(0);
        if (core.getDatabase().getMenus(false).containsKey(menuName)) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender, Message.COMMAND_ERROR_MENU_EXISTS.replace("{1}", menuName));

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_MENU_EXISTS.replace("{1}", menuName));
            }

            return false;
        }

        MySQLDatabase database = (MySQLDatabase) core.getDatabase();

        CustomConfig config = core.getResourceManager().getConfig(menuName);
        if (config == null) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender, Message.COMMAND_ERROR_UNKNOWN_MENU.replace("{1}", menuName));

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_UNKNOWN_MENU.replace("{1}", menuName));
            }

            return false;
        }

        database.importMenu(sender, config);

        if (sender.isPlayer()) {

            Utils.cosmeticsOGPlaceholderMessage(
                    (Player) sender, Message.COMMAND_IMPORT_SUCCESS.replace("{1}", menuName));

        } else {

            Utils.logToConsole(Message.COMMAND_IMPORT_SUCCESS.replace("{1}", menuName));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() == 1) {

            List<String> menus = new ArrayList<String>();
            for (String menu : core.getResourceManager().getMenus()) {

                menus.add(menu);
            }

            return menus;
        }

        return Arrays.asList("");
    }

    @Override
    public String getName() {

        return "import menu";
    }

    @Override
    public String getArgumentName() {

        return "import";
    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_IMPORT_USAGE;
    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_IMPORT_DESCRIPTION;
    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_IMPORT;
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
