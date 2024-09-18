package cosmeticsOG.ui;

import org.bukkit.Material;

import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.properties.ParticleAction;
import cosmeticsOG.util.ItemUtil;
import net.kyori.adventure.text.Component;

public class PendingPurchaseMenu {

	public static MenuInventory defaultPendingPurchaseInventory;

	static {

		defaultPendingPurchaseInventory = new MenuInventory("", Component.text(Message.PURCHASE_MENU_TITLE.getValue()), 5, null);

		// Confirm Hat.
		Hat confirm = new Hat();
		confirm.setLeftClickAction(ParticleAction.PURCHASE_CONFIRM);
		confirm.setLoaded(true);
		defaultPendingPurchaseInventory.setHat(30, confirm);

		defaultPendingPurchaseInventory.setItem(30, ItemUtil.createItem(Material.DIAMOND, 1, Message.PURCHASE_MENU_CONFIRM.getValue()));

		// Cancel Hat.
		Hat cancel = new Hat();
		cancel.setLeftClickAction(ParticleAction.PURCHASE_DENY);
		cancel.setLoaded(true);
		defaultPendingPurchaseInventory.setHat(32, cancel);

		defaultPendingPurchaseInventory.setItem(32, ItemUtil.createItem(Material.COAL, 1, Message.PURCHASE_MENU_CANCEL.getValue()));

		// Pending Hat.
		Hat pending = new Hat();
		pending.setLeftClickAction(ParticleAction.PURCHASE_ITEM);
		pending.setLoaded(true);

		// Set pending item directly from the Hat.
		defaultPendingPurchaseInventory.setItem(13, pending.getMenuItem());

	}

}