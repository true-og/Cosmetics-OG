package cosmeticsOG.commands.subcommands;

import net.trueog.utilitiesog.UtilitiesOG;
import cosmeticsOG.CosmeticsOG;
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

                String commandText = "&3Command: &f" + UtilitiesOG.stripFormatting(command.getName()) + "\n"
                        + "&3Description: &8" + UtilitiesOG.stripFormatting(command.getDescription().getValue()) + "\n"
                        + "&3Usage: &8" + UtilitiesOG.stripFormatting(command.getUsage().getValue()) + "\n"
                        + "&3Permission: &8" + UtilitiesOG.stripFormatting(command.getPermission().getPermission());

                TextComponent hoverText = UtilitiesOG.trueogColorize(commandText);
                TextComponent commandComponent = UtilitiesOG.trueogColorize("&7> &3" + command.getUsage().getValue())
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
            CosmeticsOG.chatMessage(player, "&f> &6ParticleHats v" + core.getPluginMeta().getVersion());
            CosmeticsOG.chatMessage(player, "&7> " + Message.COMMAND_HELP_TIP.getValue());

        } else {

            UtilitiesOG.logToConsole(CosmeticsOG.getPrefix(),
                    "&f> &6ParticleHats v" + core.getPluginMeta().getVersion());
            UtilitiesOG.logToConsole(CosmeticsOG.getPrefix(), "&7> " + Message.COMMAND_HELP_TIP.getValue());

        }

        for (Entry<Command, TextComponent> entry : commands.entrySet()) {

            if (entry.getKey().hasPermission(sender)) {

                TextComponent component = entry.getValue();

                if (sender.isPlayer()) {

                    sender.getPlayer().sendMessage(component);

                } else {

                    UtilitiesOG.logToConsole(CosmeticsOG.getPrefix(), component.content());

                }

            }

        }

    }

}
