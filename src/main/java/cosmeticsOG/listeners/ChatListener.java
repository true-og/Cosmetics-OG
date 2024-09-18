package cosmeticsOG.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.editor.MetaState;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.MenuManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Listens for a player editing meta properties through the menu editor
 * @author MediusEcho
 *
 */
public class ChatListener implements Listener {

	private final CosmeticsOG core;

	public ChatListener (final CosmeticsOG core) {

		this.core = core;	

		core.getServer().getPluginManager().registerEvents(this, core);

	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncChatEvent event) {

		if (SettingsManager.EDITOR_USE_ACTION_BAR.getBoolean())	{

			Player player = event.getPlayer();
			PlayerState playerState = core.getPlayerState(player);

			if (playerState.hasMenuManager()) {

				MetaState metaState = playerState.getMetaState();
				if (metaState.equals(MetaState.NONE)) {

					return;

				}

				MenuManager menuManager = playerState.getMenuManager();
				if (! (menuManager instanceof EditorMenuManager)) {

					return;

				}

				EditorMenuManager editorManager = (EditorMenuManager) menuManager;

				event.setCancelled(true);

				Bukkit.getScheduler().scheduleSyncDelayedTask(CosmeticsOG.instance, () -> {

					// Convert the Component to a plain string
					String messageString = PlainTextComponentSerializer.plainText().serialize(event.message());

					if (Utils.stripColors(messageString).equals("cancel")) {

						editorManager.reopen();

						return;

					}

					List<String> arguments = Arrays.asList(messageString.split(" "));

					metaState.onMetaSet(editorManager, player, arguments);

				});

			}

		}

	}

	/**
	 * Unregisters this AsyncPlayerChatEvent Listener
	 */
	public void unregister () {

		AsyncChatEvent.getHandlerList().unregister(this);

	}

}