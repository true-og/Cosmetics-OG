package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.managers.CommandManager;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.util.MathUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Handles the help command that lists all available commands and their descriptions to the sender.
public class BukkitHelpCommand extends Command {

    protected final CosmeticsOG core;
    protected int pages;
    private Map<Integer, Component> commands;

    public BukkitHelpCommand(CosmeticsOG core, CommandManager commandManager) {

        this.core = core;
        commands = new HashMap<>();
        int commandIndex = 0;

        for (Entry<String, Command> cmds : commandManager.getCommands().entrySet()) {

            Command cmd = cmds.getValue();
            if (cmd != null) {

                String commandText = "<gray>> <dark_aqua>" + cmd.getUsage().getValue() + " <gray>"
                        + cmd.getDescription().getValue();
                Component component = MiniMessage.miniMessage().deserialize(commandText);

                // Store the component in the map
                commands.put(commandIndex++, component);

            }

        }

        pages = (int) Math.ceil((double) commandIndex / 9D);

    }

    /***
     * Reads and sends the help page to the sender.
     *
     * @param sender the sender to send the help to
     * @param page   the page number to read
     */
    protected void readPage(Sender sender, int page) {

        if (sender.isPlayer()) {

            Player player = sender.getPlayer();

            Utils.cosmeticsOGPlaceholderMessage(player,
                    "<white>> <light_purple>Cosmetics<red>-OG <yellow>v" + core.getPluginMeta().getVersion());
            Utils.cosmeticsOGPlaceholderMessage(player, "<gray>> " + Message.COMMAND_HELP_TIP.getValue());

        } else {

            Utils.logToConsole("&r&2Cosmetics&c-OG &ev" + core.getPluginMeta().getVersion());
            Utils.logToConsole("&7 " + Message.COMMAND_HELP_TIP.getValue());

        }

        for (Entry<Integer, Component> entry : commands.entrySet()) {

            Component component = entry.getValue();
            if (sender.isPlayer()) {

                sender.getPlayer().sendMessage(component);

            } else {

                Utils.logToConsole(MiniMessage.miniMessage().serialize(component));

            }

        }

    }

    /**
     * Prints out all commands to the console.
     *
     * @param sender the CommandSender
     */
    protected void readPage(CommandSender sender) {

        sender.sendMessage(MiniMessage.miniMessage()
                .deserialize("<white>> <gold>ParticleHats v" + core.getPluginMeta().getVersion()));
        for (Entry<Integer, Component> cmd : commands.entrySet()) {

            sender.sendMessage(cmd.getValue());

        }

    }

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (!sender.hasPermission(getPermission())) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage(sender.getPlayer(), Message.COMMAND_ERROR_NO_PERMISSION.getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_NO_PERMISSION.getValue());

            }

            return false;

        }

        int targetPage = 0;
        if (args.size() >= 1) {

            targetPage = MathUtil.valueOf(args.get(0));

        }

        if (sender.isPlayer()) {

            readPage(sender, targetPage);

        } else {

            readPage(sender.getCommandSender());

        }

        return true;

    }

    @Override
    public String getName() {

        return "help";

    }

    @Override
    public String getArgumentName() {

        return "help";

    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_HELP_USAGE;

    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_HELP_DESCRIPTION;

    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_HELP;

    }

    @Override
    public boolean showInHelp() {

        return false;

    }

    @Override
    public boolean isPlayerOnly() {

        return false;

    }

}
