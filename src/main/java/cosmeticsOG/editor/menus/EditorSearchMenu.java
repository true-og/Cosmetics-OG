package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.locale.Message;
import cosmeticsOG.ui.AbstractListMenu;
import cosmeticsOG.ui.MenuManager;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.MathUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EditorSearchMenu extends AbstractListMenu {

    private final List<Material> matchingResults;
    private final List<Material> blacklist = Arrays.asList(Material.AIR);

    private final MenuAction selectAction;

    private final String menuTitle;

    public EditorSearchMenu(CosmeticsOG core, MenuManager menuManager, Player owner, String searchQuery,
            MenuObjectCallback callback)
    {

        super(core, menuManager, owner, false);

        String query = searchQuery.toLowerCase();
        String[] queries = query.split(",");

        this.matchingResults = new ArrayList<Material>();
        this.menuTitle = EditorLore.getTrimmedMenuTitle(query, Message.EDITOR_SEARCH_MENU_TITLE);

        for (Material material : Material.values()) {

            if (blacklist.contains(material)) {

                continue;

            }

            String materialName = material.toString().toLowerCase();
            for (String q : queries) {

                if (materialName.contains(q)) {

                    matchingResults.add(material);

                }

            }

        }

        this.totalPages = MathUtil.calculatePageCount(matchingResults.size(), 45);
        this.selectAction = (event, slot) -> {

            int index = slot + (currentPage * 45);
            if (index < matchingResults.size()) {

                callback.onSelect(new ItemStack(matchingResults.get(index)));

            }

            return MenuClickResult.NEUTRAL;

        };

        build();

    }

    @Override
    public void insertEmptyItem() {

        setItem(0, 22, ItemUtil.createItem(CompatibleMaterial.BARRIER.getMaterial(), 1,
                Message.EDITOR_SEARCH_MENU_NO_RESULTS.getValue()));

    }

    @Override
    public void removeEmptyItem() {

    }

    @Override
    protected void build() {

        // Back.
        setAction(49, backButtonAction);

        // Previous page.
        setAction(48, (event, slot) -> {

            currentPage--;

            open();

            return MenuClickResult.NEUTRAL;

        });

        // Next page.
        setAction(50, (event, slot) -> {

            currentPage++;

            open();

            return MenuClickResult.NEUTRAL;

        });

        for (int i = 0; i < 45; i++) {

            setAction(i, selectAction);

        }

        for (int i = 0; i < totalPages; i++) {

            Inventory inventory = Bukkit.createInventory(null, 54, Utils.legacySerializerAnyCase(menuTitle));

            inventory.setItem(49, backButtonItem);

            if ((i + 1) < totalPages) {

                inventory.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE.getMaterial(), 1,
                        Message.EDITOR_MISC_NEXT_PAGE.getValue()));

            }

            if ((i + 1) > 1) {

                inventory.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE.getMaterial(), 1,
                        Message.EDITOR_MISC_PREVIOUS_PAGE.getValue()));

            }

            setMenu(i, inventory);

        }

        if (matchingResults.isEmpty()) {

            setEmpty(true);

            return;

        }

        int index = 0;
        int page = 0;
        for (Material material : matchingResults) {

            menus.get(page).setItem(index++, ItemUtil.createItem(material, 1, material.name()));

            if (index % 45 == 0) {

                index = 0;

                page++;

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
