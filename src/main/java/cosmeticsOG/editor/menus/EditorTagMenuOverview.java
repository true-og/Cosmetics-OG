package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.compatibility.CompatibleMaterial;
import cosmeticsOG.database.Database.DataType;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.particles.properties.ParticleTag;
import cosmeticsOG.ui.AbstractListMenu;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.StringUtil;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditorTagMenuOverview extends AbstractListMenu {

    private final EditorMenuManager editorManager;
    private final Hat targetHat;
    private final String tagTitle = Message.EDITOR_TAG_OVERVIEW_MENU_TAG_TITLE.getValue();
    private final ItemStack emptyItem = ItemUtil.createItem(
            CompatibleMaterial.BARRIER.getMaterial(), 1, Message.EDITOR_TAG_OVERVIEW_MENU_EMPTY.getValue());

    private boolean isModified = false;

    public EditorTagMenuOverview(CosmeticsOG core, EditorMenuManager menuManager, Player owner) {

        super(core, menuManager, owner, true);

        this.editorManager = menuManager;
        this.targetHat = menuManager.getBaseHat();
        this.totalPages = 1;

        setMenu(
                0,
                Bukkit.createInventory(
                        null, 54, Utils.legacySerializerAnyCase(Message.EDITOR_TAG_OVERVIEW_MENU_TITLE.getValue())));

        build();
    }

    @Override
    public void insertEmptyItem() {

        setItem(0, 22, emptyItem);
    }

    @Override
    public void removeEmptyItem() {

        setItem(0, 22, null);
    }

    @Override
    protected void build() {

        setButton(0, 46, backButtonItem, backButtonAction);

        List<TextComponent> overviewTitleTextComponents =
                StringUtil.parseDescription(Message.EDITOR_TAG_OVERVIEW_MENU_INFO.getValue()).stream()
                        .map(component ->
                                (TextComponent) component) // Convert list of Components to list of TextComponents.
                        .collect(Collectors.toList());

        setItem(
                0,
                49,
                ItemUtil.createItem(
                        CompatibleMaterial.REDSTONE_TORCH.getMaterial(),
                        1,
                        Message.EDITOR_TAG_OVERVIEW_MENU_INFO_TITLE.getValue(),
                        overviewTitleTextComponents));

        // Add tag.
        ItemStack addItem = ItemUtil.createItem(
                CompatibleMaterial.TURTLE_HELMET.getMaterial(), 1, Message.EDITOR_TAG_OVERVIEW_MENU_ADD_TAG.getValue());
        setButton(0, 52, addItem, (event, slot) -> {
            EditorTagMenu editorTagMenu = new EditorTagMenu(core, editorManager, owner, (tagName) -> {
                if (tagName == null) {

                    return;
                }

                ParticleTag tag = (ParticleTag) tagName;
                List<ParticleTag> tags = targetHat.getTags();
                if (tags.contains(tag)) {

                    menuManager.closeCurrentMenu();

                    return;
                }

                int size = tags.size();
                if (size < 28) {

                    List<TextComponent> overviewDescriptionTextComponents =
                            StringUtil.parseDescription(Message.EDITOR_TAG_OVERVIEW_MENU_TAG_DESCRIPTION.getValue())
                                    .stream()
                                    .map(component -> (TextComponent) component)
                                    .collect(Collectors.toList());

                    ItemStack tagItem = ItemUtil.createItem(
                            CompatibleMaterial.MUSHROOM_STEW.getMaterial(),
                            1,
                            tagTitle.replace("{1}", tag.getDisplayName()),
                            overviewDescriptionTextComponents);

                    setItem(0, getNormalIndex(size, 10, 2), tagItem);

                    tags.add(tag);
                }

                if (isEmpty) {

                    setEmpty(false);
                }

                isModified = true;

                menuManager.closeCurrentMenu();
            });

            menuManager.addMenu(editorTagMenu);

            editorTagMenu.open();

            return MenuClickResult.NEUTRAL;
        });

        // Edit action.
        final MenuAction editAction = (event, slot) -> {
            if (event.isShiftRightClick()) {

                deleteSlot(0, slot);

                return MenuClickResult.NEGATIVE;
            }

            return MenuClickResult.NONE;
        };

        for (int i = 0; i < 28; i++) {

            setAction(getNormalIndex(i, 10, 2), editAction);
        }

        // Tags.
        List<ParticleTag> tags = targetHat.getTags();

        if (tags.isEmpty()) {

            setEmpty(true);

            return;
        }

        for (int i = 0; i < tags.size(); i++) {

            ParticleTag tag = tags.get(i);
            String titleString = Utils.legacySerializerAnyCase(tagTitle.replace("{1}", tag.getDisplayName()))
                    .content();
            List<TextComponent> textComponents =
                    StringUtil.parseDescription(Message.EDITOR_TAG_OVERVIEW_MENU_TAG_DESCRIPTION.getValue()).stream()
                            .map(component -> (TextComponent) component)
                            .collect(Collectors.toList());

            ItemStack tagItem =
                    ItemUtil.createItem(CompatibleMaterial.MUSHROOM_STEW.getMaterial(), 1, titleString, textComponents);

            setItem(0, getNormalIndex(i, 10, 2), tagItem);
        }
    }

    @Override
    public void onClose(boolean forced) {

        if (isModified) {

            core.getDatabase().saveMetaData(editorManager.getMenuName(), targetHat, DataType.TAGS, -1);
        }
    }

    @Override
    public void onTick(int ticks) {}

    @Override
    public void deleteSlot(int page, int slot) {

        super.deleteSlot(page, slot);

        int clampedIndex = getClampedIndex(slot, 10, 2);
        List<ParticleTag> tags = targetHat.getTags();

        tags.remove(clampedIndex);

        if (tags.isEmpty()) {

            setEmpty(true);
        }

        isModified = true;
    }
}
