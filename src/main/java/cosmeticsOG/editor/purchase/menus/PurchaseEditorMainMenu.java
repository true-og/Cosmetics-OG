package cosmeticsOG.editor.purchase.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.database.Database;
import cosmeticsOG.database.Database.DataType;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.MetaState;
import cosmeticsOG.editor.menus.EditorActionMenu;
import cosmeticsOG.editor.menus.EditorBaseMenu;
import cosmeticsOG.editor.menus.EditorDescriptionMenu;
import cosmeticsOG.editor.menus.EditorIconMenuOverview;
import cosmeticsOG.editor.menus.EditorSlotMenu;
import cosmeticsOG.editor.menus.EditorSoundMenu;
import cosmeticsOG.editor.purchase.PurchaseMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.properties.ParticleAction;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.StringUtil;

public class PurchaseEditorMainMenu extends AbstractStaticMenu {

	private final PurchaseMenuManager purchaseManager;
	private final EditorBaseMenu editorBaseMenu;
	private Hat targetHat;

	public PurchaseEditorMainMenu(CosmeticsOG core, PurchaseMenuManager menuManager, Player owner) {

		super(core, menuManager, owner);

		this.purchaseManager = menuManager;
		this.editorBaseMenu = menuManager.getEditingMenu();
		this.targetHat = menuManager.getBaseHat();
		this.inventory = Bukkit.createInventory(null, 36, Utils.legacySerializerAnyCase(Message.EDITOR_MAIN_MENU_TITLE.getValue()));

		build();

	}

	@Override
	protected void build() {

		setButton(31, backButtonItem, backButtonAction);

		ItemStack actionItem = ItemUtil.createItem(CompatibleMaterial.GUNPOWDER.getMaterial(), 1, Message.EDITOR_MAIN_MENU_SET_ACTION.getValue());
		EditorLore.updateSpecificActionDescription(actionItem, targetHat, targetHat.getLeftClickAction(), targetHat.getLeftClickArgument());
		setButton(11, actionItem, (event, slot) -> {

			EditorActionMenu editorActionMenu = new EditorActionMenu(core, purchaseManager, owner, true, true, (action) -> {

				if (action == null) {

					return;

				}

				ParticleAction pa = (ParticleAction) action;
				targetHat.setLeftClickAction(pa);

				EditorLore.updateSpecificActionDescription(getItem(11), targetHat, targetHat.getLeftClickAction(), targetHat.getLeftClickArgument());

				menuManager.closeCurrentMenu();

			});

			menuManager.addMenu(editorActionMenu);
			editorActionMenu.open();

			return MenuClickResult.NEUTRAL;

		});

		ItemStack nameItem = ItemUtil.createItem(CompatibleMaterial.PLAYER_HEAD.getMaterial(), 1, Message.EDITOR_META_MENU_SET_NAME.getValue());
		setButton(13, nameItem, (event, slot) -> {

			if (event.isLeftClick()) {

				purchaseManager.getOwnerState().setMetaState(MetaState.HAT_NAME);
				core.prompt(owner, MetaState.HAT_NAME);
				menuManager.closeInventory();

			}

			else {

				targetHat.setName(Message.EDITOR_MISC_NEW_PARTICLE.getValue());
				EditorLore.updateNameDescription(getItem(13), targetHat);

			}

			return MenuClickResult.NEUTRAL;

		});

		ItemStack descriptionItem = ItemUtil.createItem(CompatibleMaterial.WRITABLE_BOOK.getMaterial(), 1, Message.EDITOR_META_MENU_SET_DESCRIPTION.getValue());
		setButton(15, descriptionItem, (event, slot) -> {

			if (event.isLeftClick()) {

				EditorDescriptionMenu editorDescriptionMenu = new EditorDescriptionMenu(core, purchaseManager, owner, true);
				menuManager.addMenu(editorDescriptionMenu);
				editorDescriptionMenu.open();

			}

			else if (event.isShiftRightClick()) {

				if (! targetHat.getDescription().isEmpty()) {

					targetHat.getDescription().clear();

					Database database = core.getDatabase();
					String menuName = purchaseManager.getMenuName();
					database.saveMetaData(menuName, targetHat, DataType.DESCRIPTION, 0);

					EditorLore.updateDescriptionDescription(getItem(15), targetHat.getDescription());

				}

			}

			return MenuClickResult.NEUTRAL;

		});

		ItemStack slotItem = ItemUtil.createItem(Material.ITEM_FRAME, Message.EDITOR_MAIN_MENU_SET_SLOT.getValue(), StringUtil.parseDescription(Message.EDITOR_MAIN_MENU_SLOT_DESCRIPTION.getValue()));
		setButton(19, slotItem, (event, slot) -> {

			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, purchaseManager, owner, editorBaseMenu, false);
			menuManager.addMenu(editorSlotMenu);

			editorSlotMenu.open();

			return MenuClickResult.NEUTRAL;

		});

		ItemStack cloneItem = ItemUtil.createItem(CompatibleMaterial.PRISMARINE_SHARD.getMaterial(), Message.EDITOR_MAIN_MENU_CLONE.getValue(), StringUtil.parseDescription(Message.EDITOR_MAIN_MENU_CLONE_DESCRIPTION.getValue()));
		setButton(21, cloneItem, (event, slot) -> {

			if (targetHat.isModified()) {

				int targetSlot = purchaseManager.getTargetSlot();

				core.getDatabase().saveHat(editorBaseMenu.getMenuInventory().getName(), targetSlot, targetHat);

				targetHat.clearPropertyChanges();

			}

			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, purchaseManager, owner, editorBaseMenu, true);
			menuManager.addMenu(editorSlotMenu);

			editorSlotMenu.open();

			return MenuClickResult.NEUTRAL;

		});

		ItemStack soundItem = ItemUtil.createItem(CompatibleMaterial.MUSIC_DISC_STRAD.getMaterial(), 1, Message.EDITOR_MAIN_MENU_SET_SOUND.getValue());
		EditorLore.updateSoundItemDescription(soundItem, targetHat);
		setButton(23, soundItem, (event, slot) -> {

			if (event.isLeftClick()) {

				EditorSoundMenu editorSoundMenu = new EditorSoundMenu(core, purchaseManager, owner, (sound) -> {

					menuManager.closeCurrentMenu();

					if (sound == null) {

						return;

					}

					Sound s = (Sound) sound;

					targetHat.setSound(s);
					EditorLore.updateSoundItemDescription(getItem(23), targetHat);

				});

				menuManager.addMenu(editorSoundMenu);
				editorSoundMenu.open();

			}
			else if (event.isShiftRightClick()) {

				targetHat.removeSound();
				EditorLore.updateSoundItemDescription(getItem(23), targetHat);

			}

			return MenuClickResult.NEUTRAL;

		});

		ItemStack iconItem = targetHat.getItem();
		ItemUtil.setNameAndDescription(iconItem, Message.EDITOR_MAIN_MENU_SET_ICON, Message.EDITOR_MAIN_MENU_ICON_DESCRIPTION);
		setButton(25, iconItem, (event, slot) -> {

			EditorIconMenuOverview editorIconMenuOverview = new EditorIconMenuOverview(core, purchaseManager, owner, (mainItem) -> {

				if (mainItem == null) {

					return;

				}

				ItemStack item = (ItemStack) mainItem;

				// Update the button with the new item.
				setButton(25, item, (menuEvent, menuSlot) -> MenuClickResult.NEUTRAL);

			});

			menuManager.addMenu(editorIconMenuOverview);
			editorIconMenuOverview.open();

			return MenuClickResult.NEUTRAL;

		});

	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void open () {

		EditorLore.updateNameDescription(getItem(13), targetHat);
		EditorLore.updateDescriptionDescription(getItem(15), targetHat.getDescription());

		super.open();

	}

	@Override
	public void onTick(int ticks) {}

}