package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.editor.MetaState;
import cosmeticsOG.locale.Message;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.ui.MenuInventory;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditorSettingsMenu extends AbstractStaticMenu {

    private final EditorMenuManager editorManager;
    private final EditorBaseMenu editorBaseMenu;
    private final MenuInventory menuInventory;

    public EditorSettingsMenu(
            CosmeticsOG core, EditorMenuManager menuManager, Player owner, EditorBaseMenu editorBaseMenu) {

        super(core, menuManager, owner);

        this.editorManager = menuManager;
        this.editorBaseMenu = editorBaseMenu;
        this.menuInventory = editorBaseMenu.getMenuInventory();
        this.inventory = Bukkit.createInventory(
                null, 54, Utils.legacySerializerAnyCase(Message.EDITOR_SETTINGS_MENU_TITLE.getValue()));

        build();
    }

    @Override
    protected void build() {

        // Back button.
        setButton(49, backButtonItem, backButtonAction);

        // Set title.
        ItemStack titleItem = ItemUtil.createItem(
                CompatibleMaterial.SIGN.getMaterial(), 1, Message.EDITOR_SETTINGS_MENU_SET_TITLE.getValue());
        setButton(11, titleItem, (event, slot) -> {
            editorManager.getOwnerState().setMetaState(MetaState.MENU_TITLE);

            core.prompt(owner, MetaState.MENU_TITLE);

            menuManager.closeInventory();

            return MenuClickResult.NEUTRAL;
        });

        // Set alias.
        ItemStack aliasItem =
                ItemUtil.createItem(Material.NAME_TAG, 1, Message.EDITOR_SETTINGS_MENU_SET_ALIAS.getValue());
        setButton(13, aliasItem, (event, slot) -> {
            if (event.isLeftClick()) {

                editorManager.getOwnerState().setMetaState(MetaState.MENU_ALIAS);

                core.prompt(owner, MetaState.MENU_ALIAS);

                menuManager.closeInventory();

            } else if (event.isShiftRightClick()) {

                editorBaseMenu.getMenuInventory().resetAlias();

                core.getDatabase().saveMenuAlias(menuInventory.getName(), "NULL");

                EditorLore.updateAliasDescription(inventory.getItem(13), menuInventory.getAlias());
            }

            return MenuClickResult.NEUTRAL;
        });

        // Set size.
        ItemStack sizeItem = ItemUtil.createItem(
                CompatibleMaterial.COMPARATOR.getMaterial(), 1, Message.EDITOR_SETTINGS_MENU_SET_SIZE.getValue());
        setButton(15, sizeItem, (event, slot) -> {
            EditorResizeMenu editorResizeMenu = new EditorResizeMenu(core, menuManager, owner, editorBaseMenu);
            menuManager.addMenu(editorResizeMenu);

            editorResizeMenu.open();

            return MenuClickResult.NEUTRAL;
        });

        // Toggle live updates.
        ItemStack liveItem =
                ItemUtil.createItem(Material.LEVER, 1, Message.EDITOR_SETTINGS_MENU_TOGGLE_LIVE_MENU.getValue());
        EditorLore.updateBooleanDescription(
                liveItem, editorBaseMenu.canUpdate(), Message.EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION);
        setButton(29, liveItem, (event, slot) -> {
            editorBaseMenu.toggleUpdates();

            EditorLore.updateBooleanDescription(
                    getItem(29), editorBaseMenu.canUpdate(), Message.EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION);

            return MenuClickResult.NEUTRAL;
        });

        // Sync icons.
        ItemStack syncItem = ItemUtil.createItem(
                CompatibleMaterial.CONDUIT.getMaterial(),
                Message.EDITOR_SETTINGS_MENU_SYNC_ICONS.getValue(),
                StringUtil.parseDescription(Message.EDITOR_SETTINGS_SYNC_DESCRIPTION.getValue()));
        setButton(31, syncItem, (event, slot) -> {
            editorBaseMenu.syncItems();

            return MenuClickResult.NEUTRAL;
        });

        // Delete.
        ItemStack deleteItem = ItemUtil.createItem(Material.TNT, 1, Message.EDITOR_SETTINGS_MENU_DELETE.getValue());
        setButton(33, deleteItem, (event, slot) -> {
            EditorDeleteMenu editorDeleteMenu = new EditorDeleteMenu(core, menuManager, owner, editorBaseMenu);
            menuManager.addMenu(editorDeleteMenu);

            editorDeleteMenu.open();

            return MenuClickResult.NEUTRAL;
        });
    }

    @Override
    public void open() {

        String titleDescription = Message.EDITOR_SETTINGS_MENU_TITLE_DESCRIPTION.getValue();
        String title = titleDescription.replace("{1}", menuInventory.getTitle().content());
        ItemStack titleItem = getItem(11);
        if (titleItem != null) {

            ItemUtil.setItemDescription(titleItem, StringUtil.parseDescription(title));
        }

        ItemStack aliasItem = getItem(13);
        if (aliasItem != null) {

            EditorLore.updateAliasDescription(aliasItem, menuInventory.getAlias());
        }

        super.open();
    }

    @Override
    public void onClose(boolean forced) {}

    @Override
    public void onTick(int ticks) {}
}
