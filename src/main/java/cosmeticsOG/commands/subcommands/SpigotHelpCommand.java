package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.managers.CommandManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

// Provides a help interface for displaying available commands.
public class SpigotHelpCommand extends BukkitHelpCommand {

    private Map<Command, TextComponent> commands;

    public SpigotHelpCommand(CosmeticsOG core, CommandManager commandManager) {

        super(core, commandManager);

        commands = new HashMap<>();

        for (Entry<String, Command> cmds : commandManager.getCommands().entrySet()) {

            Command command = cmds.getValue();
            if (command != null) {

                String commandText = "&3Command: &f" + Utils.stripColors(command.getName()) + "\n" + "&3Description: &8"
                        + Utils.stripColors(command.getDescription().getValue()) + "\n" + "&3Usage: &8"
                        + Utils.stripColors(command.getUsage().getValue()) + "\n" + "&3Permission: &8"
                        + Utils.stripColors(command.getPermission().getPermission());

                TextComponent hoverText = Utils.legacySerializerAnyCase(commandText);
                TextComponent commandComponent = Utils.legacySerializerAnyCase("&7> &3" + command.getUsage().getValue())
                        .hoverEvent(HoverEvent.showText(hoverText))
                        .clickEvent(ClickEvent.suggestCommand(command.getUsage().getValue()));

                commands.put(command, commandComponent);

            }

        }

    }

    @Override
    protected void readPage(Sender sender, int page) {

        if (sender.isPlayer()) {

            Player player = sender.getPlayer();
            Utils.cosmeticsOGPlaceholderMessage(player, "&f> &6ParticleHats v" + core.getPluginMeta().getVersion());
            Utils.cosmeticsOGPlaceholderMessage(player, "&7> " + Message.COMMAND_HELP_TIP.getValue());

        } else {

            Utils.logToConsole("&f> &6ParticleHats v" + core.getPluginMeta().getVersion());
            Utils.logToConsole("&7> " + Message.COMMAND_HELP_TIP.getValue());

        }

        for (Entry<Command, TextComponent> entry : commands.entrySet()) {

            if (entry.getKey().hasPermission(sender)) {

                TextComponent component = entry.getValue();

                if (sender.isPlayer()) {

                    sender.getPlayer().sendMessage(component);

                } else {

                    Utils.logToConsole(component.content());

                }

            }

        }

    }

}
