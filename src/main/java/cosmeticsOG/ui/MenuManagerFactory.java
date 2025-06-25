package cosmeticsOG.ui;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.editor.EditorMenuManager;
import cosmeticsOG.editor.purchase.PurchaseMenuManager;
import cosmeticsOG.player.PlayerState;

public class MenuManagerFactory {

    private final CosmeticsOG core;

    public MenuManagerFactory(final CosmeticsOG core) {

        this.core = core;
    }

    /**
     * Returns a new StaticMenuManager class, unregisters any existing menu manager classes
     * @param playerState
     * @return
     */
    public StaticMenuManager getStaticMenuManager(PlayerState playerState) {

        MenuManager menuManager = playerState.getMenuManager();
        if (menuManager == null) {

            StaticMenuManager staticMenuManager = new StaticMenuManager(core, playerState.getOwner());
            playerState.setMenuManager(staticMenuManager);

            return staticMenuManager;

        } else if (!(menuManager instanceof StaticMenuManager)) {

            menuManager.willUnregister();

            StaticMenuManager staticMenuManager = new StaticMenuManager(core, playerState.getOwner());
            playerState.setMenuManager(staticMenuManager);

            return staticMenuManager;
        }

        return (StaticMenuManager) menuManager;
    }

    /**
     * Returns a new EditorMenuManager class, unregisters any existing menu manager classes
     * @param playerState
     * @return
     */
    public EditorMenuManager getEditorMenuManager(PlayerState playerState) {

        MenuManager menuManager = playerState.getMenuManager();
        if (menuManager == null) {

            EditorMenuManager editorManager = new EditorMenuManager(core, playerState.getOwner());
            playerState.setMenuManager(editorManager);

            return editorManager;

        } else if (!(menuManager instanceof EditorMenuManager)) {

            menuManager.willUnregister();

            EditorMenuManager editorManager = new EditorMenuManager(core, playerState.getOwner());
            playerState.setMenuManager(editorManager);

            return editorManager;
        }

        return (EditorMenuManager) menuManager;
    }

    /**
     * Returns a new PurchaseMenuManager class, unregistering any existing menu manager classes
     * @param playerState
     * @return
     */
    public PurchaseMenuManager getPurchaseMenuManager(PlayerState playerState) {

        MenuManager menuManager = playerState.getMenuManager();
        if (menuManager == null) {

            PurchaseMenuManager purchaseManager = new PurchaseMenuManager(core, playerState.getOwner());
            playerState.setMenuManager(purchaseManager);

            return purchaseManager;

        } else if (!(menuManager instanceof PurchaseMenuManager)) {

            menuManager.willUnregister();

            PurchaseMenuManager purchaseManager = new PurchaseMenuManager(core, playerState.getOwner());
            playerState.setMenuManager(purchaseManager);

            return purchaseManager;
        }

        return (PurchaseMenuManager) menuManager;
    }
}
