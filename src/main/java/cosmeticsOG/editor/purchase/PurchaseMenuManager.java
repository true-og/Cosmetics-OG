package cosmeticsOG.editor.purchase;

import org.bukkit.entity.Player;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.editor.purchase.menus.PurchaseEditorMainMenu;
import cosmeticsOG.editor.purchase.menus.PurchaseEditorSettingsMenu;

public class PurchaseMenuManager extends EditorMenuManager {

	public PurchaseMenuManager(CosmeticsOG core, Player owner) 
	{
		super(core, owner);
	}

	@Override
	public void openMainMenu ()
	{
		PurchaseEditorMainMenu purchaseEditorMainMenu = new PurchaseEditorMainMenu(core, this, owner);
		addMenu(purchaseEditorMainMenu);
		purchaseEditorMainMenu.open();
	}

	@Override
	public void openSettingsMenu ()
	{
		PurchaseEditorSettingsMenu purchaseEditorSettingsMenu = new PurchaseEditorSettingsMenu(core, this, owner);
		addMenu(purchaseEditorSettingsMenu);
		purchaseEditorSettingsMenu.open();
	}

}