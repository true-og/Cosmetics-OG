package cosmeticsOG.tasks;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.player.EntityState;
import cosmeticsOG.player.PlayerState;
import java.util.Collection;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuTask extends BukkitRunnable {

    private final CosmeticsOG core;
    private static int ticks = 0;

    public MenuTask(final CosmeticsOG core) {
        this.core = core;
    }

    @Override
    public void run() {
        Collection<EntityState> entityStates = core.getEntityStates();
        if (entityStates.size() > 0) {
            ticks++;
            for (EntityState entityState : entityStates) {
                // Skip entities since they're not using menus
                if (!(entityState instanceof PlayerState)) {
                    continue;
                }

                PlayerState playerState = (PlayerState) entityState;
                if (!playerState.hasMenuManager()) {
                    continue;
                }

                playerState.getMenuManager().onTick(ticks);
            }
        }
    }
}
