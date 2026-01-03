package cosmeticsOG.editor.menus;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.editor.EditorLore;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.ui.AbstractStaticMenu;
import cosmeticsOG.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditorDurationMenu extends AbstractStaticMenu {

    private final Hat targetHat;
    private final MenuCallback callback;

    public EditorDurationMenu(CosmeticsOG core, EditorMenuManager menuManager, Player owner, MenuCallback callback) {

        super(core, menuManager, owner);

        this.targetHat = menuManager.getBaseHat();
        this.callback = callback;
        this.inventory = Bukkit.createInventory(null, 27,
                Utils.legacySerializerAnyCase(Message.EDITOR_DURATION_MENU_TITLE.getValue()));

        build();

    }

    @Override
    protected void build() {

        setButton(12, backButtonItem, backButtonAction);

        ItemStack durationItem = ItemUtil.createItem(Material.MAP, 1,
                Message.EDITOR_DURATION_MENU_SET_DURATION.getValue());
        EditorLore.updateDurationDescription(durationItem, targetHat.getDemoDuration(),
                Message.EDITOR_DURATION_MENU_DESCRIPTION);
        setButton(14, durationItem, (event, slot) -> {

            int normalClick = event.isLeftClick() ? 20 : -20;
            int shiftClick = event.isShiftClick() ? 30 : 1;
            int modifier = normalClick * shiftClick;
            int duration = targetHat.getDemoDuration() + modifier;

            targetHat.setDemoDuration(duration);

            EditorLore.updateDurationDescription(getItem(14), targetHat.getDemoDuration(),
                    Message.EDITOR_DURATION_MENU_DESCRIPTION);

            return event.isLeftClick() ? MenuClickResult.POSITIVE : MenuClickResult.NEGATIVE;

        });

    }

    @Override
    public void onClose(boolean forced) {

        if (!forced) {

            callback.onCallback();

        }

    }

    @Override
    public void onTick(int ticks) {

    }

}
