package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.locale.Message;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.ui.MenuManager;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.StringUtil;
import java.util.List;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditorResizeMenu extends AbstractStaticMenu {

    private final EditorBaseMenu editorBaseMenu;

    public EditorResizeMenu(CosmeticsOG core, MenuManager menuManager, Player owner, EditorBaseMenu editorBaseMenu) {

        super(core, menuManager, owner);

        this.editorBaseMenu = editorBaseMenu;
        this.inventory = Bukkit.createInventory(null, 27,
                Utils.legacySerializerAnyCase(Message.EDITOR_RESIZE_MENU_TITLE.getValue()));

        build();

    }

    @Override
    protected void build() {

        final MenuAction resizeAction = (event, slot) -> {

            int size = (slot - 10) + (slot < 13 ? 1 : 0);

            editorBaseMenu.resizeTo(size);
            menuManager.closeCurrentMenu();

            return MenuClickResult.NEUTRAL;

        };

        String title = Message.EDITOR_RESIZE_MENU_SET_ROW_SIZE.getValue();
        String suffixInfo[] = StringUtil.parseValue(title, "2");

        setButton(13, backButtonItem, backButtonAction);
        for (int i = 0; i < 7; i++) {

            if (i == 3) {

                continue;

            }

            String t = title.replace("{1}", Integer.toString((i + 1) - (i > 3 ? 1 : 0))).replace(suffixInfo[0],
                    i == 0 ? "" : suffixInfo[1]);
            String description = Message.EDITOR_RESIZE_MENU_SET_ROW_DESCRIPTION.getValue();

            List<TextComponent> descriptionList = List.of(Utils.legacySerializerAnyCase(description));

            ItemStack row = ItemUtil.createItem(CompatibleMaterial.GRAY_DYE.getMaterial(), 1, t, descriptionList);

            setButton(i + 10, row, resizeAction);

        }

        int currentRows = editorBaseMenu.rows();

        ItemStack row = getItem(currentRows + 10 - (currentRows < 3 ? 1 : 0));

        ItemUtil.setItemType(row, CompatibleMaterial.LIME_DYE);
        ItemUtil.highlightItem(row);

    }

    @Override
    public void onClose(boolean forced) {

    }

    @Override
    public void onTick(int ticks) {

    }

}
