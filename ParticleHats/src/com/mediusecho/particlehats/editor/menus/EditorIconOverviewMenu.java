package com.mediusecho.particlehats.editor.menus;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.type.DatabaseType;
import com.mediusecho.particlehats.database.type.mysql.MySQLDatabase;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.IconData;
import com.mediusecho.particlehats.particles.properties.IconDisplayMode;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorIconOverviewMenu extends EditorMenu {

	private final Hat targetHat;
	private final EditorMainMenu editorMainMenu;
	
	private final ItemStack addItem;
	private final EditorAction addItemAction;
	private final EditorAction editItemAction;
	
	private boolean isModified = false;
	private int editingIndex = 0;
	
	private final Message iconTitle = Message.EDITOR_ICON_MENU_TITLE;
	private final Message iconDescription = Message.EDITOR_ICON_MENU_INFO_DESCRIPTION;
	
	public EditorIconOverviewMenu(Core core, Player owner, MenuBuilder menuBuilder, EditorMainMenu editorMainMenu) 
	{
		super(core, owner, menuBuilder, true);
		this.editorMainMenu = editorMainMenu;
		targetHat = menuBuilder.getBaseHat();
		
		addItem = ItemUtil.createItem(Material.TURTLE_HELMET, Message.EDITOR_ICON_MENU_ADD_ICON);
		addItemAction = (event, slot) ->
		{
			editingIndex = getClampedIndex(slot, 10, 2);
			EditorIconMenu editorIconMenu = new EditorIconMenu(core, owner, menuBuilder, iconTitle, iconDescription, (item) ->
			{
				addItem(slot, item);
			});
			
			menuBuilder.addMenu(editorIconMenu);
			editorIconMenu.open();
			return true;
		};
		
		editItemAction = (event, slot) ->
		{
			editingIndex = getClampedIndex(slot, 10, 2);
			if (event.isLeftClick())
			{
				EditorIconMenu editorIconMenu = new EditorIconMenu(core, owner, menuBuilder, iconTitle, iconDescription, (item) ->
				{
					Material material = item.getType();
					String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(material);
					ItemStack i = getItem(slot);
				
					i.setType(material);
					ItemUtil.setItemName(item, displayName);
					
					targetHat.getIconData().updateMaterial(editingIndex, material);
					isModified = true;
				});
				menuBuilder.addMenu(editorIconMenu);
				editorIconMenu.open();
			}
			
			else if (event.isShiftRightClick()) {
				deleteItem(slot);
			}
			return true;
		};
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_ICON_OVERVIEW_MENU_TITLE.getValue());
		buildMenu();
	}
	
	@Override
	public void onTick (int ticks)
	{
		IconData data = targetHat.getIconData();
		if (data != null)
		{
			Material material = data.getNextMaterial(ticks);
			if (material != null) {
				getItem(48).setType(material);
			}
		}
	}
	
	@Override
	public void onClose ()
	{
		if (isModified)
		{
			DatabaseType databaseType = core.getDatabaseType();
			if (databaseType == DatabaseType.MYSQL)
			{
				MySQLDatabase database = (MySQLDatabase)core.getDatabase();
				database.saveIconData(menuBuilder.getEditingMenu().getName(), targetHat);
			}
		}
	}

	@Override
	protected void buildMenu() 
	{
		setButton(46, backButton, (event, slot) ->
		{
			menuBuilder.goBack();
			return true;
		});
		
		// Set Main Icon
		ItemStack mainItem = ItemUtil.createItem(targetHat.getMaterial(), Message.EDITOR_ICON_MENU_SET_MAIN_ICON);
		setButton(10, mainItem, (event, slot) ->
		{
			editingIndex = 0;
			EditorIconMenu editorIconMenu = new EditorIconMenu(core, owner, menuBuilder, iconTitle, iconDescription, (item) ->
			{
				Material material = item.getType();
				targetHat.setMaterial(material);
				menuBuilder.getEditingMenu().setItemMaterial(menuBuilder.getTargetSlot(), material);
				
				getItem(10).setType(material);
				editorMainMenu.onIconChange(material);
				targetHat.setMaterial(material);
			});
			menuBuilder.addMenu(editorIconMenu);
			editorIconMenu.open();
			return true;
		});
		
		// Preview Icon
		setItem(48, ItemUtil.createItem(Material.SUNFLOWER, Message.EDITOR_ICON_MENU_PREVIEW));
		
		// Display Mode 48
		ItemStack displayItem = ItemUtil.createItem(Material.ROSE_RED, Message.EDITOR_ICON_MENU_SET_DISPLAY_MODE);
		EditorLore.updateDisplayModeDescription(displayItem, targetHat.getIconData().getDisplayMode(), Message.EDITOR_ICON_MENU_DISPLAY_MODE_DESCRIPTION);
		setButton(52, displayItem, (event, slot) ->
		{			
			final int increment = event.isLeftClick() ? 1 : -1;
			final int modeID = MathUtil.wrap(targetHat.getDisplayMode().getID() + increment, ParticleMode.values().length, 0);
			final IconDisplayMode mode = IconDisplayMode.fromId(modeID);
			
			targetHat.setDisplayMode(mode);
			EditorLore.updateDisplayModeDescription(getItem(52), mode, Message.EDITOR_ICON_MENU_DISPLAY_MODE_DESCRIPTION);
			return true;
		});
		
		// Update Frequency
		ItemStack frequencyItem = ItemUtil.createItem(Material.REPEATER, Message.EDITOR_ICON_MENU_SET_UPDATE_FREQUENCY);
		EditorLore.updateFrequencyDescription(frequencyItem, targetHat.getIconUpdateFrequency(), Message.EDITOR_ICON_MENU_UPDATE_FREQUENCY_DESCRIPTION);
		setButton(50, frequencyItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int frequency = (int) MathUtil.clamp(targetHat.getIconUpdateFrequency() + increment, 1, 63);
			
			targetHat.setIconUpdateFrequency(frequency);
			EditorLore.updateFrequencyDescription(getItem(50), frequency, Message.EDITOR_ICON_MENU_UPDATE_FREQUENCY_DESCRIPTION);
			return true;
		});
		
		// Add Item
		List<Material> materials = targetHat.getIconData().getMaterials();
		for (int i = 1; i < materials.size(); i++) 
		{
			Material material = materials.get(i);
			String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.capitalizeFirstLetter(material.toString().toLowerCase());
			
			int index = getNormalIndex(i, 10, 2);
			setItem(index, ItemUtil.createItem(material, displayName, StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ITEM_DESCRIPTION.getValue())));
			setAction(index, editItemAction);
		}
		
		if (materials.size() <= 20) {
			setItem(getNormalIndex(materials.size(), 10, 2), addItem);
		}
		
		// Add Item Action
		for (int i = 1; i < 21; i++) 
		{
			int index = getNormalIndex(i, 10, 2);
			if (getAction(index) == null) {
				setAction(index, addItemAction);
			}
		}
	}
	
	/**
	 * Adds a new material to our IconData
	 * @param slot
	 * @param material
	 */
	private void addItem (int slot, ItemStack item)
	{		
		Material material = item.getType();
		ItemStack i = getItem(slot);
		i.setType(material);
		
		ItemMeta meta = item.getItemMeta();
		if (meta instanceof PotionMeta)
		{
			PotionMeta pm = (PotionMeta)meta;
			pm.addItemFlags(ItemFlag.values());
			i.setItemMeta(pm);
		}
		
		if (meta instanceof SkullMeta)
		{
			SkullMeta sm = (SkullMeta)meta;
			sm.addItemFlags(ItemFlag.values());
			i.setItemMeta(sm);
			
//			if (sm.hasOwner())
//			{
//				OfflinePlayer skullOwner = sm.getOwningPlayer();
//				Core.log(skullOwner.getUniqueId());
//			}
//			
//			else {
//				Core.log(sm.toString());
//			}
			
			Map<String, Object> info = sm.serialize();
			for (Entry<String, Object> entry : info.entrySet())
			{
				String key = entry.getKey();
				Core.log(key);
			}
			Core.log(info.get("internal"));
		}
		
		String displayName = Message.EDITOR_ICON_MENU_ITEM_PREFIX.getValue() + StringUtil.getMaterialName(material);
		ItemUtil.setNameAndDescription(i, displayName, StringUtil.parseDescription(Message.EDITOR_ICON_MENU_ITEM_DESCRIPTION.getValue()));
		
		targetHat.getIconData().addMaterial(material);

		// Change the action to our editing action
		setAction(slot, editItemAction);
		
		// Add a new add action
		int wrappedIndex = getWrappedIndex(slot + 1, 10, 2);
		if (wrappedIndex <= 34) {
			setButton(wrappedIndex, addItem, addItemAction);
		}
		
		isModified = true;
	}
	
	/**
	 * Removes a material at the given slot
	 * @param slot
	 */
	private void deleteItem (int slot)
	{
		int clampedIndex = getClampedIndex(slot, 10, 2);
		
		// Remove the current slot
		setItem(slot, null);
		
		// Remove the material in this slot
		targetHat.getIconData().removeMaterial(clampedIndex);
		
		for (int i = clampedIndex + 1; i < 22; i++) 
		{
			int normalIndex = getNormalIndex(i, 10, 2);
			int shiftedIndex = getNormalIndex(i - 1, 10, 2);
			
			ItemStack item = getItem(normalIndex);
			if (item == null) {
				break;
			}
			
			EditorAction currentAction = getAction(normalIndex);
			setButton(normalIndex, null, null);
			setButton(shiftedIndex, item, currentAction);
			
			if (currentAction == addItemAction) {
				break;
			}
			
			// We've deleted the last element and need to add our new item back in
			if (normalIndex == 34) {
				setButton(34, addItem, addItemAction);
			}
		}
		
		isModified = true;
	}
}