package com.mediusecho.particlehats.database.type.yaml;

import java.util.List;

import com.mediusecho.particlehats.database.Database;
import com.mediusecho.particlehats.editor.menus.EditorBaseMenu;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.ui.MenuInventory;

public class YamlDatabase extends Database {

	@Override
	public void onDisable() 
	{
		
	}
	
	@Override
	public MenuInventory loadInventory (String menuName) 
	{
		return null;
	}

	@Override
	public List<String> getMenus(boolean forceUpdate) 
	{
		return null;
	}

	@Override
	public void createEmptyMenu(String menuName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveInventory(EditorBaseMenu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createHat(String menuName, int slot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteHat(String menuName, int slot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadHatData(String menuName, int slot, Hat hat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean menuExists(String menuName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void changeSlot(String menuName, int previousSlot, int newSlot, boolean swapping) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteMenu(String menuName) {
		// TODO Auto-generated method stub
		
	}

}