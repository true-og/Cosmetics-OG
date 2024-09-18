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
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.util.ItemUtil;

public class EditorOffsetMenu extends AbstractStaticMenu {

	protected final EditorMenuManager editorManager;
	protected final MenuCallback callback;

	public EditorOffsetMenu(CosmeticsOG core, EditorMenuManager menuManager, Player owner, MenuCallback callback) {

		super(core, menuManager, owner);

		this.editorManager = menuManager;
		this.callback = callback;
		this.inventory = Bukkit.createInventory(null, 45, Utils.legacySerializerAnyCase(Message.EDITOR_OFFSET_MENU_TITLE.getValue()));

		build();

	}

	@Override
	protected void build() {

		Hat targetHat = editorManager.getTargetHat();
		// X Offset.
		ItemStack xItem = ItemUtil.createItem(CompatibleMaterial.REPEATER.getMaterial(), 1, Message.EDITOR_OFFSET_MENU_SET_OFFSET_X.getValue());
		EditorLore.updateVectorDescription(xItem, targetHat.getOffset(), Message.EDITOR_OFFSET_MENU_OFFSET_X_DESCRIPTION);
		setButton(14, xItem, (event, slot) -> {

			return updateOffset(event, targetHat, VectorAxis.X, false);

		});

		// Y Offset.
		ItemStack yItem = ItemUtil.createItem(CompatibleMaterial.REPEATER.getMaterial(), 1, Message.EDITOR_OFFSET_MENU_SET_OFFSET_Y.getValue());
		EditorLore.updateVectorDescription(yItem, targetHat.getOffset(), Message.EDITOR_OFFSET_MENU_OFFSET_Y_DESCRIPTION);
		setButton(15, yItem, (event, slot) -> {

			return updateOffset(event, targetHat, VectorAxis.Y, false);

		});

		// Z Offset.
		ItemStack zItem = ItemUtil.createItem(CompatibleMaterial.REPEATER.getMaterial(), 1, Message.EDITOR_OFFSET_MENU_SET_OFFSET_Z.getValue());
		EditorLore.updateVectorDescription(zItem, targetHat.getOffset(), Message.EDITOR_OFFSET_MENU_OFFSET_Z_DESCRIPTION);
		setButton(16, zItem, (event, slot) -> {

			return updateOffset(event, targetHat, VectorAxis.Z, false);

		});

		// Random X Offset.
		ItemStack rxItem = ItemUtil.createItem(CompatibleMaterial.COMPARATOR.getMaterial(), 1, Message.EDITOR_OFFSET_MENU_SET_RANDOM_OFFSET_X.getValue());
		EditorLore.updateVectorDescription(rxItem, targetHat.getRandomOffset(), Message.EDITOR_OFFSET_MENU_OFFSET_X_DESCRIPTION);
		setButton(32, rxItem, (event, slot) -> {

			return updateOffset(event, targetHat, VectorAxis.X, true);

		});

		// Random Y Offset.
		ItemStack ryItem = ItemUtil.createItem(CompatibleMaterial.COMPARATOR.getMaterial(), 1, Message.EDITOR_OFFSET_MENU_SET_RANDOM_OFFSET_Y.getValue());
		EditorLore.updateVectorDescription(ryItem, targetHat.getRandomOffset(), Message.EDITOR_OFFSET_MENU_OFFSET_Y_DESCRIPTION);
		setButton(33, ryItem, (event, slot) -> {

			return updateOffset(event, targetHat, VectorAxis.Y, true);

		});

		// Random Z Offset.
		ItemStack rzItem = ItemUtil.createItem(CompatibleMaterial.COMPARATOR.getMaterial(), 1, Message.EDITOR_OFFSET_MENU_SET_RANDOM_OFFSET_Z.getValue());
		EditorLore.updateVectorDescription(rzItem, targetHat.getRandomOffset(), Message.EDITOR_OFFSET_MENU_OFFSET_Z_DESCRIPTION);
		setButton(34, rzItem, (event, slot) -> {

			return updateOffset(event, targetHat, VectorAxis.Z, true);

		});

		// Back.
		setButton(19, backButtonItem, backButtonAction);

	}

	@Override
	public void onClose(boolean forced) {

		if (! forced) {

			callback.onCallback();

		}

	}

	@Override
	public void onTick(int ticks) {}

	private MenuClickResult updateOffset (MenuClickEvent event, Hat hat, VectorAxis axis, boolean random) {

		double normalClick    = event.isLeftClick() ? 0.1f : -0.1f;
		double shiftClick     = event.isShiftClick() ? 10 : 1;
		double modifier       = normalClick * shiftClick;
		boolean isMiddleClick = event.isMiddleClick();

		Vector offset = random ? hat.getRandomOffset() : hat.getOffset();

		int xslot = random ? 32 : 14;
		int yslot = random ? 33 : 15;
		int zslot = random ? 34 : 16;

		switch (axis) {
		case X: {

			double xo = ! isMiddleClick ? offset.getX() + modifier : 0;
			if (random) {

				hat.setRandomOffsetX(xo);

			}
			else {

				hat.setOffsetX(xo);

			}

			break;

		}
		case Y: {

			double yo = ! isMiddleClick ? offset.getY() + modifier : 0;
			if (random) {

				hat.setRandomOffsetY(yo);

			}
			else {

				hat.setOffsetY(yo);

			}

			break;

		}
		case Z: {

			double zo = ! isMiddleClick ? offset.getZ() + modifier : 0;
			if (random) {

				hat.setRandomOffsetZ(zo);

			}
			else {

				hat.setOffsetZ(zo);

			}

			break;

		}}

		EditorLore.updateVectorDescription(getItem(xslot), offset, Message.EDITOR_OFFSET_MENU_OFFSET_X_DESCRIPTION);
		EditorLore.updateVectorDescription(getItem(yslot), offset, Message.EDITOR_OFFSET_MENU_OFFSET_Y_DESCRIPTION);
		EditorLore.updateVectorDescription(getItem(zslot), offset, Message.EDITOR_OFFSET_MENU_OFFSET_Z_DESCRIPTION);

		if (event.isMiddleClick()) {

			return MenuClickResult.NEUTRAL;

		}

		else {

			return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;

		}

	}

	protected enum VectorAxis {

		X,
		Y,
		Z;

	}

}