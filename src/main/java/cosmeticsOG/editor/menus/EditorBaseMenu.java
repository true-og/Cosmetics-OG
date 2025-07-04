package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.properties.IconData;
import cosmeticsOG.particles.properties.IconData.ItemStackTemplate;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.ui.MenuInventory;
import cosmeticsOG.ui.MenuManager;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.StringUtil;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class EditorBaseMenu extends AbstractStaticMenu {

    private final EditorMenuManager editorManager;
    private final MenuInventory menuInventory;

    private final MenuAction emptyParticleAction;
    private final MenuAction existingParticleAction;

    private final ItemStack emptyItem;

    private int rows = 0;
    private boolean canUpdate = true;
    private String editingTitle;

    public EditorBaseMenu(CosmeticsOG core, MenuManager menuManager, Player owner, MenuInventory menuInventory) {

        super(core, menuManager, owner);

        this.editorManager = (EditorMenuManager) menuManager;
        this.menuInventory = menuInventory;
        this.rows = menuInventory.getSize() / 9;
        this.emptyItem = ItemUtil.createItem(
                CompatibleMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.getMaterial(),
                Message.EDITOR_EMPTY_SLOT_TITLE.getValue(),
                StringUtil.parseDescription(Message.EDITOR_SLOT_DESCRIPTION.getValue()));
        this.editingTitle = EditorLore.getTrimmedMenuTitle(menuInventory.getName(), Message.EDITOR_BASE_MENU_TITLE);
        this.inventory =
                Bukkit.createInventory(null, menuInventory.getSize(), Utils.legacySerializerAnyCase(editingTitle));
        this.inventory.setContents(menuInventory.getContents());

        emptyParticleAction = (event, slot) -> {
            if (event.isLeftClick()) {

                editorManager.setTargetHat(createHat(slot));
                editorManager.setTargetSlot(slot);
                editorManager.openMainMenu();

            } else if (event.isRightClick()) {

                editorManager.openSettingsMenu();
            }

            return MenuClickResult.NEUTRAL;
        };

        existingParticleAction = (event, slot) -> {
            if (event.isLeftClick()) {

                editorManager.setTargetHat(menuInventory.getHat(slot));
                editorManager.setTargetSlot(slot);
                editorManager.openMainMenu();

            } else if (event.isShiftRightClick()) {

                deleteHat(slot);

                return MenuClickResult.NEGATIVE;

            } else if (event.isRightClick()) {

                editorManager.openSettingsMenu();
            }

            return MenuClickResult.NEUTRAL;
        };

        build();
    }

    @Override
    public void open() {

        int slot = editorManager.getTargetSlot();
        if (slot < 0) {

            super.open();

            return;
        }

        Hat hat = menuInventory.getHat(slot);
        if (hat != null) {

            EditorLore.updateHatDescription(getItem(slot), hat, true);
        }

        super.open();
    }

    @Override
    protected void build() {

        for (int i = 0; i < inventory.getSize(); i++) {

            if (inventory.getItem(i) == null) {

                setButton(i, emptyItem, emptyParticleAction);

            } else {

                setAction(i, existingParticleAction);
            }

            Hat hat = menuInventory.getHat(i);
            if (hat == null) {

                continue;
            }

            EditorLore.updateHatDescription(inventory.getItem(i), hat, true);
        }
    }

    @Override
    public void onClose(boolean forced) {}

    @Override
    public void onTick(int ticks) {

        if (canUpdate) {

            for (Entry<Integer, Hat> entry : menuInventory.getHats().entrySet()) {

                int slot = entry.getKey();

                Hat hat = entry.getValue();
                if (hat == null) {

                    continue;
                }

                IconData iconData = hat.getIconData();
                if (!iconData.isLive()) {

                    continue;
                }

                ItemStackTemplate itemTemplate = iconData.getNextItem(ticks);
                ItemUtil.setItemType(getItem(slot), itemTemplate.getMaterial(), itemTemplate.getDurability());
            }
        }
    }

    @Override
    public String getName() {

        return menuInventory.getName();
    }

    /**
     * Removes the hat at this slot
     * @param slot
     */
    public void removeHat(int slot) {

        menuInventory.removeHat(slot);
    }

    /**
     * Removes the hat and item at this slot
     * @param slot
     */
    public void removeButton(int slot) {

        removeHat(slot);

        setButton(slot, emptyItem, emptyParticleAction);
    }

    /**
     * Get this menus MenuInventory
     * @return
     */
    public MenuInventory getMenuInventory() {

        return menuInventory;
    }

    /**
     * Set this menu's title
     * @param title
     */
    public void setTitle(String title) {

        menuInventory.setTitle(Utils.legacySerializerAnyCase(title));

        Inventory replacementInventory =
                Bukkit.createInventory(null, inventory.getSize(), Utils.legacySerializerAnyCase(editingTitle));
        replacementInventory.setContents(inventory.getContents());

        inventory = replacementInventory;

        core.getDatabase().saveMenuTitle(getName(), title);
    }

    /**
     * Set this menu's alias
     * @param alias
     */
    public void setAlias(String alias) {

        menuInventory.setAlias(alias);

        core.getDatabase().saveMenuAlias(getName(), alias);
    }

    /**
     * Changes the items material type and durability
     */
    public void setItemType(int slot, ItemStack item) {

        Material material = item.getType();

        // Initialize damage variable.
        int damage = 0;

        // Use Damageable to get and set item damage.
        if (item.hasItemMeta()) {

            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable) {

                Damageable damageable = (Damageable) meta;
                damage = damageable.getDamage();
            }
        }

        // Call the ItemUtil method to update the item type and its damage
        ItemUtil.setItemType(getItem(slot), material, damage);
    }

    /**
     * Toggle live updates for this menu
     */
    public void toggleUpdates() {

        canUpdate = !canUpdate;
    }

    /**
     * Checks to see if this menu can update icons
     * @return
     */
    public boolean canUpdate() {

        return canUpdate;
    }

    /**
     * Get how many rows this inventory has
     * @return
     */
    public int rows() {

        return rows;
    }

    /**
     * Syncs all hats icons
     */
    public void syncItems() {

        for (Entry<Integer, Hat> hats : menuInventory.getHats().entrySet()) {

            hats.getValue().getIconData().reset();
        }
    }

    public void resizeTo(int rows) {

        if (this.rows != rows) {

            Inventory replacementInventory =
                    Bukkit.createInventory(null, (9 * rows), Utils.legacySerializerAnyCase(editingTitle));

            // inventory.setContents() only works when resizing the menus to a smaller size. So, we need to use a loop
            // to account for the other option.
            for (int i = 0; i < replacementInventory.getSize(); i++) {

                try {

                    replacementInventory.setItem(i, inventory.getItem(i));

                } catch (ArrayIndexOutOfBoundsException error) {
                }
            }

            // Fill in any empty slots.
            if (rows > this.rows) {

                for (int i = inventory.getSize(); i < replacementInventory.getSize(); i++) {

                    replacementInventory.setItem(i, emptyItem);
                }
            }

            inventory = replacementInventory;

            this.rows = rows;

            core.getDatabase().saveMenuSize(getName(), rows);
        }
    }

    public void changeSlots(int currentSlot, int newSlot, boolean swapping) {

        ItemStack currentItem = getItem(currentSlot);
        ItemStack swappingItem = getItem(newSlot);
        MenuAction currentAction = getAction(currentSlot);
        MenuAction swappingAction = getAction(newSlot);
        Hat currentHat = menuInventory.getHat(currentSlot);
        Hat swappingHat = null;

        currentHat.setSlot(newSlot);

        if (swapping) {

            swappingHat = menuInventory.getHat(newSlot);
            swappingHat.setSlot(currentSlot);

            menuInventory.setHat(currentSlot, swappingHat);

        } else {

            menuInventory.removeHat(currentSlot);
        }

        setButton(currentSlot, swappingItem, swappingAction);
        setButton(newSlot, currentItem, currentAction);

        menuInventory.setHat(newSlot, currentHat);
        editorManager.setTargetSlot(newSlot);

        core.getDatabase().moveHat(currentHat, swappingHat, getName(), null, currentSlot, newSlot, swapping);
    }

    /**
     * Clones a hat and adds it to the new slot
     * @param currentSlot
     * @param newSlot
     */
    public void cloneHat(int currentSlot, int newSlot) {

        Hat currentHat = menuInventory.getHat(currentSlot);
        Hat clonedHat = currentHat.clone();

        clonedHat.setSlot(newSlot);

        menuInventory.setHat(newSlot, clonedHat);
        setButton(newSlot, clonedHat.getItem(), existingParticleAction);

        onHatNameChange(clonedHat, newSlot);
        EditorLore.updateHatDescription(getItem(newSlot), clonedHat, true);

        core.getDatabase().cloneHat(getName(), currentHat, newSlot);
    }

    /**
     * Updates the item's display name that belongs in this slot
     * @param hat
     * @param slot
     */
    public void onHatNameChange(Hat hat, int slot) {

        ItemUtil.setItemName(getItem(slot), hat.getDisplayName());
    }

    /**
     * Creates and returns a new hat object
     * @param slot
     * @return
     */
    private Hat createHat(int slot) {

        Hat hat = new Hat();

        hat.setSlot(slot);
        hat.setLoaded(true);

        ItemStack emptyItem = ItemUtil.createItem(
                CompatibleMaterial.SUNFLOWER.getMaterial(), 1, Message.EDITOR_MISC_NEW_PARTICLE.getValue());

        EditorLore.updateHatDescription(emptyItem, hat, true);

        menuInventory.setHat(slot, hat);
        setButton(slot, emptyItem, existingParticleAction);

        core.getDatabase().createHat(menuInventory.getName(), hat);

        return hat;
    }

    /**
     * Deletes the hat in the current slot
     * @param slot
     */
    private void deleteHat(int slot) {

        setButton(slot, emptyItem, emptyParticleAction);
        menuInventory.removeHat(slot);

        core.getDatabase().deleteHat(menuInventory.getName(), slot);
    }
}
