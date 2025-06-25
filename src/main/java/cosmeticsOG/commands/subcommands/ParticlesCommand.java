package cosmeticsOG.commands.subcommands;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.commands.Command;
import cosmeticsOG.commands.Sender;
import cosmeticsOG.locale.Message;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.EquippedParticlesMenu;
import cosmeticsOG.ui.StaticMenuManager;
import java.util.ArrayList;
import org.bukkit.entity.Player;

// Allows players to open a menu for managing their equipped particle effects.
public class ParticlesCommand extends Command {

    @Override
    public boolean execute(CosmeticsOG core, Sender sender, String label, ArrayList<String> args) {

        if (sender instanceof Player) {

            PlayerState playerState = core.getPlayerState(sender.getPlayer());
            if (playerState.hasEditorOpen()) {

                Utils.cosmeticsOGPlaceholderMessage((Player) sender, Message.COMMAND_ERROR_ALREADY_EDITING.getValue());

                return false;
            }

            StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
            EquippedParticlesMenu particlesMenu =
                    new EquippedParticlesMenu(core, staticManager, sender.getPlayer(), false);

            staticManager.addMenu(particlesMenu);
            particlesMenu.open();

            return true;

        } else {

            return false;
        }
    }

    @Override
    public String getName() {

        return "particles";
    }

    @Override
    public String getArgumentName() {

        return "particles";
    }

    @Override
    public Message getUsage() {

        return Message.COMMAND_PARTICLE_USAGE;
    }

    @Override
    public Message getDescription() {

        return Message.COMMAND_PARTICLE_DESCRIPTION;
    }

    @Override
    public Permission getPermission() {

        return Permission.COMMAND_PARTICLES;
    }

    @Override
    public boolean showInHelp() {

        return true;
    }

    @Override
    public boolean isPlayerOnly() {

        return true;
    }
}
