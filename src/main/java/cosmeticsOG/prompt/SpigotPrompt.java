package cosmeticsOG.prompt;

import cosmeticsOG.Utils;
import cosmeticsOG.editor.MetaState;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class SpigotPrompt extends BukkitPrompt {

    private boolean success = true;

    @Override
    public void prompt(Player player, String message) {

        try {

            Component component = Utils.legacySerializerAnyCase(message);

            // Send an action bar message using Adventure API.
            player.sendActionBar(component);

        } catch (NoSuchMethodError error) {

            super.prompt(player, message);

            success = false;

        }

    }

    @Override
    public void prompt(Player player, MetaState state) {

        prompt(player, state.getDescription());

    }

    @Override
    public boolean canPrompt(int passes) {

        return success ? passes % 1 == 0 : super.canPrompt(passes);

    }

}
