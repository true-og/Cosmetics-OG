package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.database.Database;
import cosmeticsOG.database.Database.DataType;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.editor.MetaState;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditorMetaMenu extends AbstractStaticMenu {

    private final EditorMenuManager editorManager;
    private final Hat targetHat;

    public EditorMetaMenu(CosmeticsOG core, EditorMenuManager menuManager, Player owner) {

        super(core, menuManager, owner);

        this.editorManager = menuManager;
        this.targetHat = menuManager.getBaseHat();
        this.inventory = Bukkit.createInventory(null, 54,
                Utils.legacySerializerAnyCase(Message.EDITOR_META_MENU_TITLE.getValue()));

        build();

    }

    @Override
    public void open() {

        EditorLore.updateNameDescription(getItem(13), targetHat);
        EditorLore.updateDescriptionDescription(getItem(11), targetHat.getDescription());
        EditorLore.updateDescriptionDescription(getItem(19), targetHat.getPermissionDescription());
        EditorLore.updatePermissionDescription(getItem(15), targetHat);
        EditorLore.updateLabelDescription(getItem(29), targetHat.getLabel());
        EditorLore.updateEquipDescription(getItem(33), targetHat.getEquipDisplayMessage());
        EditorLore.updatePermissionDeniedDescription(getItem(25), targetHat.getPermissionDeniedDisplayMessage());

        super.open();

    }

    @Override
    protected void build() {

        setButton(49, backButtonItem, backButtonAction);

        // Name.
        ItemStack nameItem = ItemUtil.createItem(CompatibleMaterial.PLAYER_HEAD.getMaterial(), 1,
                Message.EDITOR_META_MENU_SET_NAME.getValue());
        setButton(13, nameItem, (event, slot) -> {

            if (event.isLeftClick()) {

                editorManager.getOwnerState().setMetaState(MetaState.HAT_NAME);

                core.prompt(owner, MetaState.HAT_NAME);

                menuManager.closeInventory();

            } else {

                targetHat.setName(Message.EDITOR_MISC_NEW_PARTICLE.getValue());

                EditorLore.updateNameDescription(getItem(13), targetHat);

            }

            return MenuClickResult.NEUTRAL;

        });

        // Description.
        ItemStack descriptionItem = ItemUtil.createItem(CompatibleMaterial.WRITABLE_BOOK.getMaterial(), 1,
                Message.EDITOR_META_MENU_SET_DESCRIPTION.getValue());
        setButton(11, descriptionItem, (event, slot) -> {

            if (event.isLeftClick()) {

                EditorDescriptionMenu editorDescriptionMenu = new EditorDescriptionMenu(core, editorManager, owner,
                        true);

                menuManager.addMenu(editorDescriptionMenu);

                editorDescriptionMenu.open();

            } else if (event.isShiftRightClick()) {

                if (!targetHat.getDescription().isEmpty()) {

                    targetHat.getDescription().clear();

                    Database database = core.getDatabase();
                    String menuName = editorManager.getMenuName();
                    database.saveMetaData(menuName, targetHat, DataType.DESCRIPTION, 0);

                    EditorLore.updateDescriptionDescription(getItem(11), targetHat.getDescription());

                }

            }

            return MenuClickResult.NEUTRAL;

        });

        // Permission description.
        ItemStack permissionDescriptionItem = ItemUtil.createItem(Material.BOOK, 1,
                Message.EDITOR_META_MENU_SET_PERMISSION_DESCRIPTION.getValue());
        setButton(19, permissionDescriptionItem, (event, slot) -> {

            if (event.isLeftClick()) {

                EditorDescriptionMenu editorDescriptionMenu = new EditorDescriptionMenu(core, editorManager, owner,
                        false);

                menuManager.addMenu(editorDescriptionMenu);

                editorDescriptionMenu.open();

            } else if (event.isShiftRightClick()) {

                if (!targetHat.getPermissionDescription().isEmpty()) {

                    targetHat.getPermissionDescription().clear();

                    Database database = core.getDatabase();
                    String menuName = editorManager.getMenuName();
                    database.saveMetaData(menuName, targetHat, DataType.PERMISSION_DESCRIPTION, 0);

                    EditorLore.updateDescriptionDescription(getItem(19), targetHat.getPermissionDescription());

                }

            }

            return MenuClickResult.NEUTRAL;

        });

        // Permission.
        ItemStack permissionItem = ItemUtil.createItem(Material.PAPER, 1,
                Message.EDITOR_META_MENU_SET_PERMISSION.getValue());
        setButton(15, permissionItem, (event, slot) -> {

            editorManager.getOwnerState().setMetaState(MetaState.HAT_PERMISSION);

            core.prompt(owner, MetaState.HAT_PERMISSION);

            menuManager.closeInventory();

            return MenuClickResult.NEUTRAL;

        });

        // Label.
        ItemStack labelItem = ItemUtil.createItem(Material.NAME_TAG, 1, Message.EDITOR_META_MENU_SET_LABEL.getValue());
        setButton(29, labelItem, (event, slot) -> {

            if (event.isLeftClick()) {

                editorManager.getOwnerState().setMetaState(MetaState.HAT_LABEL);

                core.prompt(owner, MetaState.HAT_LABEL);

                menuManager.closeInventory();

            } else if (event.isShiftRightClick()) {

                core.getDatabase().onLabelChange(targetHat.getLabel(), null, null, -1);

                targetHat.removeLabel();

                EditorLore.updateLabelDescription(getItem(29), targetHat.getLabel());

            }

            return MenuClickResult.NEUTRAL;

        });

        // Equip.
        ItemStack equipItem = ItemUtil.createItem(Material.LEATHER_HELMET, 1,
                Message.EDITOR_META_MENU_SET_EQUIP_MESSAGE.getValue());
        setButton(33, equipItem, (event, slot) -> {

            if (event.isLeftClick()) {

                editorManager.getOwnerState().setMetaState(MetaState.HAT_EQUIP_MESSAGE);

                core.prompt(owner, MetaState.HAT_EQUIP_MESSAGE);

                menuManager.closeInventory();

            } else if (event.isShiftRightClick()) {

                targetHat.removeEquipMessage();

                EditorLore.updateEquipDescription(getItem(33), targetHat.getEquipDisplayMessage());

            }

            return MenuClickResult.NEUTRAL;

        });

        // Permission denied.
        ItemStack permissionDeniedItem = ItemUtil.createItem(CompatibleMaterial.MAP.getMaterial(), 1,
                Message.EDITOR_META_MENU_SET_PERMISSION_MESSAGE.getValue());
        setButton(25, permissionDeniedItem, (event, slot) -> {

            if (event.isLeftClick()) {

                editorManager.getOwnerState().setMetaState(MetaState.HAT_PERMISSION_MESSAGE);

                core.prompt(owner, MetaState.HAT_PERMISSION_MESSAGE);

                menuManager.closeInventory();

            } else if (event.isShiftRightClick()) {

                targetHat.removePermissionDeniedMessage();

                EditorLore.updatePermissionDeniedDescription(getItem(25),
                        targetHat.getPermissionDeniedDisplayMessage());

            }

            return MenuClickResult.NEUTRAL;

        });

        // Tags.
        ItemStack tagItem = ItemUtil.createItem(Material.BOWL, 1, Message.EDITOR_META_MENU_SET_TAG.getValue());
        setButton(31, tagItem, (event, slot) -> {

            EditorTagMenuOverview editorTagMenuOverview = new EditorTagMenuOverview(core, editorManager, owner);
            menuManager.addMenu(editorTagMenuOverview);

            editorTagMenuOverview.open();

            return MenuClickResult.NEUTRAL;

        });

    }

    @Override
    public void onClose(boolean forced) {

    }

    @Override
    public void onTick(int ticks) {

    }

}
