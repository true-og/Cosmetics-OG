package cosmeticsOG.listeners;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    private final CosmeticsOG plugin;

    public PlayerListener(CosmeticsOG plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        togglePlayerHats(event.getEntity(), false);
    }

    @EventHandler
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        togglePlayerHats(event.getPlayer(), true);
    }

    private void togglePlayerHats(Player player, boolean state) {
        if (!plugin.hasEntityState(player)) {
            return;
        }

        PlayerState playerState = plugin.getPlayerState(player);
        for (Hat hat : playerState.getActiveHats()) {
            if (hat.isHidden() || hat.isVanished()) {
                continue;
            }
            hat.setIsDisplaying(state, player);
        }
    }
}
