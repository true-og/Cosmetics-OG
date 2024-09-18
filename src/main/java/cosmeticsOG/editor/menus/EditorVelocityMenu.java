package cosmeticsOG.editor.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.editor.menus.EditorOffsetMenu.VectorAxis;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.properties.ItemStackData;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.util.ItemUtil;

public class EditorVelocityMenu extends AbstractStaticMenu {

	private final EditorMenuManager editorManager;
	private final int particleIndex;
	private final MenuCallback callback;

	public EditorVelocityMenu(CosmeticsOG core, EditorMenuManager menuManager, Player owner, int particleIndex, MenuCallback callback) {

		super(core, menuManager, owner);

		this.editorManager = menuManager;
		this.particleIndex = particleIndex;
		this.callback = callback;
		this.inventory = Bukkit.createInventory(null, 27, Utils.legacySerializerAnyCase(Message.EDITOR_VELOCITY_MENU_TITLE.getValue()));

		build();

	}

	@Override
	protected void build() {

		Hat targetHat = editorManager.getTargetHat();
		ItemStackData itemStackData = targetHat.getParticleData(particleIndex).getItemStackData();
		Vector velocity = itemStackData.getVelocity();

		// X Offset.
		ItemStack xItem = ItemUtil.createItem(CompatibleMaterial.REPEATER.getMaterial(), 1, Message.EDITOR_VELOCITY_MENU_SET_VELOCITY_X.getValue());
		EditorLore.updateVectorDescription(xItem, velocity, Message.EDITOR_OFFSET_MENU_OFFSET_X_DESCRIPTION);
		setButton(14, xItem, (event, slot) -> {

			return updateVelocity(event, targetHat, VectorAxis.X);

		});

		// Y Offset.
		ItemStack yItem = ItemUtil.createItem(CompatibleMaterial.REPEATER.getMaterial(), 1, Message.EDITOR_VELOCITY_MENU_SET_VELOCITY_Y.getValue());
		EditorLore.updateVectorDescription(yItem, velocity, Message.EDITOR_OFFSET_MENU_OFFSET_Y_DESCRIPTION);
		setButton(15, yItem, (event, slot) -> {

			return updateVelocity(event, targetHat, VectorAxis.Y);

		});

		// Z Offset.
		ItemStack zItem = ItemUtil.createItem(CompatibleMaterial.REPEATER.getMaterial(), 1, Message.EDITOR_VELOCITY_MENU_SET_VELOCITY_Z.getValue());
		EditorLore.updateVectorDescription(zItem, velocity, Message.EDITOR_OFFSET_MENU_OFFSET_Z_DESCRIPTION);
		setButton(16, zItem, (event, slot) -> {

			return updateVelocity(event, targetHat, VectorAxis.Z);

		});

		// Back.
		setButton(10, backButtonItem, backButtonAction);

	}

	@Override
	public void onClose(boolean forced) {

		if (! forced) {

			callback.onCallback();

		}

	}

	@Override
	public void onTick(int ticks) {}

	public MenuClickResult updateVelocity (MenuClickEvent event, Hat hat, VectorAxis axis) {

		double normalClick    = event.isLeftClick() ? 0.1f : -0.1f;
		double shiftClick     = event.isShiftClick() ? 10 : 1;
		double modifier       = normalClick * shiftClick;
		boolean isMiddleClick = event.isMiddleClick();

		ItemStackData data = hat.getParticleData(particleIndex).getItemStackData();
		Vector velocity = data.getVelocity();

		switch (axis) {
		case X:

			double vx = !isMiddleClick ? velocity.getX() + modifier : 0;

			data.setVelocityX(vx);

			break;
		case Y:

			double vy = !isMiddleClick ? velocity.getY() + modifier : 0;

			data.setVelocityY(vy);

			break;
		case Z:

			double vz = !isMiddleClick ? velocity.getZ() + modifier : 0;

			data.setVelocityZ(vz);

			break;
		}

		EditorLore.updateVectorDescription(getItem(14), velocity, Message.EDITOR_OFFSET_MENU_OFFSET_X_DESCRIPTION);
		EditorLore.updateVectorDescription(getItem(15), velocity, Message.EDITOR_OFFSET_MENU_OFFSET_Y_DESCRIPTION);
		EditorLore.updateVectorDescription(getItem(16), velocity, Message.EDITOR_OFFSET_MENU_OFFSET_Z_DESCRIPTION);

		if (event.isMiddleClick()) {

			return MenuClickResult.NEUTRAL;

		}

		else {

			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;

		}

	}

}