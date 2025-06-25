package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.compatibility.CompatibleSound;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.util.ItemUtil;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditorSlotMenu extends AbstractStaticMenu {

    private final EditorMenuManager editorManager;
    private final EditorBaseMenu editorBaseMenu;
    private final boolean cloning;
    private final int size;

    public EditorSlotMenu(
            CosmeticsOG core,
            EditorMenuManager menuManager,
            Player owner,
            EditorBaseMenu editorBaseMenu,
            boolean cloning) {

        super(core, menuManager, owner);

        this.editorManager = menuManager;
        this.editorBaseMenu = editorBaseMenu;
        this.cloning = cloning;
        this.size = editorBaseMenu.getMenuInventory().getSize();
        this.inventory = Bukkit.createInventory(
                null, size, Utils.legacySerializerAnyCase(Message.EDITOR_SLOT_MENU_TITlE.getValue()));

        build();
    }

    @Override
    protected void build() {

        int targetSlot = editorManager.getTargetSlot();
        final MenuAction selectAction = (event, slot) -> {
            if (cloning) {

                editorBaseMenu.cloneHat(targetSlot, slot);

                editorManager.returnToBaseMenu();

                return MenuClickResult.NEUTRAL;
            }

            editorBaseMenu.changeSlots(targetSlot, slot, false);

            menuManager.closeCurrentMenu();

            return MenuClickResult.NEUTRAL;
        };

        final MenuAction swapAction = (event, slot) -> {
            editorBaseMenu.changeSlots(targetSlot, slot, true);

            editorManager.returnToBaseMenu();

            return MenuClickResult.NEUTRAL;
        };

        final MenuAction secretAction = (event, slot) -> {
            CompatibleSound.ENTITY_VILLAGER_NO.play(owner, 0.5f, 1.0f);

            return MenuClickResult.NONE;
        };

        for (int i = 0; i < size; i++) {

            ItemStack item;
            Hat hat = editorBaseMenu.getMenuInventory().getHat(i);
            if (hat != null) {

                item = hat.getItem().clone();

            } else {

                item = ItemUtil.createItem(
                        CompatibleMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.getMaterial(),
                        1,
                        Message.EDITOR_SLOT_MENU_SELECT.getValue());
            }

            String displayName = Message.EDITOR_SLOT_MENU_SELECT.getValue();
            if (i == targetSlot) {

                ItemUtil.setItemType(item, Material.NETHER_STAR, 0);

                displayName = Message.EDITOR_SLOT_MENU_CANCEL.getValue();

                setAction(i, backButtonAction);

            } else if (editorBaseMenu.getMenuInventory().getHat(i) != null) {
                if (!cloning) {

                    displayName = Message.EDITOR_SLOT_MENU_SWAP.getValue();

                    setAction(i, swapAction);

                } else {

                    displayName = Message.EDITOR_SLOT_MENU_OCCUPIED.getValue();

                    setAction(i, secretAction);
                }

            } else {

                setAction(i, selectAction);
            }

            ItemUtil.setItemName(item, displayName);
            ItemUtil.setItemDescription(item, Arrays.asList());

            inventory.setItem(i, item);
        }
    }

    @Override
    public void onClose(boolean forced) {}

    @Override
    public void onTick(int ticks) {}
}
