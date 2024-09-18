package cosmeticsOG.editor.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.ui.AbstractListMenu;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.MathUtil;
import cosmeticsOG.util.StringUtil;

public class EditorPotionMenu extends AbstractListMenu {

	private final Hat targetHat;
	private final MenuCallback callback;

	private final String menuTitle = Message.EDITOR_POTION_MENU_TITLE.getValue();
	private final String potionTitle = Message.EDITOR_POTION_MENU_POTION_TITLE.getValue();
	private final String potionSelected = Message.EDITOR_POTION_MENU_POTION_DESCRIPTION.getValue();

	private final List<String> potionBlacklist = Arrays.asList(
			"CONFUSION",
			"HARM",
			"POISON",
			"WEAKNESS",
			"WITHER",
			"UNLUCK",
			"HUNGER",
			"BAD_OMEN"
			);

	private List<PotionEffectType> supportedPotions;
	private Map<Integer, PotionEffectType> potions;

	public EditorPotionMenu(CosmeticsOG core, EditorMenuManager menuManager, Player owner, MenuCallback callback) {

		super(core, menuManager, owner, false);

		this.targetHat = menuManager.getBaseHat();
		this.callback = callback;
		this.supportedPotions = new ArrayList<PotionEffectType>();
		this.potions = new HashMap<Integer, PotionEffectType>();

		boolean useBlacklist = SettingsManager.EDITOR_SHOW_BLACKLISTED_POTIONS.getBoolean();
		for (PotionEffectType potion : PotionEffectType.values()) {

			if (potion == null) {

				continue;

			}

			if (! useBlacklist && potionBlacklist.contains(potion.getName())) {

				continue;

			}

			supportedPotions.add(potion);

		}

		this.totalPages = MathUtil.calculatePageCount(supportedPotions.size(), 28);

		build();

	}

	@Override
	public void insertEmptyItem() {}

	@Override
	public void removeEmptyItem() {}

	@Override
	protected void build() {

		for (int i = 0; i < totalPages; i++) {

			String title = menuTitle.replace("{1}", Integer.toString(i + 1)).replace("{2}", Integer.toString(totalPages));
			Inventory menu = Bukkit.createInventory(null, 54, Utils.legacySerializerAnyCase(title));

			menu.setItem(49, backButtonItem);

			// Next page.
			if ((i + 1) < totalPages) {

				menu.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE.getMaterial(), 1, Message.EDITOR_MISC_NEXT_PAGE.getValue()));

			}

			// Previous page.

			if ((i + 1) > 1) {

				menu.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE.getMaterial(), 1, Message.EDITOR_MISC_PREVIOUS_PAGE.getValue()));

			}

			// Potion strength.
			menu.setItem(52, ItemUtil.createItem(Material.GHAST_TEAR, 1, Message.EDITOR_POTION_MENU_SET_STRENGTH.getValue()));

			EditorLore.updatePotionStrengthDescription(menu.getItem(52), targetHat.getPotion());

			setMenu(i, menu);

		}

		MenuAction selectAction = (event, slot) -> {

			int index = getClampedIndex(slot, 10, 2) + (currentPage * 28);
			if (potions.containsKey(index))	{

				targetHat.setPotion(potions.get(index), targetHat.getPotionAmplifier());

				menuManager.closeCurrentMenu();

			}

			return MenuClickResult.NEUTRAL;

		};

		for (int i = 0; i < 28; i++) {

			setAction(getNormalIndex(i, 10, 2), selectAction);

		}

		setAction(49, backButtonAction);

		// Previous page.
		setAction(48, (clickEvent, slot) -> {

			currentPage--;

			open();

			return MenuClickResult.NEUTRAL;

		});

		// Next page.
		setAction(50, (clickEvent, slot) -> {

			currentPage++;

			open();

			return MenuClickResult.NEUTRAL;

		});

		setAction(52, (event, slot) -> {

			int strength = 1;
			PotionEffect pe = targetHat.getPotion();
			if (pe != null) {

				strength = pe.getAmplifier();
				strength += event.isLeftClick() ? 1 : -1;

				targetHat.setPotionAmplifier(strength);

			}

			ItemStack item = menus.get(currentPage).getItem(52);
			EditorLore.updatePotionStrengthDescription(item, targetHat.getPotion());

			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;

		});

		PotionEffect currentPotion = targetHat.getPotion();
		PotionEffectType currentType = null;

		if (currentPotion != null) {

			currentType = currentPotion.getType();

		}

		String[] selectInfo = StringUtil.parseValue(potionSelected, "1");
		String[] selectedInfo = StringUtil.parseValue(potionSelected, "2");	

		int globalIndex = 0;
		int index = 0;
		int page = 0;

		for (PotionEffectType potion : supportedPotions) {

			String name = StringUtil.capitalizeFirstLetter(potion.getName().toLowerCase());
			ItemStack item = ItemUtil.createItem(Material.POTION, 1, potionTitle.replace("{1}", name));

			if (currentType != null && currentType.equals(potion)) {

				ItemUtil.highlightItem(item);

				String description = potionSelected.replace(selectInfo[0], "").replace(selectedInfo[0], selectedInfo[1]);

				ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));

			}
			else {

				String description = potionSelected.replace(selectInfo[0], selectInfo[1]).replace(selectedInfo[0], "");

				ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));

			}

			menus.get(page).setItem(getNormalIndex(index, 10, 2), item);
			potions.put(globalIndex++, potion);

			index++;

			if (index % 28 == 0) {

				index = 0;

				page++;
			}

		}

	}

	@Override
	public void onClose(boolean forced) {

		if (! forced) {

			callback.onCallback();

		}

	}

	@Override
	public void onTick(int ticks) {}

}