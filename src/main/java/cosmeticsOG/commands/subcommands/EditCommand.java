package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.database.Database;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.editor.purchase.PurchaseMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.MenuInventory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;

// Allows for GUI-based editing of Menus in the database.
public class EditCommand extends Command {

    private final CosmeticsOG core;

    public EditCommand(final CosmeticsOG core) {

        this.core = core;

    }

    @Override
    public List<String> tabComplete(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() == 1) {

            List<String> menus = new ArrayList<String>(core.getDatabase().getMenus(false).keySet());
            List<String> result = new ArrayList<String>();

            menus.add("purchase");

            for (String menu : menus) {

                if (hasPermission(sender, menu)) {

                    result.add(menu);

                }

            }

            return result;

        }

        return Arrays.asList("");

    }

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (args.size() < 1) {

            if (sender.isPlayer()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_EDIT_USAGE.getValue());

            } else {

                Utils.logToConsole(Message.COMMAND_ERROR_ARGUMENTS.getValue());
                Utils.logToConsole(Message.COMMAND_EDIT_USAGE.getValue());

            }

            return false;

        }

        PlayerState playerState = core.getPlayerState(sender.getPlayer());
        Database database = core.getDatabase();
        if (playerState.hasEditorOpen()) {

            Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ALREADY_EDITING.getValue());

            return false;

        }

        String menuName = (args.get(0).contains(".") ? args.get(0).split("\\.")[0] : args.get(0));
        if (menuName.equalsIgnoreCase("purchase")) {

            MenuInventory inventory = database.getPurchaseMenu(playerState);
            if (inventory != null) {

                PurchaseMenuManager purchaseManager = core.getMenuManagerFactory().getPurchaseMenuManager(playerState);

                purchaseManager.setEditingMenu(inventory);
                purchaseManager.open();

            }

            return false;

        }

        if (!database.menuExists(menuName)) {

            Utils.cosmeticsOGPlaceholderMessage((Player) sender,
                    Message.COMMAND_ERROR_UNKNOWN_MENU.replace("{1}", menuName));

            return false;

        }

        MenuInventory inventory = database.loadInventory(menuName, playerState);
        if (inventory == null) {

            return false;

        }

        EditorMenuManager editorManager = core.getMenuManagerFactory().getEditorMenuManager(playerState);

        editorManager.setEditingMenu(inventory);
        editorManager.open();

        return true;

    }

    @Override
    public String getName() {

        return "edit menu";

    }

    @Override
    public String getArgumentName() {

        return "edit";

    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_EDIT_USAGE;

    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_EDIT_DESCRIPTION;

    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_EDIT;

    }

    @Override
    public boolean hasWildcardPermission() {

        return true;

    }

    @Override
    public Permission getWildcardPermission() {

        return Permission.COMMAND_EDIT_ALL;

    }

    @Override
    public boolean showInHelp() {

        return true;

    }

    @Override
    public boolean isPlayerOnly() {

        return true;

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

        // Check for individual menu permissions.
        List<String> menus = new ArrayList<String>(core.getDatabase().getMenus(false).keySet());
        menus.add("purchase");

        for (String menu : menus) {

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

}
