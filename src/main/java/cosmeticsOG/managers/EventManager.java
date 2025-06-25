package cosmeticsOG.managers;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.listeners.ChatListener;
import cosmeticsOG.listeners.CommandListener;
import cosmeticsOG.listeners.ConnectionListener;
import cosmeticsOG.listeners.EntityListener;
import cosmeticsOG.listeners.InteractListener;
import cosmeticsOG.listeners.InventoryListener;
import cosmeticsOG.listeners.PlayerListener;

@SuppressWarnings("unused")
public class EventManager {

    private final CosmeticsOG core;

    // Events
    private final InventoryListener inventoryListener;
    private final ChatListener chatListener;
    private final EntityListener entityListener;
    private final ConnectionListener connectionListener;
    private final InteractListener interactListener;
    private final CommandListener commandListener;
    private final PlayerListener playerListener;

    public EventManager(final CosmeticsOG core) {
        this.core = core;

        inventoryListener = new InventoryListener(core);
        chatListener = new ChatListener(core);
        entityListener = new EntityListener(core);
        connectionListener = new ConnectionListener(core);
        interactListener = new InteractListener(core);
        commandListener = new CommandListener(core);
        playerListener = new PlayerListener(core);
    }

    public void onReload() {
        entityListener.onReload();
    }
}
