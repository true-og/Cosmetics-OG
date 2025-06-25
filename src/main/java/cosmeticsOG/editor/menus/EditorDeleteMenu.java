package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.locale.Message;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.ui.MenuManager;
import cosmeticsOG.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditorDeleteMenu extends AbstractStaticMenu {

    private final EditorBaseMenu editorBaseMenu;

    public EditorDeleteMenu(CosmeticsOG core, MenuManager menuManager, Player owner, EditorBaseMenu editorBaseMenu) {

        super(core, menuManager, owner);

        this.editorBaseMenu = editorBaseMenu;
        this.inventory = Bukkit.createInventory(
                null, 27, Utils.legacySerializerAnyCase(Message.EDITOR_DELETE_MENU_TITLE.getValue()));

        build();
    }

    @Override
    protected void build() {

        ItemStack yesItem = ItemUtil.createItem(
                CompatibleMaterial.ROSE_RED.getMaterial(), 1, Message.EDITOR_DELETE_MENU_YES.getValue());
        setButton(12, yesItem, (event, slot) -> {
            core.getDatabase().deleteMenu(editorBaseMenu.getMenuInventory().getName());

            menuManager.closeInventory();

            return MenuClickResult.NEUTRAL;
        });

        ItemStack noItem = ItemUtil.createItem(Material.COAL, 1, Message.EDITOR_DELETE_MENU_NO.getValue());
        setButton(14, noItem, (event, slot) -> {
            menuManager.closeCurrentMenu();

            return MenuClickResult.NEUTRAL;
        });
    }

    @Override
    public void onClose(boolean forced) {}

    @Override
    public void onTick(int ticks) {}
}
