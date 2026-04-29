package cosmeticsOG.editor.menus;

import net.trueog.utilitiesog.UtilitiesOG;
import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.locale.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EditorNodeMainMenu extends EditorMainMenu {

    public EditorNodeMainMenu(CosmeticsOG core, EditorMenuManager menuManager, Player owner) {

        super(core, menuManager, owner);

    }

    @Override
    public void build() {

        this.inventory = Bukkit.createInventory(null, 45,
                UtilitiesOG.trueogColorize(Message.EDITOR_MAIN_MENU_TITLE.getValue()));
        this.particleButtonSlot = 13;
        this.trackingButtonSlot = 19;
        this.countButtonSlot = 15;
        this.equipButtonSlot = 42;

        buildSection();

        setButton(38, backButtonItem, backButtonAction);

    }

}
