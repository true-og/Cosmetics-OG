package com.mediusecho.particlehats.editor;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.managers.SettingsManager;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.MenuState;
import com.mediusecho.particlehats.util.ItemUtil;

public abstract class EditorMenu {

	protected final Core core;
	protected final Player owner;
	protected final MenuBuilder menuBuilder;
	
	// Let's us know that this menu only responds to clicks inside its own inventory
	// Some menus will need to respond to clicks in the players inventory so we add this
	protected final boolean localOnly;
	
	protected final Map<Integer, EditorAction> actions;
	protected final Map<Integer, Hat> hats;
	protected Inventory inventory;
	
	protected final ItemStack mainMenuButton;
	protected final ItemStack backButton;
	protected final EditorAction backAction;
	
	public EditorMenu (Core core, Player owner, final MenuBuilder menuBuilder, final boolean localOnly)
	{
		this.core = core;
		this.owner = owner;
		this.menuBuilder = menuBuilder;	
		this.localOnly = localOnly;
		
		actions = new HashMap<Integer, EditorAction>();
		hats = new HashMap<Integer, Hat>();
		
		mainMenuButton = ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_MAIN_MENU.getValue());
		backButton = ItemUtil.createItem(Material.NETHER_STAR, Message.EDITOR_MISC_GO_BACK.getValue());
		
		backAction = (event, slot) ->
		{
			menuBuilder.goBack();
			return true;
		};
	}

	/**
	 * Handle clicking inside this menu
	 * @param event
	 * @param clickedName
	 * @param inMenu
	 * @return
	 */
	public boolean onClick (InventoryClickEvent event, final int slot, final boolean inMenu)
	{
		if (inMenu)
		{
			EditorAction action = getAction(slot);
			if (action != null) {
				return action.onClick(new EditorClickEvent(event), slot);
			}
		}
		
		else {
			return onClickOutside(event, slot);
		}
		return false;
	}
	
	/**
	 * Gets called any time the player is clicking outside of a menu
	 * @param event
	 * @param slot
	 * @return
	 */
	public boolean onClickOutside (InventoryClickEvent event, final int slot) { return true; }
	
	/**
	 * Builds this menu
	 */
	protected abstract void buildMenu ();
	
	/**
	 * Called before the menu is closed
	 */
	protected void onClose () {}
	
	/**
	 * Lets us dynamically update this menu
	 */
	protected void onTick (int ticks) {}
	
	/**
	 * Returns the EditorAction in this slot
	 * @param slot
	 * @return
	 */
	protected EditorAction getAction (int slot) {
		return actions.get(slot);
	}
	
	/**
	 * Set the EditorAction for this slot
	 * @param slot
	 * @param action
	 */
	protected void setAction (int slot, EditorAction action) {
		actions.put(slot, action);
	}
	
	/**
	 * Returns the item found at this slot
	 * @param slot
	 * @return
	 */
	protected ItemStack getItem (int slot) {
		return inventory.getItem(slot);
	}
	
	/**
	 *
	 * @param slot
	 * @param item
	 */
	protected void setItem (int slot, ItemStack item) {
		inventory.setItem(slot, item);
	}
	
	/**
	 * Sets this menus action and item for the given slot
	 * @param slot
	 * @param action
	 * @param item
	 */
	protected void setButton (int slot, ItemStack item, EditorAction action)
	{
		setAction(slot, action);
		setItem(slot, item);
	}
	
	/**
	 * Returns the Hat object found at this slot
	 * @param slot
	 * @return
	 */
	public Hat getHat (int slot) {
		return hats.get(slot);
	}
	
	/**
	 * Sets the hat that belongs in this slot
	 * @param slot
	 * @param hat
	 */
	public void setHat (int slot, Hat hat) {
		hats.put(slot, hat);
	}
	
	/**
	 * Returns whether this menus listens for clicks outside of its inventory
	 * @return
	 */
	public boolean isLocalOnly () {
		return localOnly;
	}
	
	/**
	 * Opens this menu
	 */
	public void open ()
	{
		menuBuilder.setOwnerState(MenuState.SWITCHING);
		owner.openInventory(inventory);
	}
	
	/**
	 * Returns an index relative to 0 starting at the startingIndex
	 * @param slot Slot in inventory
	 * @param startingIndex Where to start clamping in the inventory
	 * @param offset How many slots to ignore in each row
	 * @return
	 */
	protected int getClampedIndex (int slot, int startingIndex, int offset) {
		return Math.max((slot - (((slot / 9) - 1) * offset) - startingIndex), 0);
	}
	
	/**
	 * 
	 * @param slot Clamped index
	 * @param startingIndex Which slot 0 is relative to
	 * @param offset How many slots are ignored in each row
	 * @return
	 */
	protected int getNormalIndex (int slot, int startingIndex, int offset) {
		return (slot + ((slot / (9 - offset)) * offset) + startingIndex);
	}
	
	protected int getWrappedIndex (int slot, int startingIndex, int offset) {
		return getNormalIndex(getClampedIndex(slot, startingIndex, offset), startingIndex, offset);
	}
	
	/**
	 * Plays the EDITOR_SOUND_ID found in config.yml
	 */
	public void playSound ()
	{
		if (SettingsManager.EDITOR_SOUND_ENABLED.getBoolean())
		{
			Sound sound = SettingsManager.EDITOR_SOUND_ID.getSound();
			if (sound != null)
			{
				float volume = SettingsManager.EDITOR_SOUND_VOLUME.getFloat();
				float pitch = SettingsManager.EDITOR_SOUND_PITCH.getFloat();
				
				owner.playSound(owner.getLocation(), sound, volume, pitch);
			}
		}
	}
	
	@FunctionalInterface
	protected interface EditorAction {
		public boolean onClick (EditorClickEvent event, int slot);
	}
	
	@FunctionalInterface
	protected interface EditorItemCallback {
		public void onSelect (ItemStack item);
	}
	
	protected class EditorClickEvent
	{
		private final InventoryClickEvent event;
		
		private final boolean isMiddleClick;
		private final boolean isShiftLeftClick;
		private final boolean isShiftRightClick;
		
		public EditorClickEvent (final InventoryClickEvent event)
		{
			this.event = event;
			
			isShiftLeftClick  = event.isLeftClick() && event.isShiftClick();
			isShiftRightClick = event.isRightClick() && event.isShiftClick();
			isMiddleClick     = event.getClick().equals(ClickType.MIDDLE);
		}
		
		/**
		 * Get this EditorClickEvent's InventoryClickEvent
		 * @return
		 */
		public InventoryClickEvent getEvent () {
			return event;
		}
		
		/**
		 * Returns true if this event is a left click
		 * @return
		 */
		public boolean isLeftClick () {
			return event.isLeftClick();
		}
		
		/**
		 * Returns true if this event is a right click
		 * @return
		 */
		public boolean isRightClick () {
			return event.isRightClick();
		}
		
		/**
		 * Returns true if this event is a shift click
		 * @return
		 */
		public boolean isShiftClick () {
			return event.isShiftClick();
		}
		
		/**
		 * Returns true if this event is a left shift click
		 * @return
		 */
		public boolean isShiftLeftClick () {
			return isShiftLeftClick;
		}
		
		/**
		 * Returns true if this event is a right shift click
		 * @return
		 */
		public boolean isShiftRightClick () {
			return isShiftRightClick;
		}
		
		/**
		 * Returns true if this event is a middle click
		 * @return
		 */
		public boolean isMiddleClick () {
			return isMiddleClick;
		}
	}
}