package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.effects.PixelEffect;
import cosmeticsOG.particles.properties.ParticleType;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.StringUtil;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EditorTypeMenu extends AbstractStaticMenu {

    private final EditorMenuManager editorManager;
    private final Hat targetHat;
    private final MenuAction setTypeAction;
    private final MenuObjectCallback callback;

    private final String title = Message.EDITOR_TYPE_MENU_TITLE.getValue();
    private final String typePrefix = Message.EDITOR_TYPE_MENU_TYPE_PREFIX.getValue();

    private Map<String, BufferedImage> customTypes;

    private Map<Integer, Inventory> includedTypeMenus;
    private Map<Integer, Inventory> customTypeMenus;

    private Map<Integer, ParticleType> includedTypeData;
    private Map<Integer, BufferedImage> customTypeData;
    private Map<Integer, String> customTypeNames;

    private final int includedTypePages;
    private final int customTypePages;

    private int includedTypeCurrentPage;
    private int customTypeCurrentPage;

    private boolean selectingFromIncludedTypes = true;

    public EditorTypeMenu(CosmeticsOG core, EditorMenuManager menuManager, Player owner, MenuObjectCallback callback) {

        super(core, menuManager, owner);

        this.editorManager = menuManager;
        this.targetHat = editorManager.getTargetHat();
        this.callback = callback;
        this.inventory = Bukkit.createInventory(null, 54, Component.text(""));

        includedTypeMenus = new HashMap<Integer, Inventory>();
        customTypeMenus = new HashMap<Integer, Inventory>();

        includedTypeData = new HashMap<Integer, ParticleType>();
        customTypeData = new HashMap<Integer, BufferedImage>();
        customTypeNames = new HashMap<Integer, String>();

        customTypes = core.getDatabase().getImages(false);

        includedTypePages = (int) Math.ceil((double) ParticleType.values().length / 28D);
        customTypePages = (int) Math.max(Math.ceil((double) customTypes.size() / 28D), 1);

        includedTypeCurrentPage = 0;
        customTypeCurrentPage = 0;

        setTypeAction = (event, slot) -> {

            int index = getClampedIndex(slot, 10, 2);
            if (selectingFromIncludedTypes) {

                int typeIndex = index + (includedTypeCurrentPage * 28);
                if (includedTypeData.containsKey(typeIndex)) {

                    targetHat.setType(includedTypeData.get(typeIndex));

                    menuManager.closeCurrentMenu();

                }

            } else {

                int imageIndex = index + (customTypeCurrentPage * 28);
                if (customTypeData.containsKey(imageIndex)) {

                    String name = customTypeNames.get(imageIndex);
                    BufferedImage image = customTypeData.get(imageIndex);

                    targetHat.setType(ParticleType.CUSTOM);
                    targetHat.setCustomType(new PixelEffect(image, name));

                    menuManager.closeCurrentMenu();

                }

            }

            return MenuClickResult.NEUTRAL;

        };

        build();

    }

    @Override
    public void open() {

        open(includedTypeMenus.get(includedTypeCurrentPage));

    }

    private void open(Inventory inventory) {

        menuManager.isOpeningMenu(this);

        owner.openInventory(inventory);

    }

    @Override
    protected void build() {

        // Main menu.
        setAction(49, backButtonAction);
        for (int i = 0; i < 28; i++) {

            setAction(getNormalIndex(i, 10, 2), setTypeAction);

        }

        // Previous page.
        setAction(48, (event, slot) -> {

            if (selectingFromIncludedTypes) {

                includedTypeCurrentPage--;

                open(includedTypeMenus.get(includedTypeCurrentPage));

            } else {

                customTypeCurrentPage--;

                open(customTypeMenus.get(customTypeCurrentPage));

            }

            return MenuClickResult.NEUTRAL;

        });

        // Next page.
        setAction(50, (event, slot) -> {

            if (selectingFromIncludedTypes) {

                includedTypeCurrentPage++;

                open(includedTypeMenus.get(includedTypeCurrentPage));

            } else {

                customTypeCurrentPage++;

                open(customTypeMenus.get(customTypeCurrentPage));

            }

            return MenuClickResult.NEUTRAL;

        });

        // Included types filter.
        setAction(46, (event, slot) -> {

            selectingFromIncludedTypes = true;

            open(includedTypeMenus.get(includedTypeCurrentPage));

            return MenuClickResult.NEUTRAL;

        });

        // Custom types filter.
        setAction(47, (event, slot) -> {

            selectingFromIncludedTypes = false;

            open(customTypeMenus.get(customTypeCurrentPage));

            return MenuClickResult.NEUTRAL;

        });

        // Create included type menus.
        generateIncludedTypeMenus();

        // Create custom type menus.
        generateCustomTypeMenus();

    }

    @Override
    public void onClose(boolean forced) {

        if (!forced) {

            callback.onSelect(null);

        }

    }

    @Override
    public void onTick(int ticks) {

    }

    @Override
    public boolean hasInventory(Inventory inventory) {

        if (selectingFromIncludedTypes) {

            return includedTypeMenus.get(includedTypeCurrentPage).equals(inventory);

        }

        return customTypeMenus.get(customTypeCurrentPage).equals(inventory);

    }

    private void generateIncludedTypeMenus() {

        generateMenus(includedTypeMenus, includedTypePages, 0);

        ParticleType currentType = targetHat.getType();
        int page = 0;
        int index = 0;
        int dataIndex = 0;

        for (ParticleType type : ParticleType.values()) {

            if (type.isCustom()) {

                continue;

            }

            if (type.isDebug()) {

                if (!CosmeticsOG.debugging) {

                    continue;

                }

            }

            boolean selected = currentType.equals(type);

            String title = typePrefix.replace("{1}", type.getDisplayName());
            ItemStack item = ItemUtil.createItem(CompatibleMaterial.FIREWORK_STAR.getMaterial(), 1, title);

            EditorLore.updateTypeItemDescription(item, type, selected);

            if (selected) {

                ItemUtil.setItemType(item, CompatibleMaterial.CYAN_DYE);
                ItemUtil.highlightItem(item);

            }

            includedTypeMenus.get(page).setItem(getNormalIndex(index++, 10, 2), item);
            includedTypeData.put(dataIndex++, type);

            if (index % 28 == 0) {

                index = 0;

                page++;

            }

        }

    }

    private void generateCustomTypeMenus() {

        generateMenus(customTypeMenus, customTypePages, 1);

        String description = Message.EDITOR_TYPE_MENU_CUSTOM_TYPE_DESCRIPTION.getValue();
        String[] selectInfo = StringUtil.parseValue(description, "1");
        String[] selectedInfo = StringUtil.parseValue(description, "2");

        if (customTypes.size() == 0) {

            customTypeMenus.get(0).setItem(22, ItemUtil.createItem(CompatibleMaterial.BARRIER.getMaterial(), 1,
                    Message.EDITOR_TYPE_MENU_NO_CUSTOM_TYPES.getValue()));

        } else {

            String currentEffectName = "";
            if (targetHat.getType().isCustom()) {

                PixelEffect customEffect = targetHat.getCustomEffect();
                if (customEffect != null) {

                    currentEffectName = customEffect.getImageName();

                }

            }

            int page = 0;
            int index = 0;
            int dataIndex = 0;
            for (Entry<String, BufferedImage> types : customTypes.entrySet()) {

                String name = types.getKey();
                boolean isSelected = name.equals(currentEffectName);

                String title = typePrefix.replace("{1}", StringUtil.capitalizeFirstLetter(name.toLowerCase()));
                ItemStack item = ItemUtil.createItem(CompatibleMaterial.FIRE_CHARGE.getMaterial(), 1, title);

                String select = isSelected ? "" : selectInfo[1];
                String selected = isSelected ? selectedInfo[1] : "";
                String s = description.replace(selectInfo[0], select).replace(selectedInfo[0], selected);

                ItemUtil.setItemDescription(item, StringUtil.parseDescription(s));

                if (isSelected) {

                    ItemUtil.highlightItem(item);

                }

                customTypeMenus.get(page).setItem(getNormalIndex(index++, 10, 2), item);

                customTypeData.put(dataIndex, types.getValue());
                customTypeNames.put(dataIndex, name);

                dataIndex++;

                if (index % 28 == 0) {

                    index = 0;

                    page++;

                }

            }

        }

    }

    private void generateMenus(Map<Integer, Inventory> menus, int pages, int category) {

        for (int i = 0; i < pages; i++) {

            String menuTitle = title.replace("{1}", Integer.toString(i + 1)).replace("{2}", Integer.toString(pages));

            Inventory menu = Bukkit.createInventory(null, 54, Utils.legacySerializerAnyCase(menuTitle));
            menu.setItem(49, backButtonItem);

            // Next page.
            if ((i + 1) < pages) {

                menu.setItem(50, ItemUtil.createItem(CompatibleMaterial.LIME_DYE.getMaterial(), 1,
                        Message.EDITOR_MISC_NEXT_PAGE.getValue()));

            }

            // Previous page.
            if ((i + 1) > 1) {

                menu.setItem(48, ItemUtil.createItem(CompatibleMaterial.LIME_DYE.getMaterial(), 1,
                        Message.EDITOR_MISC_PREVIOUS_PAGE.getValue()));

            }

            if (category == 0) {

                menu.setItem(46, ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW.getMaterial(), 1,
                        Message.EDITOR_TYPE_MENU_INCLUDED_FILTER.getValue()));
                menu.setItem(47,
                        ItemUtil.createItem(Material.BOWL, 1, Message.EDITOR_TYPE_MENU_CUSTOM_FILTER.getValue()));

            } else {

                menu.setItem(46,
                        ItemUtil.createItem(Material.BOWL, 1, Message.EDITOR_TYPE_MENU_INCLUDED_FILTER.getValue()));
                menu.setItem(47, ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW.getMaterial(), 1,
                        Message.EDITOR_TYPE_MENU_CUSTOM_FILTER.getValue()));

            }

            menus.put(i, menu);

        }

    }

}
