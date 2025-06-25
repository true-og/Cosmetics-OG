package cosmeticsOG.commands;

import cosmeticsOG.permission.Permission;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents both a CommandSender and Player
 * @author MediusEcho
 *
 */
public class Sender {

    private final CommandSender commandSender;
    private final boolean isConsoleSender;

    private final Player playerSender;
    private final UUID playerID;

    public Sender(CommandSender sender) {
        boolean isPlayer = sender instanceof Player;

        playerSender = isPlayer ? (Player) sender : null;
        playerID = isPlayer ? playerSender.getUniqueId() : null;

        commandSender = sender;
        isConsoleSender = !isPlayer;
    }

    /**
     * Returns true if this Sender is a Player
     * @return
     */
    public boolean isPlayer() {
        return !isConsoleSender;
    }

    /**
     * Returns the Player executing this command
     * @return
     */
    public Player getPlayer() {
        return playerSender;
    }

    /**
     * Returns the UUID of the Player executing this command
     * @return
     */
    public UUID getPlayerID() {
        return playerID;
    }

    /**
     * Returns the CommandSender executing this command
     * @return
     */
    public CommandSender getCommandSender() {
        return commandSender;
    }

    /**
     * Returns true if this Sender has permission
     * @param permission
     * @return
     */
    public boolean hasPermission(String permission) {
        return isConsoleSender ? true : playerSender.hasPermission(permission);
    }

    /**
     * Returns true if this Sender has permission
     * @param permission
     * @return
     */
    public boolean hasPermission(Permission permission) {
        if (isConsoleSender) {
            return true;
        }

        if (playerSender.hasPermission(permission.getPermission())) {
            return true;
        }

        if (playerSender.hasPermission(Permission.COMMAND_ALL.getPermission())) {
            return true;
        }

        return false;
    }
}
