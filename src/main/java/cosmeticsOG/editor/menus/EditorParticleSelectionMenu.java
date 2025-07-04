package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.ParticleEffect;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.MathUtil;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EditorParticleSelectionMenu extends AbstractStaticMenu {

    private final Hat targetHat;
    private final int particleIndex;
    private final MenuAction particleSelectionAction;

    private MenuType menuType = MenuType.PARTICLES;

    private final Map<Integer, Inventory> particleMenus;
    private final Map<Integer, Inventory> colorFilterMenus;
    private final Map<Integer, Inventory> dataFilterMenus;
    private final Inventory recentFilterMenu;

    private final Map<Integer, ParticleEffect> particles;
    private final Map<Integer, ParticleEffect> colorParticles;
    private final Map<Integer, ParticleEffect> dataParticles;
    private final Map<Integer, ParticleEffect> recentParticles;

    private final int particlePages;
    private final int colorPages;
    private final int dataPages;

    private int currentParticlePage = 0;
    private int currentColorPage = 0;
    private int currentDataPage = 0;

    public EditorParticleSelectionMenu(
            CosmeticsOG core,
            EditorMenuManager menuManager,
            Player owner,
            int particleIndex,
            MenuObjectCallback callback) {

        super(core, menuManager, owner);

        this.targetHat = menuManager.getTargetHat();
        this.particleIndex = particleIndex;

        this.particleMenus = new HashMap<Integer, Inventory>();
        this.colorFilterMenus = new HashMap<Integer, Inventory>();
        this.dataFilterMenus = new HashMap<Integer, Inventory>();
        this.recentFilterMenu = Bukkit.createInventory(
                null, 54, Utils.legacySerializerAnyCase(Message.EDITOR_PARTICLE_MENU_RECENT_FILTER_TITLE.getValue()));

        this.particles = new HashMap<Integer, ParticleEffect>();
        this.colorParticles = new HashMap<Integer, ParticleEffect>();
        this.dataParticles = new HashMap<Integer, ParticleEffect>();
        this.recentParticles = new HashMap<Integer, ParticleEffect>();

        this.particlePages = MathUtil.calculatePageCount(getParticleCountWithTag(MenuType.PARTICLES), 45);
        this.colorPages = MathUtil.calculatePageCount(getParticleCountWithTag(MenuType.COLOR), 45);
        this.dataPages = MathUtil.calculatePageCount(getParticleCountWithTag(MenuType.DATA), 45);

        this.inventory = Bukkit.createInventory(null, 54, Component.text(""));

        this.particleSelectionAction = (event, slot) -> {
            int index = slot;
            switch (menuType) {
                case COLOR:
                    index = slot + (currentColorPage * 45);
                    if (colorParticles.containsKey(index)) {

                        callback.onSelect(colorParticles.get(index));
                    }

                    break;
                case DATA:
                    index = slot + (currentDataPage * 45);
                    if (dataParticles.containsKey(index)) {

                        callback.onSelect(dataParticles.get(index));
                    }

                    break;
                case RECENTS:
                    index = getClampedIndex(slot, 10, 2);
                    if (recentParticles.containsKey(index)) {

                        callback.onSelect(recentParticles.get(index));
                    }

                    break;
                default:
                    index = slot + (currentParticlePage * 45);
                    if (particles.containsKey(index)) {

                        callback.onSelect(particles.get(index));
                    }
            }

            return MenuClickResult.NEUTRAL;
        };

        build();
    }

    @Override
    public void open() {

        Inventory inventory = particleMenus.get(currentParticlePage);

        open(inventory);
    }

    private void open(Inventory inventory) {

        menuManager.isOpeningMenu(this);
        owner.openInventory(inventory);
    }

    @Override
    protected void build() {

        // Build particle menus.
        generateMenus(particleMenus, Message.EDITOR_PARTICLE_MENU_TITLE, particlePages, MenuType.PARTICLES);

        // Color filter.
        generateMenus(colorFilterMenus, Message.EDITOR_PARTICLE_MENU_COLOUR_FILTER_TITLE, colorPages, MenuType.COLOR);

        // Data filter.
        generateMenus(dataFilterMenus, Message.EDITOR_PARTICLE_MENU_DATA_FILTER_TITLE, dataPages, MenuType.DATA);

        // Recents filter.
        recentFilterMenu.setItem(49, backButtonItem);
        recentFilterMenu.setItem(
                45, ItemUtil.createItem(Material.BOWL, 1, Message.EDITOR_PARTICLE_MENU_NORMAL_FILTER.getValue()));
        recentFilterMenu.setItem(
                46, ItemUtil.createItem(Material.BOWL, 1, Message.EDITOR_PARTICLE_MENU_COLOUR_FILTER.getValue()));
        recentFilterMenu.setItem(
                47, ItemUtil.createItem(Material.BOWL, 1, Message.EDITOR_PARTICLE_MENU_DATA_FILTER.getValue()));
        recentFilterMenu.setItem(
                53,
                ItemUtil.createItem(
                        CompatibleMaterial.MUSHROOM_STEW.getMaterial(),
                        1,
                        Message.EDITOR_PARTICLE_MENU_RECENT_FILTER.getValue()));

        // Back button.
        setAction(49, backButtonAction);

        // All particles filter.
        setAction(45, (event, slot) -> {
            menuType = MenuType.PARTICLES;

            open(particleMenus.get(currentParticlePage));

            return MenuClickResult.NEUTRAL;
        });

        // Color filter.
        setAction(46, (event, slot) -> {
            menuType = MenuType.COLOR;

            open(colorFilterMenus.get(currentColorPage));

            return MenuClickResult.NEUTRAL;
        });

        // Data filter.
        setAction(47, (event, slot) -> {
            menuType = MenuType.DATA;

            open(dataFilterMenus.get(currentDataPage));

            return MenuClickResult.NEUTRAL;
        });

        // Recents filter.
        setAction(53, (event, slot) -> {
            menuType = MenuType.RECENTS;

            open(recentFilterMenu);

            return MenuClickResult.NEUTRAL;
        });

        // Previous page.
        setAction(48, (event, slot) -> {
            switch (menuType) {
                case COLOR:
                    open(colorFilterMenus.get(--currentColorPage));

                    break;
                case DATA:
                    open(dataFilterMenus.get(--currentDataPage));

                    break;
                default:
                    open(particleMenus.get(--currentParticlePage));
            }

            return MenuClickResult.NEUTRAL;
        });

        // Next page.
        setAction(50, (event, slot) -> {
            switch (menuType) {
                case COLOR:
                    open(colorFilterMenus.get(++currentColorPage));

                    break;
                case DATA:
                    open(dataFilterMenus.get(++currentDataPage));

                    break;
                default:
                    open(particleMenus.get(++currentParticlePage));
            }

            return MenuClickResult.NEUTRAL;
        });

        for (int i = 0; i < 45; i++) {

            setAction(i, particleSelectionAction);
        }

        ParticleEffect currentEffect = targetHat.getParticle(particleIndex);

        int particlePage = 0;
        int particleItemIndex = 0;
        int particleParticleIndex = 0;

        int colorPage = 0;
        int colorItemIndex = 0;
        int colorParticleIndex = 0;

        int dataPage = 0;
        int dataItemIndex = 0;
        int dataParticleIndex = 0;

        for (ParticleEffect pe : ParticleEffect.values()) {

            if (!pe.isSupported()) {

                continue;
            }

            ItemStack item = pe.getItem().clone();
            ItemUtil.setItemName(item, pe.getDisplayName());

            boolean isSelected = false;
            if (pe.equals(currentEffect)) {

                ItemUtil.highlightItem(item);

                isSelected = true;
            }

            EditorLore.updateParticleItemDescription(item, pe, isSelected);

            if (pe.hasColorData()) {

                colorFilterMenus.get(colorPage).setItem(colorItemIndex++, item);
                colorParticles.put(colorParticleIndex++, pe);
            }

            if (pe.hasData()) {

                dataFilterMenus.get(dataPage).setItem(dataItemIndex++, item);
                dataParticles.put(dataParticleIndex++, pe);
            }

            particleMenus.get(particlePage).setItem(particleItemIndex++, item);
            particles.put(particleParticleIndex++, pe);

            if (particleItemIndex % 45 == 0) {

                particleItemIndex = 0;

                particlePage++;
            }

            if (colorItemIndex > 0 && colorItemIndex % 45 == 0) {

                colorItemIndex = 0;
                colorPage = 0;
            }

            if (dataItemIndex > 0 && dataItemIndex % 45 == 0) {

                dataItemIndex = 0;

                dataPage++;
            }
        }

        // Recently used.
        int index = 0;
        for (ParticleEffect pe : core.getParticleManager().getRecentlyUsedParticles(ownerID)) {

            ItemStack item = pe.getItem().clone();
            ItemUtil.setItemName(item, pe.getDisplayName());

            boolean selected = false;
            if (pe.equals(currentEffect)) {

                ItemUtil.highlightItem(item);

                selected = true;
            }

            EditorLore.updateParticleItemDescription(item, pe, selected);

            recentParticles.put(index, pe);
            recentFilterMenu.setItem(getNormalIndex(index++, 10, 2), item);
        }
    }

    @Override
    public void onClose(boolean forced) {}

    @Override
    public void onTick(int ticks) {}

    @Override
    public boolean hasInventory(Inventory inventory) {

        switch (menuType) {
            case COLOR:
                return colorFilterMenus.containsValue(inventory);
            case DATA:
                return dataFilterMenus.containsValue(inventory);
            case RECENTS:
                return recentFilterMenu.equals(inventory);
            default:
                return particleMenus.containsValue(inventory);
        }
    }

    private void generateMenus(Map<Integer, Inventory> pages, Message title, int totalPages, MenuType type) {

        for (int i = 0; i < totalPages; i++) {

            String menuTitle =
                    title.replace("{1}", Integer.toString(i + 1)).replace("{2}", Integer.toString(totalPages));
            Inventory inventory = Bukkit.createInventory(null, 54, Utils.legacySerializerAnyCase(menuTitle));

            inventory.setItem(49, backButtonItem);
            inventory.setItem(
                    45, ItemUtil.createItem(Material.BOWL, 1, Message.EDITOR_PARTICLE_MENU_NORMAL_FILTER.getValue()));
            inventory.setItem(
                    46, ItemUtil.createItem(Material.BOWL, 1, Message.EDITOR_PARTICLE_MENU_COLOUR_FILTER.getValue()));
            inventory.setItem(
                    47, ItemUtil.createItem(Material.BOWL, 1, Message.EDITOR_PARTICLE_MENU_DATA_FILTER.getValue()));
            inventory.setItem(
                    53, ItemUtil.createItem(Material.BOWL, 1, Message.EDITOR_PARTICLE_MENU_RECENT_FILTER.getValue()));

            if ((i + 1) < totalPages) {

                inventory.setItem(
                        50,
                        ItemUtil.createItem(
                                CompatibleMaterial.LIME_DYE.getMaterial(),
                                1,
                                Message.EDITOR_MISC_NEXT_PAGE.getValue()));
            }

            if ((i + 1) > 1) {

                inventory.setItem(
                        48,
                        ItemUtil.createItem(
                                CompatibleMaterial.LIME_DYE.getMaterial(),
                                1,
                                Message.EDITOR_MISC_PREVIOUS_PAGE.getValue()));
            }

            switch (type) {
                case COLOR:
                    inventory.setItem(
                            46,
                            ItemUtil.createItem(
                                    CompatibleMaterial.MUSHROOM_STEW.getMaterial(),
                                    1,
                                    Message.EDITOR_PARTICLE_MENU_COLOUR_FILTER.getValue()));
                    break;
                case DATA:
                    inventory.setItem(
                            47,
                            ItemUtil.createItem(
                                    CompatibleMaterial.MUSHROOM_STEW.getMaterial(),
                                    1,
                                    Message.EDITOR_PARTICLE_MENU_DATA_FILTER.getValue()));

                    break;
                case RECENTS:
                    inventory.setItem(
                            53,
                            ItemUtil.createItem(
                                    CompatibleMaterial.MUSHROOM_STEW.getMaterial(),
                                    1,
                                    Message.EDITOR_PARTICLE_MENU_RECENT_FILTER.getValue()));

                    break;
                default:
                    inventory.setItem(
                            45,
                            ItemUtil.createItem(
                                    CompatibleMaterial.MUSHROOM_STEW.getMaterial(),
                                    1,
                                    Message.EDITOR_PARTICLE_MENU_NORMAL_FILTER.getValue()));

                    break;
            }

            pages.put(i, inventory);
        }
    }

    private int getParticleCountWithTag(MenuType type) {

        int count = 0;
        for (ParticleEffect pe : ParticleEffect.values()) {

            if (!pe.isSupported()) {

                continue;
            }

            switch (type) {
                case COLOR:
                    if (pe.hasColorData()) {

                        count++;
                    }

                    break;
                case DATA:
                    if (pe.hasData()) {

                        count++;
                    }

                    break;
                default:
                    count++;
            }
        }

        return count;
    }

    private enum MenuType {
        PARTICLES,
        COLOR,
        DATA,
        RECENTS;
    }
}
