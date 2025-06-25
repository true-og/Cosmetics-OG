package cosmeticsOG.listeners;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.database.Database;
import cosmeticsOG.database.properties.Group;
import cosmeticsOG.locale.Message;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.AbstractMenu;
import cosmeticsOG.ui.MenuInventory;
import cosmeticsOG.ui.StaticMenu;
import cosmeticsOG.ui.StaticMenuManager;
import cosmeticsOG.util.ResourceUtil;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    private final CosmeticsOG core;

    public InteractListener(final CosmeticsOG core) {
        this.core = core;
        core.getServer().getPluginManager().registerEvents(this, core);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!SettingsManager.MENU_OPEN_WITH_ITEM.getBoolean()) {
            return;
        }

        ItemStack item = event.getItem();
        if (item != null) {
            Material checkAgainst = SettingsManager.MENU_OPEN_WITH_ITEM_MATERIAL.getMaterial();
            if (item.getType().equals(checkAgainst)) {
                short durability = (short) SettingsManager.MENU_OPEN_WITH_ITEM_DAMAGE.getInt();
                if (CosmeticsOG.serverVersion < 13 && item.getDurability() != durability) {
                    return;
                }

                Database database = core.getDatabase();
                Player player = event.getPlayer();
                String menuName = "";

                if (SettingsManager.MENU_OPEN_WITH_GROUP.getBoolean()) {
                    List<Group> groups = database.getGroups(true);
                    for (Group g : groups) {
                        if (player.hasPermission(Permission.GROUP.append(g.getName()))) {
                            menuName = g.getDefaultMenu();
                        }
                    }
                } else {
                    menuName = SettingsManager.MENU_OPEN_DEFAULT_MENU.getString();
                }

                if (menuName.equals("")) {
                    return;
                }

                menuName = ResourceUtil.removeExtension(menuName);
                PlayerState playerState = core.getPlayerState(player);
                MenuInventory inventory = database.loadInventory(menuName, playerState);

                if (inventory == null) {
                    player.sendMessage(
                            Message.COMMAND_ERROR_UNKNOWN_MENU.getValue().replace("{1}", menuName));
                    return;
                }

                StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
                AbstractMenu menu = new StaticMenu(core, staticManager, player, inventory);

                staticManager.addMenu(menu);
                menu.open();
            }
        }
    }
}
