package com.mediusecho.particlehats.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.database.type.mysql.MySQLDatabase;
import com.mediusecho.particlehats.editor.EditorLore;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleLocation;
import com.mediusecho.particlehats.particles.properties.ParticleMode;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.MathUtil;

public class EditorMainMenu extends EditorMenu {
	
	// These will let us extend this class to the node editor
	protected int particleItemSlot = 40;
	protected int trackingItemSlot = 29;
	
	public EditorMainMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder, true);
		
		inventory = Bukkit.createInventory(null, 54, Message.EDITOR_MAIN_MENU_TITLE.getValue());
		buildMenu();
	}
	
	@Override
	public void onClose ()
	{
		Hat hat = menuBuilder.getTargetHat();
		if (hat.isModified())
		{
			MySQLDatabase database = (MySQLDatabase)core.getDatabase();
			String sqlQuery = hat.getSQLUpdateQuery();
			
			Core.log("saving hat with query: " + sqlQuery);
			
			database.saveIncremental(menuBuilder.getEditingMenu().getName(), menuBuilder.getTargetSlot(), hat.getSQLUpdateQuery());
			hat.clearPropertyChanges();
		}
	}

	@Override
	protected void buildMenu() 
	{
		Hat targetHat = menuBuilder.getTargetHat();
		
		// Main Menu
		setItem(46, mainMenuButton);
		setAction(46, (clickEvent, slot) ->
		{
			menuBuilder.goBack();
			return true;
		});
		
		// Equip
		setButton(52, ItemUtil.createItem(Material.DIAMOND_HELMET, Message.EDITOR_MISC_EQUIP), (event, slot) ->
		{
			return true;
		});
		
		// Type
		setButton(10, ItemUtil.createItem(Material.CYAN_DYE, Message.EDITOR_MAIN_MENU_SET_TYPE), (event, slot) ->
		{
			return true;
		});
		
		// Location
		ItemStack locationItem = ItemUtil.createItem(Material.CLAY_BALL, Message.EDITOR_MAIN_MENU_SET_LOCATION);
		EditorLore.updateLocationDescription(locationItem, targetHat.getLocation(), Message.EDITOR_MAIN_MENU_LOCATION_DESCRIPTION);
		setButton(11, locationItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int locationID = MathUtil.wrap(targetHat.getLocation().getID() + increment, ParticleLocation.values().length, 0);
			final ParticleLocation location = ParticleLocation.fromId(locationID);
			
			targetHat.setLocation(location);
			EditorLore.updateLocationDescription(getItem(11), location, Message.EDITOR_MAIN_MENU_LOCATION_DESCRIPTION);
			return true;
		});
		
		// Meta
		setButton(13, ItemUtil.createItem(Material.SIGN, Message.EDITOR_MAIN_MENU_SET_META), (event, slot) ->
		{
			return true;
		});
		
		// Price
		ItemStack priceItem = ItemUtil.createItem(Material.GOLD_NUGGET, Message.EDITOR_MAIN_MENU_SET_PRICE);
		EditorLore.updatePriceDescription(priceItem, targetHat.getPrice(), Message.EDITOR_MAIN_MENU_PRICE_DESCRIPTION);
		setButton(15, priceItem, (event, slot) ->
		{
			final int increment = (event.isLeftClick() ? 1 : -1) * (event.isShiftClick() ? 10 : 1);
			final int price = (int) MathUtil.clamp(targetHat.getPrice() + increment, 0, 2000000000);
			
			targetHat.setPrice(price);
			EditorLore.updatePriceDescription(getItem(15), price, Message.EDITOR_MAIN_MENU_PRICE_DESCRIPTION);
			return true;
		});
		
		// Speed
		ItemStack speedItem = ItemUtil.createItem(Material.SUGAR, Message.EDITOR_MAIN_MENU_SET_SPEED);
		EditorLore.updateIntegerDescription(speedItem, targetHat.getSpeed(), Message.EDITOR_MAIN_MENU_SPEED_DESCRIPTION);
		setButton(16, speedItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int speed = (int) MathUtil.clamp(targetHat.getSpeed() + increment, 0, 10);
			
			targetHat.setSpeed(speed);
			EditorLore.updateIntegerDescription(getItem(16), speed, Message.EDITOR_MAIN_MENU_SPEED_DESCRIPTION);
			return true;
		});
		
		// Action
		ItemStack actionItem = ItemUtil.createItem(Material.GUNPOWDER, Message.EDITOR_MAIN_MENU_SET_ACTION);
		EditorLore.updateGenericActionDescription(actionItem, targetHat);
		setButton(19, actionItem, (event, slot) ->
		{
			EditorActionOverviewMenu editorActionOverviewMenu = new EditorActionOverviewMenu(core, owner, menuBuilder, this);
			menuBuilder.addMenu(editorActionOverviewMenu);
			editorActionOverviewMenu.open();
			return true;
		});
		
		// Mode
		ItemStack modeItem = ItemUtil.createItem(Material.ROSE_RED, Message.EDITOR_MAIN_MENU_SET_MODE);
		EditorLore.updateModeDescription(modeItem, targetHat.getMode(), Message.EDITOR_MAIN_MENU_MODE_DESCRIPTION);
		setButton(20, modeItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int modeID = MathUtil.wrap(targetHat.getMode().getID() + increment, ParticleMode.values().length, 0);
			final ParticleMode mode = ParticleMode.fromId(modeID);
			
			targetHat.setMode(mode);
			EditorLore.updateModeDescription(getItem(20), mode, Message.EDITOR_MAIN_MENU_MODE_DESCRIPTION);
			return true;
		});
		
		// Frequency
		ItemStack frequencyItem = ItemUtil.createItem(Material.COMPARATOR, Message.EDITOR_MAIN_MENU_SET_UPDATE_FREQUENCY);
		EditorLore.updateFrequencyDescription(frequencyItem, targetHat.getUpdateFrequency(), Message.EDITOR_MAIN_MENU_UPDATE_FREQUENCY_DESCRIPTION);
		setButton(22, frequencyItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int frequency = (int) MathUtil.clamp(targetHat.getUpdateFrequency() + increment, 1, 63);
			
			targetHat.setUpdateFrequency(frequency);
			EditorLore.updateFrequencyDescription(getItem(22), frequency, Message.EDITOR_MAIN_MENU_UPDATE_FREQUENCY_DESCRIPTION);
			return true;
		});
		
		// Angle
		ItemStack angleItem =  ItemUtil.createItem(Material.SLIME_BALL, Message.EDITOR_MAIN_MENU_SET_ANGLE);
		EditorLore.updateVectorDescription(angleItem, targetHat.getAngle(), Message.EDITOR_MAIN_MENU_VECTOR_DESCRIPTION);
		setButton(24, angleItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorAngleMenu editorAngleMenu = new EditorAngleMenu(core, owner, menuBuilder, this);
				menuBuilder.addMenu(editorAngleMenu);
				editorAngleMenu.open();
			}
			
			else if (event.isShiftRightClick()) 
			{
				targetHat.setAngle(0, 0, 0);
				onAngleChange();
			}
			return true;
		});
		
		// Offset
		ItemStack offsetItem = ItemUtil.createItem(Material.REPEATER, Message.EDITOR_MAIN_MENU_SET_OFFSET);
		EditorLore.updateVectorDescription(offsetItem, targetHat.getOffset(), Message.EDITOR_MAIN_MENU_VECTOR_DESCRIPTION);
		setButton(25, offsetItem, (event, slot) ->
		{
			if (event.isLeftClick())
			{
				EditorOffsetMenu editorOffsetMenu = new EditorOffsetMenu(core, owner, menuBuilder, this);
				menuBuilder.addMenu(editorOffsetMenu);
				editorOffsetMenu.open();
			}
			
			else if (event.isShiftRightClick())
			{
				targetHat.setOffset(0, 0, 0);
				onOffsetChange();
			}
			return true;
		});
		
		// Sound
		ItemStack soundItem = ItemUtil.createItem(Material.MUSIC_DISC_STRAD, Message.EDITOR_MAIN_MENU_SET_SOUND);
		//
		setButton(28, soundItem, (event, slot) ->
		{
			EditorSoundMenu editorSoundMenu = new EditorSoundMenu(core, owner, menuBuilder);
			menuBuilder.addMenu(editorSoundMenu);
			editorSoundMenu.open();
			return true;
		});
		
		// Tracking
		ItemStack trackingItem = ItemUtil.createItem(Material.COMPASS, Message.EDITOR_MAIN_MENU_SET_TRACKING_METHOD); 
		setButton(trackingItemSlot, trackingItem, (event, slot) ->
		{
			return true;
		});
		
		// Count
		ItemStack countItem = ItemUtil.createItem(Material.WHEAT_SEEDS, Message.EDITOR_MAIN_MENU_SET_COUNT);
		EditorLore.updateIntegerDescription(countItem, targetHat.getCount(), Message.EDITOR_MAIN_MENU_COUNT_DESCRIPTION);
		setButton(33, countItem, (event, slot) ->
		{
			final int increment = event.isLeftClick() ? 1 : -1;
			final int count = (int) MathUtil.clamp(targetHat.getCount() + increment, 1, 15);
			
			targetHat.setCount(count);
			EditorLore.updateIntegerDescription(getItem(33), count, Message.EDITOR_MAIN_MENU_COUNT_DESCRIPTION);
			return true;
		});
		
		// Slot
		ItemStack slotItem = ItemUtil.createItem(Material.ITEM_FRAME, Message.EDITOR_MAIN_MENU_SET_SLOT, Message.EDITOR_MAIN_MENU_SLOT_DESCRIPTION);
		setButton(39, slotItem, (event, slot) ->
		{
			EditorSlotMenu editorSlotMenu = new EditorSlotMenu(core, owner, menuBuilder);
			menuBuilder.addMenu(editorSlotMenu);
			editorSlotMenu.open();
			return true;
		});
		
		// Particle
		ItemStack particleItem = ItemUtil.createItem(Material.REDSTONE, "Set Particle");
		setButton(particleItemSlot, particleItem, (event, slot) ->
		{
			EditorParticleSelectionMenu editorParticleMenu = new EditorParticleSelectionMenu(core, owner, menuBuilder);
			menuBuilder.addMenu(editorParticleMenu);
			editorParticleMenu.open();
			return true;
		});
		
		// Icon
		ItemStack iconItem = ItemUtil.createItem(targetHat.getMaterial(), Message.EDITOR_MAIN_MENU_SET_ICON, Message.EDITOR_MAIN_MENU_ICON_DESCRIPTION);
		setButton(41, iconItem, (event, slot) ->
		{
			EditorIconOverviewMenu editorIconOverviewMenu = new EditorIconOverviewMenu(core, owner, menuBuilder, this);
			menuBuilder.addMenu(editorIconOverviewMenu);
			editorIconOverviewMenu.open();
			return true;
		});
	}
	
	/**
	 * Updates our icon material
	 * @param material
	 */
	public void onIconChange (Material material) {
		getItem(41).setType(material);
	}
	
	/**
	 * Updates the main menu to reflect an offset property update
	 */
	public void onOffsetChange () {
		EditorLore.updateVectorDescription(getItem(25), menuBuilder.getTargetHat().getOffset(), Message.EDITOR_MAIN_MENU_VECTOR_DESCRIPTION);
	}
	
	/**
	 * Updates the main menu to reflect an angle property update
	 */
	public void onAngleChange () {
		EditorLore.updateVectorDescription(getItem(24), menuBuilder.getTargetHat().getAngle(), Message.EDITOR_MAIN_MENU_VECTOR_DESCRIPTION);
	}
	
	/**
	 * Updates the main menu to reflect an action property update
	 */
	public void onActionChange () {
		EditorLore.updateGenericActionDescription(getItem(19), menuBuilder.getBaseHat());
	}
}