package cosmeticsOG.managers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.hooks.VanishHook;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.ParticleEffect;
import cosmeticsOG.permission.Permission;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.AbstractMenu;
import cosmeticsOG.ui.MenuManager;
import cosmeticsOG.ui.StaticMenu;
import cosmeticsOG.ui.StaticMenuManager;

public class ParticleManager {

	private final CosmeticsOG core;
	private final Deque<ParticleEffect> emptyRecents = new ArrayDeque<ParticleEffect>();

	private Map<UUID, Deque<ParticleEffect>> recentParticles;

	public ParticleManager (CosmeticsOG core) {

		this.core = core;

		recentParticles = new HashMap<UUID, Deque<ParticleEffect>>();

	}

	/**
	 * Adds this particle to the list of recently used particles
	 * @param id
	 * @param particle
	 */
	public void addParticleToRecents (UUID id, ParticleEffect particle) {

		Deque<ParticleEffect> particles = recentParticles.get(id);
		if (particles == null) {

			particles = new ArrayDeque<ParticleEffect>();

			recentParticles.put(id, particles);

		}

		// Only add unique particles, no duplicates.
		if (! particles.contains(particle)) {

			particles.addFirst(particle);

		}

		// We're only showing one menu worth of particles, so limit it to 21 slots.
		if (particles.size() > 21) {

			particles.removeLast();

		}

	}

	/**
	 * Gets all particles recently used by the player
	 * @param id
	 * @return
	 */
	public Deque<ParticleEffect> getRecentlyUsedParticles (UUID id) {

		if (recentParticles.containsKey(id)) {

			return recentParticles.get(id);

		}

		return emptyRecents;

	}

	public boolean equipHat (Player player, Hat hat) {

		if (equipHat(player, hat, true)) {

			MenuManager menuManager = core.getPlayerState(player).getMenuManager();
			if (menuManager instanceof StaticMenuManager) {

				StaticMenuManager staticMenuManager = (StaticMenuManager)menuManager;
				AbstractMenu menu = staticMenuManager.getMenuFromCache(hat.getMenu());

				if (menu instanceof StaticMenu) {

					((StaticMenu) menu).equipHat(hat);

				}

			}

			return true;

		}

		return false;

	}

	public boolean equipHat (Player player, Hat hat, boolean showEquipMessage) {

		PlayerState playerState = core.getPlayerState(player);
		String worldName = player.getWorld().getName().toLowerCase();
		List<String> disabledWorlds = SettingsManager.DISABLED_WORLDS.getList();

		// Disabled World.
		if (disabledWorlds.contains(worldName)) {

			Utils.cosmeticsOGPlaceholderMessage(player, Message.WORLD_DISABLED.getValue());

			return false;

		}

		// No world permission.
		if (SettingsManager.CHECK_WORLD_PERMISSION.getBoolean()) {
			if (! player.hasPermission(Permission.WORLD_ALL.getPermission()) && !player.hasPermission(Permission.WORLD.append(worldName))) {

				Utils.cosmeticsOGPlaceholderMessage(player, Message.WORLD_NO_PERMISSION.getValue());

				return false;

			}

		}

		// Too many hats equipped.
		if (! playerState.canEquip()) {

			if (SettingsManager.UNEQUIP_OVERFLOW_HATS.getBoolean()) {

				if (playerState.isEquipOverflowed()) {

					playerState.removeLastHat();

				}

			}

			else {

				Utils.cosmeticsOGPlaceholderMessage(player, Message.HAT_EQUIPPED_OVERFLOW.replace("{1}", Integer.toString(SettingsManager.MAXIMUM_HAT_LIMIT.getInt())));

				return false;

			}

		}

		if (playerState.canEquip()) {

			boolean isVanished = false;
			if (SettingsManager.FLAG_VANISH.getBoolean()) {

				VanishHook vanishHook = core.getHookManager().getVanishHook();
				if (vanishHook != null) {

					isVanished = vanishHook.isVanished(player);

				}

			}

			hat.setVanished(isVanished);
			playerState.addHat(hat);

			if (showEquipMessage) {

				String equipMessage = hat.getEquipDisplayMessage();

				if (! equipMessage.equals("")) {

					Utils.cosmeticsOGPlaceholderMessage(player, equipMessage);

				} 

				else {

					Message defaultMessage = hat.isVanished() ? Message.HAT_EQUIPPED_VANISHED : Message.HAT_EQUIPPED;

					Utils.cosmeticsOGPlaceholderMessage(player, defaultMessage.replace("{1}", hat.getDisplayName()));

				}

			}

		}

		return true;

	}

}