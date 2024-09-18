package cosmeticsOG.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.locale.Message;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.properties.IconData;
import cosmeticsOG.particles.properties.IconData.ItemStackTemplate;
import cosmeticsOG.particles.properties.ParticleAction;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class StaticMenu extends AbstractStaticMenu {

	private final MenuInventory menuInventory;
	private final PlayerState ownerState;

	private final MenuAction hatAction;

	public StaticMenu(CosmeticsOG core, MenuManager menuManager, Player owner, MenuInventory menuInventory) {

		super(core, menuManager, owner);

		this.menuInventory = menuInventory;
		this.ownerState = core.getPlayerState(owner);
		this.inventory = Bukkit.createInventory(null, menuInventory.getSize(), menuInventory.getDisplayTitle());

		hatAction = (event, slot) -> {

			Hat hat = menuInventory.getHat(slot);
			MenuClickResult result = MenuClickResult.NEUTRAL;
			if (hat == null) {

				return MenuClickResult.NONE;

			}

			if (! hat.isLoaded()) {

				core.getDatabase().loadHat(getName(), slot, hat);

			}

			if (hat.playSound(owner)) {

				result = MenuClickResult.NONE;

			}

			if (event.isLeftClick()) {

				hat.getLeftClickAction().onClick(owner, hat, slot, inventory, hat.getLeftClickArgument());

			}
			else if (event.isRightClick()) {

				ParticleAction action = hat.getRightClickAction();
				if (action == ParticleAction.MIMIC) {

					hat.getLeftClickAction().onClick(owner, hat, slot, inventory, hat.getLeftClickArgument());

				}
				else {

					action.onClick(owner, hat, slot, inventory, hat.getRightClickArgument());

				}

			}

			return result;

		};

		build();

	}

	//	@Override
	//	public void open () 
	//	{
		//		menuManager.isOpeningMenu(this);
	//		menuInventory.open(owner);
	//	}

	@Override
	protected void build() {

		Material lockedMaterial = SettingsManager.MENU_LOCKED_ITEM.getMaterial();
		int lockedMaterialDurability = SettingsManager.MENU_LOCKED_ITEM_DAMAGE.getInt();
		TextComponent lockedTitle = Utils.legacySerializerAnyCase(SettingsManager.MENU_LOCKED_ITEM_TITLE.getString());

		List<Hat> equippedHats = core.getPlayerState(owner).getActiveHats();
		for (int i = 0; i < inventory.getSize(); i++) {

			ItemStack item = menuInventory.getItem(i);
			Hat hat = menuInventory.getHat(i);
			if (item == null || hat == null) {

				continue;

			}

			// Check for PURCHASE_ITEM action.
			if (hat.getLeftClickAction() == ParticleAction.PURCHASE_ITEM) {

				Hat pendingPurchase = ownerState.getPendingPurchase();
				if (pendingPurchase != null) {

					inventory.setItem(i, pendingPurchase.getMenuItem());

				}

				continue;

			}

			// Highlight equipped hats.
			if (equippedHats.contains(hat)) {

				ItemUtil.highlightItem(item);

				ItemMeta itemMeta = item.getItemMeta();
				List<Component> lore = itemMeta.lore();
				if (lore == null) {

					lore = new ArrayList<Component>();

				}

				String equippedLore = Message.HAT_EQUIPPED_DESCRIPTION.getValue();
				String[] lineInfo = StringUtil.parseValue(equippedLore, "1");
				if (lore.size() > 0) {

					equippedLore = equippedLore.replace(lineInfo[0], lineInfo[1]);

				}
				else {

					equippedLore = equippedLore.replace(lineInfo[0], "");

				}

				lore.addAll(StringUtil.parseDescription(equippedLore));
				itemMeta.lore(lore);
				item.setItemMeta(itemMeta);

			}

			// Lock hats that aren't equipped if possible.
			else if (hat.canBeLocked() && hat.isLocked()) {

				if (SettingsManager.MENU_LOCK_HATS_WITHOUT_PERMISSION.getBoolean()) {

					ItemUtil.setItemType(item, lockedMaterial, lockedMaterialDurability);
					ItemUtil.setItemName(item, lockedTitle.content());

				}

			}

			inventory.setItem(i, item);

		}

		for (int i = 0; i < inventory.getSize(); i++) {

			setAction(i, hatAction);

		}

	}

	@Override
	public void onClose(boolean forced) {}

	@Override
	public void onTick(int ticks) {

		for (Entry<Integer, Hat> set : menuInventory.getHats().entrySet()) {

			int slot = set.getKey();
			Hat hat = set.getValue();
			if (hat == null || hat.isLocked()) {

				continue;

			}

			IconData iconData = hat.getIconData();
			if (iconData.isLive()) {

				ItemStackTemplate itemTemplate = iconData.getNextItem(ticks);
				ItemUtil.setItemType(inventory.getItem(slot), itemTemplate.getMaterial(), itemTemplate.getDurability());

			}

		}

	}

	@Override
	public String getName () {

		return menuInventory.getName();

	}

	/**
	 * Get all hats in this menu
	 * @return
	 */
	public Map<Integer, Hat> getHats () {

		return menuInventory.getHats();

	}

	public void equipHat(Hat hat) {

		ItemStack item = menuInventory.getItem(hat.getSlot());
		ItemUtil.highlightItem(item);

		ItemMeta itemMeta = item.getItemMeta();

		// Assuming hat.getCachedDescription() returns a List<String>.
		List<String> cachedDescription = hat.getCachedDescription();
		List<Component> lore = new ArrayList<>();

		// Convert each String in cachedDescription to a TextComponent and add to lore.
		for (String line : cachedDescription) {

			lore.add(Component.text(line));

		}

		TextComponent equippedLore = Utils.legacySerializerAnyCase(Message.HAT_EQUIPPED_DESCRIPTION.getValue());

		String[] lineInfo = StringUtil.parseValue(equippedLore.content(), "1");

		// Replace content based on existing lore size.
		String updatedLoreContent = lore.size() > 0 ? equippedLore.content().replace(lineInfo[0], lineInfo[1]) : equippedLore.content().replace(lineInfo[0], "");

		// Update the TextComponent with the new content.
		equippedLore = equippedLore.content(updatedLoreContent);

		// Add the updated TextComponent to the lore.
		lore.add(equippedLore);

		// Set the lore as a List of Components in the ItemMeta.
		itemMeta.lore(lore);

		// Apply the updated ItemMeta back to the ItemStack.
		item.setItemMeta(itemMeta);

		// Update the inventory with the modified item.
		inventory.setItem(hat.getSlot(), item);

	}

	public void unequipHat(Hat hat) {

		// Get the ItemStack from the menu inventory.
		ItemStack item = menuInventory.getItem(hat.getSlot());
		ItemUtil.stripHighlight(item);

		// Convert List<String> from hat.getCachedDescription() to List<Component>.
		List<Component> descriptionComponents = hat.getCachedDescription().stream()
				// Convert each String to a Component.
				.map(Component::text)
				// Add the output to the list.
				.collect(Collectors.toList());

		// Set the ItemStack's description using a List<Component>.
		ItemUtil.setItemDescription(item, descriptionComponents);

		// Update the inventory with the modified ItemStack.
		inventory.setItem(hat.getSlot(), item);

	}

}