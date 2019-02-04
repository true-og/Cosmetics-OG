package com.mediusecho.particlehats.editor.menus;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.util.ItemUtil;

public class EditorSlotMenu extends EditorMenu {
	
	private final EditorBaseMenu editorBaseMenu;
	private final int size;
	
	public EditorSlotMenu(Core core, Player owner, MenuBuilder menuBuilder) 
	{
		super(core, owner, menuBuilder, true);
		
		editorBaseMenu = menuBuilder.getEditingMenu();
		size = editorBaseMenu.getInventory().getSize();
		
		inventory = Bukkit.createInventory(null, size, Message.EDITOR_SLOT_MENU_TITlE.getValue());
		buildMenu();
	}

	@Override
	protected void buildMenu() 
	{
		EditorBaseMenu editorBaseMenu = menuBuilder.getEditingMenu();
		int targetSlot = menuBuilder.getTargetSlot();
		
//		for (int i = 0; i < size; i++)
//		{
//			Hat hat = editorBaseMenu.getHat(i);
//			if (hat != null) {
//				inventory.setItem(i, ItemUtil.createItem(hat.getMaterial(), 1));
//			}
//		}
		
		//inventory.setContents(editorBaseMenu.getInventory().getContents());
		
		final EditorAction cancelAction = (event, slot) ->
		{
			menuBuilder.goBack();
			return true;
		};
		
		final EditorAction selectAction = (event, slot) ->
		{
			editorBaseMenu.changeSlots(menuBuilder.getTargetSlot(), slot, false);
			menuBuilder.goBack();
			return true;
		};
		
		final EditorAction swapAction = (event, slot) ->
		{
			editorBaseMenu.changeSlots(menuBuilder.getTargetSlot(), slot, true);
			menuBuilder.goBack();
			return true;
		};
		
		for (int i = 0; i < size; i++)
		{
			ItemStack item;
			Hat hat = editorBaseMenu.getHat(i);
			
			if (hat != null) {
				item = ItemUtil.createItem(hat.getMaterial(), 1);
			}
			
			else {
				item = ItemUtil.createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, Message.EDITOR_SLOT_MENU_SELECT);
			}
			
			String displayName = Message.EDITOR_SLOT_MENU_SELECT.getValue();
			if (i  == targetSlot) 
			{
				item.setType(Material.NETHER_STAR);
				displayName = Message.EDITOR_SLOT_MENU_CANCEL.getValue();
				setAction(i, cancelAction);
			}
			
			else if (editorBaseMenu.getHat(i) != null)
			{
				displayName = Message.EDITOR_SLOT_MENU_SWAP.getValue();
				setAction(i, swapAction);
			}
			
			else {
				setAction(i, selectAction);
			}
			
			ItemUtil.setItemName(item, displayName);
			ItemUtil.setItemDescription(item, Arrays.asList());
			
			inventory.setItem(i, item);
		}
	}

}