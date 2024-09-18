package cosmeticsOG.editor.menus;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.database.Database;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.ParticleEffect;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.util.ItemUtil;

public class EditorParticleMenuOverview extends AbstractStaticMenu {

	private final EditorMenuManager editorManager;
	private final EditorMainMenu editorMainMenu;
	private final Hat targetHat;

	private Map<Integer, ParticleEffect> particles;
	private Map<Integer, Boolean > modifiedParticles;

	public EditorParticleMenuOverview(CosmeticsOG core, EditorMenuManager menuManager, Player owner, EditorMainMenu editorMainMenu) {

		super(core, menuManager, owner);

		this.editorManager = menuManager;
		this.editorMainMenu = editorMainMenu;
		this.targetHat = menuManager.getTargetHat();
		this.particles = new HashMap<Integer, ParticleEffect>();
		this.modifiedParticles = new HashMap<Integer, Boolean>();	
		this.inventory = Bukkit.createInventory(null, 54, Utils.legacySerializerAnyCase(Message.EDITOR_PARTICLE_OVERVIEW_MENU_TITLE.getValue()));

		build();

	}

	@Override
	protected void build() {

		setButton(49, backButtonItem, backButtonAction);

		MenuAction editAction = (event, slot) -> {

			int particleIndex = getClampedIndex(slot, 10, 2);
			if (event.isLeftClick()) {

				EditorParticleSelectionMenu editorParticleSelectionMenu = new EditorParticleSelectionMenu(core, editorManager, owner, particleIndex, (particle) -> {

					menuManager.closeCurrentMenu();

					if (particle == null) {

						return;

					}

					ParticleEffect pe = (ParticleEffect)particle;
					targetHat.setParticle(particleIndex, pe);

					// Add this particle to the recents list.
					core.getParticleManager().addParticleToRecents(ownerID, pe);

					ItemStack item = getItem(slot);
					ItemStack itemStack = pe.getItem();
					Material material = itemStack.getType();
					int damage = 0;

					ItemUtil.setItemType(item, material, damage);

					EditorLore.updateParticleDescription(item, targetHat, particleIndex);	

					modifiedParticles.put(particleIndex, true);

				});

				menuManager.addMenu(editorParticleSelectionMenu);

				editorParticleSelectionMenu.open();

			}

			else if (event.isRightClick()) {

				editorMainMenu.onParticleEdit(getItem(slot), particleIndex);

			}

			return MenuClickResult.NEUTRAL;

		};

		for (int i = 0; i < 28; i++) {

			setAction(getNormalIndex(i, 10, 2), editAction);

		}

		String itemTitle = Message.EDITOR_PARTICLE_OVERVIEW_PARTICLE_NAME.getValue();
		int particlesSupported = targetHat.getType().getParticlesSupported();
		for (int i = 0; i < particlesSupported; i++) {

			ParticleEffect particle = targetHat.getParticle(i);
			ItemStack item = particle.getItem().clone();

			ItemUtil.setItemName(item, itemTitle.replace("{1}", Integer.toString(i + 1)));

			particles.put(i, particle);
			modifiedParticles.put(i, false);

			EditorLore.updateParticleDescription(item, targetHat, i);

			setItem(getNormalIndex(i, 10, 2), item);

		}

	}

	@Override
	public void onClose(boolean forced) {

		Database database = core.getDatabase();
		String menuName = editorManager.getMenuName();
		for (Entry<Integer, Boolean> entry : modifiedParticles.entrySet()) {

			if (entry.getValue()) {

				database.saveParticleData(menuName, targetHat, entry.getKey());

			}

		}

		for (int i = 0; i < targetHat.getType().getParticlesSupported(); i++) {

			if (targetHat.getParticleData(i).hasPropertyChanges()) {

				database.saveParticleData(menuName, targetHat, i);

			}

		}

	}

	@Override
	public void onTick(int ticks) {}

}