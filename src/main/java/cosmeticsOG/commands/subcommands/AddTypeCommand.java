package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.database.type.DatabaseType;
import cosmeticsOG.database.type.mysql.MySQLDatabase;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.entity.Player;

// Allows for the addition of images to the database.
public class AddTypeCommand extends Command {

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

        if (core.getDatabaseType().equals(DatabaseType.YAML)) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ADD_TYPE_ERROR.getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ADD_TYPE_ERROR.getValue());
            }

            return false;
        }

        String imageName = args.get(0);
        if (core.getDatabase().getImages(false).containsKey(imageName)) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender, Message.COMMAND_ERROR_TYPE_EXISTS.replace("{1}", imageName));

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_TYPE_EXISTS.replace("{1}", imageName));
            }

            return false;
        }

        MySQLDatabase database = (MySQLDatabase) core.getDatabase();
        BufferedImage image = core.getResourceManager().getImages().get(imageName);
        if (database.insertImage(imageName, image)) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender, Message.COMMAND_ADD_TYPE_SUCCESS.replace("{1}", imageName));

            } else {

                Utils.logToConsole(Message.COMMAND_ADD_TYPE_SUCCESS.replace("{1}", imageName));
            }

            return true;
        }

        return false;
    }

    @Override
    public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() == 1) {

            List<String> images = new ArrayList<String>();
            Map<String, BufferedImage> imageCache = core.getDatabase().getImages(false);

            for (Entry<String, BufferedImage> entry :
                    core.getResourceManager().getImages().entrySet()) {

                if (!imageCache.containsKey(entry.getKey())) {

                    images.add(entry.getKey());
                }
            }

            return images;
        }

        return Arrays.asList("");
    }

    @Override
    public String getName() {

        return "add type";
    }

    @Override
    public String getArgumentName() {

        return "add";
    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_ADD_TYPE_USAGE;
    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_ADD_TYPE_DESCRIPTION;
    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_TYPE_ADD;
    }

    @Override
    public boolean hasWildcardPermission() {

        return true;
    }

    @Override
    public Permission getWildcardPermission() {

        return Permission.COMMAND_TYPE_ALL;
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
