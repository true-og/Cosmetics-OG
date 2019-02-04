package com.mediusecho.particlehats.editor.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.editor.EditorMenu;
import com.mediusecho.particlehats.editor.MenuBuilder;
import com.mediusecho.particlehats.locale.Message;
import com.mediusecho.particlehats.particles.Hat;
import com.mediusecho.particlehats.particles.properties.ParticleAction;
import com.mediusecho.particlehats.ui.MenuState;
import com.mediusecho.particlehats.util.ItemUtil;
import com.mediusecho.particlehats.util.StringUtil;

public class EditorActionMenu extends EditorMenu {
	
	private final Map<Integer, Inventory> menus;
	private final List<ParticleAction> actions;
	private final int pages;
	private final Hat targetHat;
	
	private int currentPage = 0;
	private boolean isLeftClick = false;
	
	private final EditorAction selectAction;
	
	public EditorActionMenu(Core core, Player owner, MenuBuilder menuBuilder, EditorActionOverviewMenu editorActionOverviewMenu, boolean isLeftClick) 
	{
		super(core, owner, menuBuilder, true);
		
		//this.editorActionOverviewMenu = editorActionOverviewMenu;
		targetHat = menuBuilder.getBaseHat();
		menus = new HashMap<Integer, Inventory>();
		actions = new ArrayList<ParticleAction>();
		pages = (int) Math.ceil(ParticleAction.values().length / 28D);
		this.isLeftClick = isLeftClick;
		
		selectAction = (event, slot) ->
		{
			ParticleAction action = actions.get(this.getClampedIndex(slot, 10, 2));
			if (action != null)
			{
				if (isLeftClick) {
					targetHat.setLeftClickAction(action);
				} else {
					targetHat.setRightClickAction(action);
				}
				
				editorActionOverviewMenu.onActionChange(action, isLeftClick);
				menuBuilder.goBack();
			}
			return true;
		};
		
		buildMenu();
	}
	
	@Override
	public void open ()
	{
		Inventory menu = menus.get(currentPage);
		if (menu != null)
		{
			menuBuilder.setOwnerState(MenuState.SWITCHING);
			owner.openInventory(menu);
		}
	}

	@Override
	protected void buildMenu() 
	{		
		// Setup actions
		setAction(49, backAction);
		
		// Create our pages
		for (int i = 0; i < pages; i++)
		{
			String menuTitle = Message.EDITOR_ACTION_MENU_TITLE.getValue()
					.replace("{1}", isLeftClick ? "Left Click" : "Right Click")
					.replace("{2}", Integer.toString(i + 1)).replace("{3}", Integer.toString(pages));
			Inventory menu = Bukkit.createInventory(null, 54, menuTitle);
			
			menu.setItem(49, backButton);
			
			menus.put(i, menu);
		}
		
		// Fill in our actions
		for (int i = 0; i < 28; i++) {
			setAction(getNormalIndex(i, 10, 2), selectAction);
		}
		
		// Insert out actions
		int index = 0;
		int page = 0;
		for (ParticleAction action : ParticleAction.values())
		{
			// Skip the mimic action if we're selected a left click
			if (action == ParticleAction.MIMIC && isLeftClick) {
				continue;
			}
			
			ItemStack item = ItemUtil.createItem(Material.FIREWORK_STAR, action.getDisplayName());
			String description = Message.EDITOR_ACTION_MENU_ACTION_DESCRIPTION.getValue();
			String[] selectedInfo = StringUtil.parseValue(description, "2");
			String[] selectInfo = StringUtil.parseValue(description, "3");
			
			ParticleAction currentAction = isLeftClick ? targetHat.getLeftClickAction() : targetHat.getRightClickAction();
			if (selectedInfo != null && selectInfo != null)
			{
				if (currentAction.equals(action)) {
					description = description.replace(selectedInfo[0], selectedInfo[1]).replace(selectInfo[0], "");
				}
				
				else {
					description = description.replace(selectInfo[0], selectInfo[1]).replace(selectedInfo[0], "");
				}
			}
			
			// Highlight our item
			if (currentAction.equals(action))
			{
				item.setType(Material.GUNPOWDER);
				ItemUtil.highlightItem(item);
			}
			
			description = description.replace("{1}", action.getDescription());
			ItemUtil.setItemDescription(item, StringUtil.parseDescription(description));
			
			menus.get(page).setItem(getNormalIndex(index++, 10, 2), item);
			actions.add(action);
			
			if (index % 28 == 0)
			{
				index = 0;
				page++;
			}
		}
	}

}