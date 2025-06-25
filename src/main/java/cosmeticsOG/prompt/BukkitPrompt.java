package cosmeticsOG.prompt;

import cosmeticsOG.editor.MetaState;
import org.bukkit.entity.Player;

public class BukkitPrompt implements Prompt {

    @Override
    public void prompt(Player player, String message) {
        player.sendMessage(message);
    }

    @Override
    public void prompt(Player player, MetaState state) {
        prompt(player, state.getUsage());
    }

    @Override
    public boolean canPrompt(int passes) {
        return passes % 12 == 0;
    }
}
