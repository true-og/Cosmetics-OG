package com.mediusecho.particlehats.database;

import java.util.List;

import com.mediusecho.particlehats.editor.menus.EditorBaseMenu;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.MenuInventory;

public abstract class Database {

	public abstract void onDisable ();
	
	/**
	 * Loads this menu and all its hats
	 * @param menuName
	 * @return
	 */
	public abstract MenuInventory loadInventory (String menuName);
	

	public abstract void saveInventory (EditorBaseMenu menu);
	
	/**
	 * Creates and inserts an empty menu into our database
	 * @param menuName
	 */
	public abstract void createEmptyMenu (String menuName);
	
	/**
	 * Deletes a menu and all it's data
	 * @param menuName
	 */
	public abstract void deleteMenu (String menuName);
	
	/**
	 * Check to see if this menu exists in our database
	 * @param menuName
	 * @return
	 */
	public abstract boolean menuExists (String menuName);
	
	/**
	 * Inserts a new hat entry into the database
	 * @param menuName
	 * @param slot
	 */
	public abstract void createHat (String menuName, int slot);
	
	/**
	 * Loads all data for this hat
	 * @param menuName
	 * @param slot
	 * @param hat
	 */
	public abstract void loadHatData (String menuName, int slot, Hat hat);
	
	/**
	 * Deletes this hat from the database
	 * @param menuName
	 * @param slot
	 */
	public abstract void deleteHat (String menuName, int slot);
	
	public abstract void changeSlot (String menuName, int previousSlot, int newSlot, boolean swapping);
	
	/**
	 * Returns a list of menus that exist in our database
	 * @param forceUpdate Forces the menu cache to be updated
	 * @return
	 */
	public abstract List<String> getMenus (boolean forceUpdate);
}