package cosmeticsOG.tasks;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.editor.MetaState;
import cosmeticsOG.player.EntityState;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.prompt.Prompt;
import java.util.Collection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PromptTask extends BukkitRunnable {

    private final CosmeticsOG core;
    private int passes = 0;

    public PromptTask(final CosmeticsOG core) {
        this.core = core;
    }

    @Override
    public void run() {
        Collection<EntityState> entityStates = core.getEntityStates();
        if (entityStates.size() > 0) {
            passes++;
            for (EntityState entityState : entityStates) {
                Entity entity = entityState.getOwner();
                if (!(entity instanceof Player)) {
                    continue;
                }

                if (!(entityState instanceof PlayerState)) {
                    continue;
                }

                PlayerState playerState = (PlayerState) entityState;
                MetaState metaState = playerState.getMetaState();

                if (metaState == MetaState.NONE) {
                    continue;
                }

                int time = playerState.getMetaStateTime();
                if (time <= 0) {
                    if (playerState.hasMenuManager()) {
                        ((EditorMenuManager) playerState.getMenuManager()).reopen();
                    } else {
                        playerState.setMetaState(MetaState.NONE);
                    }
                    continue;
                }

                Prompt prompt = core.getPrompt();
                if (prompt.canPrompt(passes)) {
                    prompt.prompt((Player) entity, metaState);
                }
            }
        }
    }
}
