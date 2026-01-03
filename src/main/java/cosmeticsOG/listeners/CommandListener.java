package cosmeticsOG.listeners;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.editor.MetaState;
import cosmeticsOG.locale.Message;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.AbstractMenu;
import cosmeticsOG.ui.MenuInventory;
import cosmeticsOG.ui.StaticMenu;
import cosmeticsOG.ui.StaticMenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    private final CosmeticsOG core;

    public CommandListener(final CosmeticsOG core) {

        this.core = core;
        core.getServer().getPluginManager().registerEvents(this, core);

    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        if (event.getMessage().contains(" ")) {

            return;

        }

        Player player = event.getPlayer();
        PlayerState playerState = core.getPlayerState(player);

        // Cancel any commands if this player is editing a meta property
        if (SettingsManager.EDITOR_RESTRICT_COMMANDS.getBoolean() && playerState.hasEditorOpen()) {

            MetaState metaState = playerState.getMetaState();
            if (metaState != MetaState.NONE) {

                player.sendMessage(Message.COMMAND_ERROR_ALREADY_EDITING.getValue());
                event.setCancelled(true);

            }

        }

        // Check for menu aliases
        else {

            String cmd = event.getMessage().replaceFirst("/", "");
            MenuInventory inventory = core.getDatabase().getInventoryFromAlias(cmd, playerState);

            if (inventory != null) {

                event.setCancelled(true);

                StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
                AbstractMenu menu = new StaticMenu(core, staticManager, player, inventory);

                staticManager.addMenu(menu);
                menu.open();

            }

        }

    }

}
