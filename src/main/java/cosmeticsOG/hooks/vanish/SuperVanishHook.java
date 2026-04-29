package cosmeticsOG.hooks.vanish;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.hooks.VanishHook;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.player.PlayerState;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

public class SuperVanishHook implements VanishHook, Listener {

    private static final String VANISH_METADATA_KEY = "vanished";
    private static final String VANISH_API_CLASS = "de.myzelyam.api.vanish.VanishAPI";
    private static final String[] VANISH_EVENT_CLASSES = { "de.myzelyam.api.vanish.PostPlayerHideEvent",
            "de.myzelyam.api.vanish.PostPlayerShowEvent", "de.myzelyam.api.vanish.PlayerHideEvent",
            "de.myzelyam.api.vanish.PlayerShowEvent" };

    private final CosmeticsOG core;
    private final Plugin vanishPlugin;
    private Method isVanishedMethod;
    private boolean hasLoadedVanishAPI;

    public SuperVanishHook(final CosmeticsOG core, final Plugin vanishPlugin) {

        this.core = core;
        this.vanishPlugin = vanishPlugin;

        registerVanishEvents();
        syncOnlinePlayers();

    }

    @Override
    public boolean isVanished(Player player) {

        Boolean apiVanished = getVanishAPIState(player);
        if (apiVanished != null) {

            return apiVanished;

        }

        return hasVanishMetadata(player);

    }

    @Override
    public void unregister() {

        HandlerList.unregisterAll(this);

    }

    private void registerVanishEvents() {

        registerVanishEvent(VANISH_EVENT_CLASSES[0], true);
        registerVanishEvent(VANISH_EVENT_CLASSES[1], false);
        registerVanishEvent(VANISH_EVENT_CLASSES[2], true);
        registerVanishEvent(VANISH_EVENT_CLASSES[3], false);

    }

    @SuppressWarnings("unchecked")
    private void registerVanishEvent(String eventClassName, boolean vanished) {

        try {

            Class<?> eventClass = Class.forName(eventClassName, false, vanishPlugin.getClass().getClassLoader());
            if (!Event.class.isAssignableFrom(eventClass)) {

                return;

            }

            EventExecutor executor = (listener, event) -> handleVanishEvent(event, vanished);
            core.getServer().getPluginManager().registerEvent((Class<? extends Event>) eventClass, this,
                    EventPriority.MONITOR, executor, core, true);

        } catch (ClassNotFoundException ignored) {

        }

    }

    private void handleVanishEvent(Event event, boolean vanished) {

        if (event instanceof PlayerEvent) {

            syncPlayer(((PlayerEvent) event).getPlayer(), vanished);

        }

    }

    private void syncOnlinePlayers() {

        for (Player player : core.getServer().getOnlinePlayers()) {

            syncPlayer(player, isVanished(player));

        }

    }

    private void syncPlayer(Player player, boolean vanished) {

        if (!core.hasEntityState(player)) {

            return;

        }

        PlayerState playerState = core.getPlayerState(player);
        for (Hat hat : playerState.getActiveHats()) {

            hat.setVanished(vanished);

        }

    }

    private Boolean getVanishAPIState(Player player) {

        Method method = getIsVanishedMethod();
        if (method == null) {

            return null;

        }

        try {

            Object result = method.invoke(null, player);
            if (result instanceof Boolean) {

                return (Boolean) result;

            }

        } catch (ReflectiveOperationException | RuntimeException ignored) {

        }

        return null;

    }

    private Method getIsVanishedMethod() {

        if (!hasLoadedVanishAPI) {

            hasLoadedVanishAPI = true;
            try {

                Class<?> vanishAPI = Class.forName(VANISH_API_CLASS, false, vanishPlugin.getClass().getClassLoader());
                isVanishedMethod = vanishAPI.getMethod("isVanished", Player.class);

            } catch (ReflectiveOperationException | RuntimeException ignored) {

            }

        }

        return isVanishedMethod;

    }

    private boolean hasVanishMetadata(Player player) {

        for (MetadataValue value : player.getMetadata(VANISH_METADATA_KEY)) {

            Plugin owningPlugin = value.getOwningPlugin();
            if (owningPlugin != null && owningPlugin.equals(vanishPlugin) && value.asBoolean()) {

                return true;

            }

        }

        return false;

    }

}
