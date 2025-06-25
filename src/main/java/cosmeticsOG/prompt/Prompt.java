package cosmeticsOG.prompt;

import cosmeticsOG.editor.MetaState;
import org.bukkit.entity.Player;

public interface Prompt {

    /**
     * Prompts the player with a message
     *
     * @param player
     * @param message
     */
    public void prompt(Player player, String message);

    /**
     * Prompts the player with a MetaState usage
     *
     * @param player
     * @param state
     */
    public void prompt(Player player, MetaState state);

    /**
     * Checks to see if we can prompt this player
     * @param passes
     * @return
     */
    public boolean canPrompt(int passes);
}
