package cosmeticsOG.tasks;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.player.EntityState;
import cosmeticsOG.player.PlayerState.AFKState;
import cosmeticsOG.player.PlayerState.PVPState;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityTask extends BukkitRunnable {

    private final CosmeticsOG core;

    private int afkCooldown = SettingsManager.AFK_COOLDOWN.getInt() * 1000;
    private int pvpCooldown = SettingsManager.COMBAT_COOLDOWN.getInt() * 1000;

    private List<String> disabledWorlds;

    public EntityTask(CosmeticsOG core) {

        this.core = core;
        disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();

    }

    @Override
    public void run() {

        Collection<EntityState> entityStates = core.getEntityStates();
        if (entityStates.size() > 0) {

            for (EntityState entityState : entityStates) {

                Entity entity = entityState.getOwner();

                if (entity == null) {

                    continue;

                }

                // Skip this entity if they don't have any hats
                if (entityState.getHatCount() == 0) {

                    continue;

                }

                // Skip this world if it is disabled
                World world = entity.getWorld();
                if (disabledWorlds.contains(world.getName())) {

                    continue;

                }

                // Checks and updates the entities mode
                checkMode(entity.getUniqueId(), entityState);

            }

        }

    }

    public void onReload() {

        afkCooldown = SettingsManager.AFK_COOLDOWN.getInt() * 1000;
        pvpCooldown = SettingsManager.COMBAT_COOLDOWN.getInt() * 1000;

        disabledWorlds.clear();
        disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();

    }

    private void checkMode(UUID id, EntityState entityState) {

        Entity entity = entityState.getOwner();
        AFKState afkState = entityState.getAFKState();
        PVPState pvpState = entityState.getPVPState();

        Location lastKnownLocation = entityState.getLastKnownLocation();
        Location currentLocation = entity.getLocation();

        // Make sure there is always a last known location
        if (lastKnownLocation == null || !(lastKnownLocation.getWorld().equals(currentLocation.getWorld()))) {

            lastKnownLocation = currentLocation;
            entityState.setLastKnownLocation(lastKnownLocation);

        }

        // Update our last move time if the 2 locations are more than a block from each
        // other
        double activeDistance = lastKnownLocation.distanceSquared(currentLocation);
        if (activeDistance >= 1) {

            entityState.setLastMoveTime(System.currentTimeMillis());
            entityState.setLastKnownLocation(currentLocation);

        }

        if (afkState == AFKState.AFK) {

            Location afkLocation = entityState.getAFKLocation();
            if (afkLocation != null) {

                double distance = 7;
                if (afkLocation.getWorld().equals(currentLocation.getWorld())) {

                    distance = afkLocation.distanceSquared(currentLocation);

                }

                if (distance > 6) {

                    entityState.setAFKState(AFKState.ACTIVE);
                    entityState.setLastKnownLocation(currentLocation);
                    entityState.setLastMoveTime(System.currentTimeMillis());

                }

            }

        } else {

            final long timeAFK = System.currentTimeMillis() - entityState.getLastMoveTime();
            if (timeAFK > afkCooldown) {

                entityState.setAFKState(AFKState.AFK);
                entityState.setAFKLocation(currentLocation);
                entityState.setLastKnownLocation(currentLocation);

            }

        }

        if (pvpState == PVPState.ENGAGED) {

            final long timeEngaged = System.currentTimeMillis() - entityState.getLastCombatTime();
            if (timeEngaged > pvpCooldown) {

                entityState.setPVPState(PVPState.PEACEFUL);

            }

        }

    }

}
