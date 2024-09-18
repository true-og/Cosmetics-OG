package cosmeticsOG.editor.menus;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.database.Database.DataType;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.properties.IconData;
import cosmeticsOG.particles.properties.IconData.ItemStackTemplate;
import cosmeticsOG.particles.properties.IconDisplayMode;
import cosmeticsOG.ui.AbstractListMenu;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.MathUtil;
import cosmeticsOG.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class EditorIconMenuOverview extends AbstractListMenu {

	private final EditorMenuManager editorManager;
	private final MenuObjectCallback callback;
	private final Hat targetHat;

	private boolean isModified = false;
	private int editingIndex = 0;

	private final Message iconTitle = Message.EDITOR_ICON_MENU_ITEM_TITLE;
	private final Message iconName = Message.EDITOR_ICON_MENU_ITEM_INFO;
	private final Message iconDescription = Message.EDITOR_ICON_MENU_ITEM_DESCRIPTION;

	public EditorIconMenuOverview(CosmeticsOG core, EditorMenuManager menuManager, Player owner, MenuObjectCallback callback) {

		super(core, menuManager, owner, true);

		this.editorManager = menuManager;
		this.callback = callback;
		this.targetHat = editorManager.getBaseHat();
		this.totalPages = 1;

		setMenu(0, Bukkit.createInventory(null, 54, Utils.legacySerializerAnyCase(Message.EDITOR_ICON_OVERVIEW_MENU_TITLE.getValue())));

		build();

	}

	@Override
	public void insertEmptyItem() {}

	@Override
	public void removeEmptyItem() {}

	@Override
	protected void build() {

		setButton(0, 46, backButtonItem, backButtonAction);

		// Set Main Icon.
		ItemStack mainItem = targetHat.getItem();
		ItemUtil.setItemName(mainItem, Message.EDITOR_ICON_MENU_SET_MAIN_ICON);
		setButton(0, 10, mainItem, (event, slot) -> {

			editingIndex = 0;

			EditorItemPromptMenu editorItemPromptMenu = new EditorItemPromptMenu(core, editorManager, owner, iconTitle, iconName, iconDescription, (item) -> {

				editorManager.closeCurrentMenu();

				if (item == null) {

					return;

				}

				ItemStack i = ((ItemStack)item).clone();
				i.setAmount(1);

				editorManager.getEditingMenu().setItemType(editorManager.getTargetSlot(), i);

				// Extract Material from ItemStack 'i'.
				Material material = i.getType();
				// Declare a container for the item's durability.
				int durability = 0;
				if (i.hasItemMeta() && i.getItemMeta() instanceof Damageable) {

					Damageable meta = (Damageable) i.getItemMeta();

					durability = meta.getDamage();

				}

				// Set the item type with the correct material and durability.
				ItemUtil.setItemType(getItem(0, 10), material, durability);

				callback.onSelect(i);
				targetHat.setItem(i);

				isModified = true;

			});

			menuManager.addMenu(editorItemPromptMenu);

			editorItemPromptMenu.open();

			return MenuClickResult.NEUTRAL;

		});

		// Preview Icon.
		setItem(0, 48, ItemUtil.createItem(CompatibleMaterial.SUNFLOWER.getMaterial(), 1, Message.EDITOR_ICON_MENU_PREVIEW.getValue()));

		// Display Mode.
		ItemStack displayItem = ItemUtil.createItem(CompatibleMaterial.ROSE_RED.getMaterial(), 1, Message.EDITOR_ICON_MENU_SET_DISPLAY_MODE.getValue());
		EditorLore.updateDisplayModeDescription(displayItem, targetHat.getIconData().getDisplayMode(), Message.EDITOR_ICON_MENU_DISPLAY_MODE_DESCRIPTION);
		setButton(0, 50, displayItem, (event, slot) -> {

			final int increment = event.isLeftClick() ? 1 : -1;
			final int modeID = MathUtil.wrap(targetHat.getDisplayMode().getID() + increment, IconDisplayMode.values().length, 0);
			final IconDisplayMode mode = IconDisplayMode.fromId(modeID);

			targetHat.setDisplayMode(mode);

			EditorLore.updateDisplayModeDescription(getItem(0, 50), mode, Message.EDITOR_ICON_MENU_DISPLAY_MODE_DESCRIPTION);

			return MenuClickResult.NEUTRAL;

		});

		// Update Frequency.
		ItemStack frequencyItem = ItemUtil.createItem(CompatibleMaterial.REPEATER.getMaterial(), 1, Message.EDITOR_ICON_MENU_SET_UPDATE_FREQUENCY.getValue());
		EditorLore.updateFrequencyDescription(frequencyItem, targetHat.getIconUpdateFrequency(), Message.EDITOR_ICON_MENU_UPDATE_FREQUENCY_DESCRIPTION);
		setButton(0, 49, frequencyItem, (event, slot) -> {

			final int increment = event.isLeftClick() ? 1 : -1;
			final int frequency = (int) MathUtil.clamp(targetHat.getIconUpdateFrequency() + increment, 1, 63);

			targetHat.setIconUpdateFrequency(frequency);

			EditorLore.updateFrequencyDescription(getItem(0, 49), frequency, Message.EDITOR_ICON_MENU_UPDATE_FREQUENCY_DESCRIPTION);

			return MenuClickResult.NEUTRAL;

		});

		// Edit Action.
		MenuAction editAction = (event, slot) -> {

			editingIndex = getClampedIndex(slot, 10, 2);

			if (event.isLeftClick()) {

				EditorItemPromptMenu editorItemPromptMenu = new EditorItemPromptMenu(core, editorManager, owner, iconTitle, iconName, iconDescription, (item) -> {

					editorManager.closeCurrentMenu();

					if (item == null) {

						return;

					}

					ItemStack editingItem = ((ItemStack) item).clone();
					editingItem.setAmount(1);

					// Extract the Material from the item.
					Material material = editingItem.getType();

					// Construct a formatted Material name for the item.
					String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(material);

					// Extract the durability (damage).
					int damage = 0;
					if (editingItem.hasItemMeta() && editingItem.getItemMeta() instanceof Damageable) {

						Damageable meta = (Damageable) editingItem.getItemMeta();

						damage = meta.getDamage();

					}

					// Get the current item from the inventory slot.
					ItemStack currentItem = getItem(0, slot);

					// Set the material type and durability for the current item.
					ItemUtil.setItemType(currentItem, material, damage);

					// Set the item's name.
					ItemUtil.setItemName(currentItem, displayName);

					// Update the item in the target hat's icon data.
					targetHat.getIconData().updateItem(editingIndex, currentItem);

					isModified = true;

				});

				menuManager.addMenu(editorItemPromptMenu);

				editorItemPromptMenu.open();

			}

			else if (event.isShiftRightClick()) {

				deleteSlot(0, slot);

				return MenuClickResult.NEGATIVE;

			}

			return MenuClickResult.NEUTRAL;

		};


		for (int i = 1; i < 28; i++) {

			setAction(getNormalIndex(i, 10, 2), editAction);

		}

		// Current Items.
		List<ItemStackTemplate> items = targetHat.getIconData().getItems();
		for (int i = 1; i < items.size(); i++) {

			ItemStackTemplate itemTemplate = items.get(i);
			String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.capitalizeFirstLetter(itemTemplate.getMaterial().toString().toLowerCase());
			ItemStack item = ItemUtil.createItem(itemTemplate.getMaterial(), 1, itemTemplate.getDurability());

			int index = getNormalIndex(i, 10, 2);

			// Convert the display name to TextComponent using TrueOG Utils for color codes.
			TextComponent nameComponent = Utils.legacySerializerAnyCase(displayName);

			// Parse and process the description if it's a List<String>, converting it to List<TextComponent>.
			List<TextComponent> descriptionComponent = StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ICON_DESCRIPTION.getValue())
					.stream()
					.filter(component -> component instanceof TextComponent) // Filter only TextComponent instances.
					.map(component -> (TextComponent) component) // Cast Component to TextComponent.
					.collect(Collectors.toList());

			// Safely convert List<TextComponent> to List<Component> without unchecked cast.
			List<Component> description = descriptionComponent.stream()
					.map(textComponent -> (Component) textComponent) // Cast TextComponent to Component.
					.collect(Collectors.toList());

			// Call ItemUtil.setNameAndDescription with the correct types.
			ItemUtil.setNameAndDescription(item, nameComponent, description);

			setItem(0, index, item);

		}

	}

	@Override
	public void onClose(boolean forced) {

		if (isModified) {

			core.getDatabase().saveMetaData(editorManager.getMenuName(), targetHat, DataType.ICON, 0);

			isModified = false;

		}

	}

	@Override
	public void onTick(int ticks) {

		IconData iconData = targetHat.getIconData();

		if (iconData == null) {

			return;

		}

		ItemStackTemplate itemTemplate = iconData.getNextItem(ticks);
		ItemUtil.setItemType(getItem(0, 48), itemTemplate.getMaterial(), itemTemplate.getDurability());

	}

	@Override
	public void deleteSlot(int page, int slot) {

		super.deleteSlot(page, slot);

		// Remove the material in this slot.
		targetHat.getIconData().removeItem(getClampedIndex(slot, 10, 2));

		isModified = true;

	}

}