package cosmeticsOG.editor.menus;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.editor.MetaState;
import cosmeticsOG.locale.Message;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.StringUtil;
import net.kyori.adventure.text.Component;

public class EditorItemPromptMenu extends AbstractStaticMenu {

	private final EditorMenuManager editorManager;
	private final MenuObjectCallback callback;
	private final Message itemName;
	private final Message itemDescription;

	private boolean isSearching = false;

	public EditorItemPromptMenu(CosmeticsOG core, EditorMenuManager menuManager, Player owner, Message title, Message itemName, Message itemDescription, MenuObjectCallback callback) {

		super(core, menuManager, owner);

		this.editorManager = menuManager;
		this.callback = callback;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		this.inventory = Bukkit.createInventory(null, 54, Utils.legacySerializerAnyCase(title.getValue()));

		build();

	}

	@Override
	public MenuClickResult onClickOutside (InventoryClickEvent event, final int slot) {

		ItemStack item = event.getCurrentItem();
		if (item == null) {

			return MenuClickResult.NONE;

		}

		ItemStack i = new ItemStack(item.getType(), 1);

		editorManager.getOwnerState().addRecentItem(i);
		callback.onSelect(i);

		return MenuClickResult.NEUTRAL;

	}

	@Override
	protected void build() {

		ItemStack info = ItemUtil.createItem(CompatibleMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.getMaterial(), 1, itemName.getValue());

		ItemUtil.setItemDescription(info, StringUtil.parseDescription(itemDescription.getValue()));

		for (int i = 0; i < inventory.getSize(); i++) {

			setItem(i, info);
		}

		// Back Button.
		setButton(10, backButtonItem, backButtonAction);

		// Search Button.
		setButton(37, ItemUtil.createItem(CompatibleMaterial.SIGN.getMaterial(), 1, Message.EDITOR_MISC_SEARCH.getValue()), (event, slot) -> {

			isSearching = true;

			core.getPlayerState(owner).setMetaState(MetaState.BLOCK_SEARCH);
			core.prompt(owner, MetaState.BLOCK_SEARCH);

			menuManager.closeInventory();

			return MenuClickResult.NEUTRAL;

		});

		// Recents.
		MenuAction selectRecentAction = (event, slot) -> {

			ItemStack item = event.getEvent().getCurrentItem();
			if (item == null) {

				return MenuClickResult.NONE;

			}

			ItemStack i = new ItemStack(item.getType(), 1);

			editorManager.getOwnerState().addRecentItem(i);
			callback.onSelect(i);

			return MenuClickResult.NEUTRAL;

		};

		List<ItemStack> recentItems = editorManager.getOwnerState().getRecentItems();
		List<Component> description = StringUtil.parseDescription(Message.EDITOR_ICON_MENU_RECENTS_DESCRIPTION.getValue());
		int index = 0;
		for (ItemStack item : recentItems) {

			if (index >= 20) {

				return;

			}

			ItemStack i = item.clone();

			ItemUtil.setItemDescription(i, description);

			setButton(getNormalIndex(index++, 12, 4), i, selectRecentAction);

		}

	}

	@Override
	public void open () {

		super.open();

		if (isSearching) {

			isSearching = false;

			String searchQuery = editorManager.getMetaArgument();
			if (! searchQuery.equals("")) {

				editorManager.resetMetaArgument();

				EditorSearchMenu editorSearchMenu = new EditorSearchMenu(core, menuManager, owner, searchQuery, (item) -> {

					ItemStack i = (ItemStack) item;
					if (i == null) {

						return;

					}

					menuManager.closeCurrentMenu();
					editorManager.getOwnerState().addRecentItem(i);
					callback.onSelect(i);

				});

				menuManager.addMenu(editorSearchMenu);

				editorSearchMenu.open();

			}

		}

	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {}

}