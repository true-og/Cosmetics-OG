package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import java.util.ArrayList;

// Executes basic debugging functionality.
public class DebugCommand extends Command {

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        return false;
    }

    @Override
    public String getName() {

        return "debug";
    }

    @Override
    public String getArgumentName() {

        return "debug";
    }

    @Override
    public Message getUsage() {

        return Message.UNKNOWN;
    }

    @Override
    public Message getDescription() {

        return Message.UNKNOWN;
    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_ALL;
    }

    @Override
    public boolean showInHelp() {

        return false;
    }

    @Override
    public boolean isPlayerOnly() {

        return true;
    }
}
