package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.compatibility.CompatibleSound;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.ui.MenuInventory;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.StringUtil;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditorTransferMenu extends AbstractStaticMenu {

    private final EditorMenuManager editorManager;
    private final MenuInventory menuInventory;
    List<TextComponent> textComponents = StringUtil
            .parseDescription(Message.EDITOR_MOVE_MENU_MOVE_DESCRIPTION.getValue()).stream()
            .map(component -> (TextComponent) component) // Convert list of Components to list of TextComponents.
            .collect(Collectors.toList());

    private final ItemStack emptyItem = ItemUtil.createItem(
            CompatibleMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.getMaterial(), 1, Message.EDITOR_MOVE_MENU_MOVE.getValue(),
            textComponents);

    private final String menuName;

    public EditorTransferMenu(CosmeticsOG core, EditorMenuManager menuManager, Player owner, String menuName) {

        super(core, menuManager, owner);

        this.editorManager = menuManager;
        this.menuName = menuName;
        this.menuInventory = core.getDatabase().loadInventory(menuName, core.getPlayerState(owner));
        this.inventory = Bukkit.createInventory(null, menuInventory.getSize(), Utils.legacySerializerAnyCase(
                EditorLore.getTrimmedMenuTitle(menuInventory.getTitle().content(), Message.EDITOR_MOVE_MENU_TITLE)));

        build();

    }

    @Override
    protected void build() {

        final MenuAction moveAction = (event, slot) -> {

            if (event.isRightClick()) {

                menuManager.closeCurrentMenu();

                return MenuClickResult.NEUTRAL;

            }

            int currentSlot = editorManager.getBaseHat().getSlot();

            core.getDatabase().moveHat(null, editorManager.getBaseHat(), editorManager.getMenuName(), menuName,
                    currentSlot, slot, false);

            editorManager.getEditingMenu().removeButton(currentSlot);
            editorManager.returnToBaseMenu();

            return MenuClickResult.NEUTRAL;

        };

        final MenuAction cancelAction = (event, slot) -> {

            if (event.isRightClick()) {

                menuManager.closeCurrentMenu();

                return MenuClickResult.NEUTRAL;

            } else {

                CompatibleSound.ENTITY_VILLAGER_NO.play(owner, 1.0f, 1.0f);

                return MenuClickResult.NONE;

            }

        };

        for (int i = 0; i < menuInventory.getSize(); i++) {

            ItemStack item = menuInventory.getItem(i);
            if (item == null) {

                setButton(i, emptyItem, moveAction);

            } else {

                ItemUtil.setNameAndDescription(item,
                        Utils.legacySerializerAnyCase(Message.EDITOR_MOVE_MENU_OCCUPIED.getValue()),
                        StringUtil.parseDescription(Message.EDITOR_MOVE_MENU_OCCUPIED_DESCRIPTION.getValue()));

                setButton(i, item, cancelAction);

            }

        }

    }

    @Override
    public void onClose(boolean forced) {

    }

    @Override
    public void onTick(int ticks) {

    }

}
