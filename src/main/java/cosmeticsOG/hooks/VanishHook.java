package cosmeticsOG.hooks;

import org.bukkit.entity.Player;

public interface VanishHook {

    /**
     * Checks to see if this player is currently vanished
     * 
     * @param player
     * @return
     */
    boolean isVanished(Player player);

    /**
     * Unregisters this hook
     */
    void unregister();

}