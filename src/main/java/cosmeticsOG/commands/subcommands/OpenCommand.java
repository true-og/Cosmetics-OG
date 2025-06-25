package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.database.Database;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.AbstractMenu;
import cosmeticsOG.ui.MenuInventory;
import cosmeticsOG.ui.StaticMenu;
import cosmeticsOG.ui.StaticMenuManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;

// Allows a player to open a specified menu.
public class OpenCommand extends Command {

    private final CosmeticsOG core;
    private final Database database;

    private OpenPlayerCommand openPlayerCommand;

    public OpenCommand(final CosmeticsOG core) {

        this.core = core;
        database = core.getDatabase();

        openPlayerCommand = new OpenPlayerCommand(this);
        register(openPlayerCommand);
    }

    @Override
    public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() == 1) {

            Set<String> menus = database.getMenus(false).keySet();
            List<String> result = new ArrayList<String>();
            for (String menu : menus) {

                if (hasPermission(sender, menu)) {

                    result.add(menu);
                }
            }

            return result;

        } else if (args.size() == 2) {

            return openPlayerCommand.onTabComplete(core, sender, label, args);
        }

        return Arrays.asList("");
    }

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() == 0) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_OPEN_USAGE.getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.logToConsole(Message.COMMAND_OPEN_USAGE.getValue());
            }

            return false;
        }

        if (args.size() == 1) {

            if (!sender.isPlayer()) {

                Utils.logToConsole(Message.COMMAND_ERROR_PLAYER_ONLY.getValue());

                return false;

            } else {

                PlayerState playerState = core.getPlayerState(sender.getPlayer());
                if (playerState.hasEditorOpen()) {

                    Utils.cosmeticsOGPlaceholderMessage(
                            (Player) sender, Message.COMMAND_ERROR_ALREADY_EDITING.getValue());

                    return false;
                }

                AbstractMenu menu = getRequestedMenu(playerState, args.get(0), sender, sender.getPlayer());
                if (menu == null) {

                    return false;
                }

                StaticMenuManager staticManager = (StaticMenuManager) playerState.getMenuManager();
                staticManager.addMenu(menu);

                menu.open();

                return true;
            }

        } else {

            return openPlayerCommand.onCommand(core, sender, label, args);
        }
    }

    @Override
    public String getName() {

        return "open menu";
    }

    @Override
    public String getArgumentName() {

        return "open";
    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_OPEN_USAGE;
    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_OPEN_DESCRIPTION;
    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_OPEN;
    }

    @Override
    public boolean hasWildcardPermission() {

        return true;
    }

    @Override
    public Permission getWildcardPermission() {

        return Permission.COMMAND_OPEN_ALL;
    }

    @Override
    public boolean showInHelp() {

        return true;
    }

    @Override
    public boolean isPlayerOnly() {

        return false;
    }

    @Override
    public boolean hasPermission(Sender sender) {

        if (!sender.isPlayer()) {

            return true;
        }

        // /h wild card check.
        if (Permission.COMMAND_ALL.hasPermission(sender)) {

            return true;
        }

        // Specific command wild card check.
        if (hasWildcardPermission()) {

            if (getWildcardPermission().hasPermission(sender)) {

                return true;
            }
        }

        // Check for individual menu permissions
        for (String menu : core.getDatabase().getMenus(false).keySet()) {

            if (sender.hasPermission(getPermission().append(menu))) {

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Sender sender, String arg) {

        if (!sender.isPlayer()) {

            return true;
        }

        // /h wild card check.
        if (Permission.COMMAND_ALL.hasPermission(sender)) {

            return true;
        }

        // Specific command wild card check.
        if (hasWildcardPermission()) {

            if (getWildcardPermission().hasPermission(sender)) {

                return true;
            }
        }

        if (arg != null && !arg.equals("")) {

            if (sender.hasPermission(getPermission().append(arg))) {

                return true;
            }
        }

        return false;
    }

    public AbstractMenu getRequestedMenu(
            PlayerState playerState, String requestedMenuName, Sender sender, Player player) {

        // Grab the name without any extensions.
        String menuName = (requestedMenuName.contains(".") ? requestedMenuName.split("\\.")[0] : requestedMenuName);

        if (menuName.equals("purchase")) {

            Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_OPEN_ERROR.replace("{1}", menuName));

            return null;
        }

        StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
        AbstractMenu menu = staticManager.getMenuFromCache(menuName);
        if (menu == null) {

            CosmeticsOG.debug("cache didnt exist, loading menu " + menuName);
            MenuInventory menuInventory = core.getDatabase().loadInventory(menuName, playerState);

            if (menuInventory == null) {

                Utils.cosmeticsOGPlaceholderMessage(
                        (Player) sender, Message.COMMAND_ERROR_UNKNOWN_MENU.replace("{1}", menuName));

                return null;
            }

            return new StaticMenu(core, staticManager, player, menuInventory);
        }

        return menu;
    }
}
