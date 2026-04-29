package cosmeticsOG.commands.subcommands;

import net.trueog.utilitiesog.UtilitiesOG;
import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import java.util.ArrayList;
import org.bukkit.entity.Player;

// Allows for the plugin to be reloaded.
public class ReloadCommand extends Command {

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        core.onReload();

        if (sender.isPlayer()) {

            CosmeticsOG.chatMessage((Player) sender, Message.COMMAND_RELOAD_SUCCESS.getValue());

        } else {

            UtilitiesOG.logToConsole(CosmeticsOG.getPrefix(), Message.COMMAND_RELOAD_SUCCESS.getValue());

        }

        return true;

    }

    @Override
    public String getName() {

        return "reload";

    }

    @Override
    public String getArgumentName() {

        return "reload";

    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_RELOAD_USAGE;

    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_RELOAD_DESCRIPTION;

    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_RELOAD;

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
