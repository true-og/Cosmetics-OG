package cosmeticsOG.tasks;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.properties.ParticleTag;
import cosmeticsOG.particles.properties.ParticleType;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.player.EntityState;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.util.PlayerUtil;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class HatTask extends BukkitRunnable {

    private final CosmeticsOG plugin;
    private final Entity entity;
    private final Hat hat;

    private List<String> disabledWorlds;
    private boolean checkWorldPermission;
    private boolean essentialsVanishFlag;

    private int ticks = 0;

    public HatTask(CosmeticsOG plugin, Entity entity, @NotNull Hat hat) {
        this.plugin = plugin;
        this.entity = entity;
        this.hat = hat;

        disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();
        checkWorldPermission = SettingsManager.CHECK_WORLD_PERMISSION.getBoolean();
        essentialsVanishFlag = SettingsManager.FLAG_ESSENTIALS_VANISH.getBoolean();

        runTaskTimerAsynchronously(plugin, 0, 1L);
    }

    public Hat getHat() {
        return hat;
    }

    public void reload() {
        disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();
        checkWorldPermission = SettingsManager.CHECK_WORLD_PERMISSION.getBoolean();
        essentialsVanishFlag = SettingsManager.FLAG_ESSENTIALS_VANISH.getBoolean();
    }

    @Override
    public void run() {
        // Skip this world if it is disabled
        World world = entity.getWorld();
        if (disabledWorlds.contains(world.getName())) {
            return;
        }

        EntityState entityState = plugin.getEntityState(entity);

        // Handle player checks
        if (entityState instanceof PlayerState) {
            PlayerState playerState = (PlayerState) entityState;
            Player player = playerState.getOwner();

            // Make sure the player has permission for this world
            if (checkWorldPermission) {
                if (!player.hasPermission(Permission.WORLD_ALL.getPermission())
                        && !player.hasPermission(Permission.WORLD.append(world.getName()))) {
                    return;
                }
            }

            // Skip if the player has a potion of invisibility
            if (essentialsVanishFlag && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                if (hat.isDisplaying()) {
                    hat.setIsDisplaying(false, player);
                }
                return;
            }
        }

        if (hat.isVanished() || hat.isHidden()) {
            return;
        }

        checkHat(entityState, hat, true);

        ticks++;
        hat.onTick(ticks, entity);

        // Unequip if this hat's demo duration has run out.
        if (!hat.isDemoActive()) {
            PlayerUtil.runNextTick(() -> entityState.removeHat(hat));
        }
    }

    public void stop() {
        try {
            cancel();
        } catch (Exception ignored) {
        }
    }

    private void checkHat(@NotNull EntityState entityState, @NotNull Hat hat, boolean checkNode) {
        switch (hat.getMode()) {
            case ACTIVE:
                displayHat(entity, hat);
                break;

            case WHEN_MOVING:
                if (entityState.getAFKState() == PlayerState.AFKState.ACTIVE) {
                    displayHat(entity, hat);
                }
                break;

            case WHEN_AFK:
                if (entityState.getAFKState() == PlayerState.AFKState.AFK) {
                    displayHat(entity, hat);
                }
                break;

            case WHEN_PEACEFUL:
                if (entityState.getPVPState() == PlayerState.PVPState.PEACEFUL) {
                    displayHat(entity, hat);
                }
                break;

            case WHEN_GLIDING:
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    if (player.isGliding()) {
                        displayHat(entity, hat);
                    }
                }
                break;

            case WHEN_SPRINTING:
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    if (player.isSprinting()) {
                        displayHat(entity, hat);
                    }
                }
                break;

            case WHEN_SWIMMING:
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    if (player.isSwimming()) {
                        displayHat(entity, hat);
                    }
                }
                break;

            case WHEN_FLYING:
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    if (player.isFlying()) {
                        displayHat(entity, hat);
                    }
                }
                break;
        }

        // Loop through and check each node hat
        if (checkNode) {
            for (Hat node : hat.getNodes()) {
                checkHat(entityState, node, false);
            }
        }
    }

    private void displayHat(Entity entity, @NotNull Hat hat) {
        if (hat.getType() == ParticleType.NONE) {
            return;
        }

        if (!handleTags(entity, hat)) {
            return;
        }

        hat.displayType(ticks, entity);
    }

    private boolean handleTags(Entity entity, @NotNull Hat hat) {
        List<ParticleTag> tags = hat.getTags();

        if (tags.contains(ParticleTag.ARROWS)) {
            for (Entity e : PlayerUtil.getNearbyEntitiesAsync(entity, 50, 50, 50)) {
                if (e instanceof Arrow) {
                    Arrow arrow = (Arrow) e;
                    if (!arrow.isOnGround()) {
                        if (arrow.getShooter() instanceof Player) {
                            Player player = (Player) arrow.getShooter();
                            if (player.equals(entity)) {
                                hat.displayType(ticks, arrow);
                            }
                        }
                    }
                }
            }
            return false;
        }

        if (tags.contains(ParticleTag.PICTURE_MODE) && entity instanceof Player) {
            displayToNearestEntity((Player) entity, hat, ArmorStand.class);
            return false;
        }

        return true;
    }

    private void displayToNearestEntity(@NotNull Player player, Hat hat, Class<?> entity) {
        Entity nearest = null;
        double maxDistance = 1000;

        for (Entity e : PlayerUtil.getNearbyEntitiesAsync(player, 50, 10, 50)) {
            if (entity.isInstance(e)) {
                double distance = e.getLocation().distanceSquared(player.getLocation());
                if (distance < maxDistance) {
                    maxDistance = distance;
                    nearest = e;
                }
            }
        }

        if (nearest != null) {
            hat.displayType(ticks, nearest);
        }
    }
}
