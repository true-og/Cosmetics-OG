package cosmeticsOG.editor.purchase.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.MetaState;
import cosmeticsOG.editor.menus.EditorBaseMenu;
import cosmeticsOG.editor.menus.EditorResizeMenu;
import cosmeticsOG.editor.purchase.PurchaseMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.ui.MenuInventory;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PurchaseEditorSettingsMenu extends AbstractStaticMenu {

    private final PurchaseMenuManager purchaseManager;
    private final EditorBaseMenu editorBaseMenu;
    private final MenuInventory menuInventory;

    public PurchaseEditorSettingsMenu(CosmeticsOG core, PurchaseMenuManager menuManager, Player owner) {

        super(core, menuManager, owner);

        this.purchaseManager = menuManager;
        this.editorBaseMenu = menuManager.getEditingMenu();
        this.menuInventory = editorBaseMenu.getMenuInventory();
        this.inventory = Bukkit.createInventory(null, 36,
                Utils.legacySerializerAnyCase(Message.EDITOR_SETTINGS_MENU_TITLE.getValue()));

        build();

    }

    @Override
    protected void build() {

        // Back.
        setButton(31, backButtonItem, backButtonAction);

        // Set Title.
        ItemStack titleItem = ItemUtil.createItem(CompatibleMaterial.SIGN.getMaterial(), 1,
                Message.EDITOR_SETTINGS_MENU_SET_TITLE.getValue());
        setButton(10, titleItem, (event, slot) -> {

            purchaseManager.getOwnerState().setMetaState(MetaState.MENU_TITLE);
            core.prompt(owner, MetaState.MENU_TITLE);
            menuManager.closeInventory();

            return MenuClickResult.NEUTRAL;

        });

        // Set Size.
        ItemStack sizeItem = ItemUtil.createItem(CompatibleMaterial.COMPARATOR.getMaterial(), 1,
                Message.EDITOR_SETTINGS_MENU_SET_SIZE.getValue());
        setButton(12, sizeItem, (event, slot) -> {

            EditorResizeMenu editorResizeMenu = new EditorResizeMenu(core, purchaseManager, owner, editorBaseMenu);
            menuManager.addMenu(editorResizeMenu);
            editorResizeMenu.open();

            return MenuClickResult.NEUTRAL;

        });

        // Toggle Live Updates.
        ItemStack liveItem = ItemUtil.createItem(Material.LEVER, 1,
                Message.EDITOR_SETTINGS_MENU_TOGGLE_LIVE_MENU.getValue());
        EditorLore.updateBooleanDescription(liveItem, editorBaseMenu.canUpdate(),
                Message.EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION);
        setButton(14, liveItem, (event, slot) -> {

            editorBaseMenu.toggleUpdates();
            EditorLore.updateBooleanDescription(getItem(14), editorBaseMenu.canUpdate(),
                    Message.EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION);

            return MenuClickResult.NEUTRAL;

        });

        // Sync Icons.
        ItemStack syncItem = ItemUtil.createItem(CompatibleMaterial.CONDUIT.getMaterial(),
                Message.EDITOR_SETTINGS_MENU_SYNC_ICONS.getValue(),
                StringUtil.parseDescription(Message.EDITOR_SETTINGS_SYNC_DESCRIPTION.getValue()));
        setButton(16, syncItem, (event, slot) -> {

            editorBaseMenu.syncItems();

            return MenuClickResult.NEUTRAL;

        });

    }

    @Override
    public void onClose(boolean forced) {

    }

    @Override
    public void open() {

        String titleDescription = Message.EDITOR_SETTINGS_MENU_TITLE_DESCRIPTION.getValue();
        String title = titleDescription.replace("{1}", menuInventory.getTitle().content());

        ItemStack titleItem = getItem(10);
        if (titleItem != null) {

            ItemUtil.setItemDescription(titleItem, StringUtil.parseDescription(title));

        }

        super.open();

    }

    @Override
    public void onTick(int ticks) {

    }

}
