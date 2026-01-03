package cosmeticsOG.particles.properties;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.Utils;
import cosmeticsOG.events.HatEquipEvent;
import cosmeticsOG.hooks.CurrencyHook;
import cosmeticsOG.locale.Message;
import cosmeticsOG.managers.SettingsManager;
import cosmeticsOG.particles.Hat;
import cosmeticsOG.player.PlayerState;
import cosmeticsOG.ui.AbstractMenu;
import cosmeticsOG.ui.EquippedParticlesMenu;
import cosmeticsOG.ui.MenuInventory;
import cosmeticsOG.ui.PendingPurchaseMenu;
import cosmeticsOG.ui.StaticMenu;
import cosmeticsOG.ui.StaticMenuManager;
import cosmeticsOG.util.ItemUtil;
import cosmeticsOG.util.PlayerUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public enum ParticleAction {

    EQUIP(0), TOGGLE(1), CLOSE(2), DUMMY(3), OVERRIDE(4), CLEAR(5), COMMAND(6, true), OPEN_MENU(7, true),
    OPEN_MENU_PERMISSION(8, true), PURCHASE_CONFIRM(9, false, true), PURCHASE_DENY(10, false, true),
    PURCHASE_ITEM(11, false, true), MIMIC(12), DEMO(13, true), ACTIVE_PARTICLES(14);

    private final CosmeticsOG core = CosmeticsOG.instance;

    private final int id;
    private final boolean hasData;
    private final boolean isHidden;

    private static final Map<Integer, ParticleAction> actionID = new HashMap<Integer, ParticleAction>();

    static {

        for (ParticleAction pa : values()) {

            actionID.put(pa.id, pa);

        }

    }

    private ParticleAction(final int id, final boolean hasData, final boolean isHidden) {

        this.id = id;
        this.hasData = hasData;
        this.isHidden = isHidden;

    }

    private ParticleAction(final int id, final boolean hasData) {

        this(id, hasData, false);

    }

    private ParticleAction(final int id) {

        this(id, false, false);

    }

    /**
     * Get this ParticleActions id
     * 
     * @return
     */
    public int getID() {

        return id;

    }

    public String getName() {

        return this.toString().toLowerCase();

    }

    /**
     * Returns true if this action relies on additional data
     * 
     * @return
     */
    public boolean hasData() {

        return hasData;

    }

    /**
     * Checks to see if this Action should be hidden from the menu editor
     * 
     * @return
     */
    public boolean isHidden() {

        return isHidden;

    }

    /**
     * Get the name of this ParticleAction
     * 
     * @return The name of this action as defined in the current messages.yml file
     */
    public String getDisplayName() {

        final String key = "ACTION_" + toString() + "_NAME";
        try {

            return Message.valueOf(key).getValue();

        } catch (IllegalArgumentException e) {

            return "";

        }

    }

    /**
     * Get the name of this ParticleAction without color codes
     * 
     * @return
     */
    public String getStrippedName() {

        return Utils.stripColors(getDisplayName());

    }

    /**
     * Get the description of this ParticleAction
     * 
     * @return The description of this action as defined in the current messages.yml
     *         file
     */
    public String getDescription() {

        final String key = "ACTION_" + toString() + "_DESCRIPTION";
        try {

            return Message.valueOf(key).getValue();

        } catch (IllegalArgumentException e) {

            return "";

        }

    }

    /**
     * Perform this action
     */
    @SuppressWarnings("incomplete-switch")
    public void onClick(Player player, Hat hat, int slot, Inventory inventory, String argument) {

        PlayerState playerState = core.getPlayerState(player);
        boolean canClose = SettingsManager.CLOSE_MENU_ON_EQUIP.getBoolean();

        switch (this) {

            case EQUIP: {

                HatEquipEvent event = new HatEquipEvent(player, hat);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {

                    // Remove an already equipped hat
                    if (checkAgainstEquippedHats(hat, slot, playerState, inventory)) {

                        return;

                    }

                    // Check to see if we have already purchased this hat
                    if (playerState.hasPurchased(hat)) {

                        core.getParticleManager().equipHat(player, hat);

                        if (canClose) {

                            PlayerUtil.closeInventory(player);

                        }

                        return;

                    }

                    boolean canUsePermission = SettingsManager.FLAG_PERMISSION.getBoolean();
                    boolean canUseCurrency = SettingsManager.isEconomyEnabled();
                    boolean canUseExp = SettingsManager.FLAG_EXPERIENCE.getBoolean();

                    if (canUsePermission) {

                        if (hat.isLocked()) {

                            // Only show the permission denied message if vault and exp are also disabled.
                            if (!canUseCurrency && !canUseExp) {

                                String deniedMessage = hat.getPermissionDeniedDisplayMessage();

                                if (!deniedMessage.equals("")) {

                                    Utils.cosmeticsOGPlaceholderMessage(player, deniedMessage);

                                } else {

                                    Utils.cosmeticsOGPlaceholderMessage(player, Message.HAT_NO_PERMISSION.getValue());

                                }

                                if (canClose) {

                                    PlayerUtil.closeInventory(player);

                                }

                                return;

                            }

                        }

                        // We have permission.
                        else {

                            core.getParticleManager().equipHat(player, hat);

                            if (canClose) {

                                PlayerUtil.closeInventory(player);

                            }

                            return;

                        }

                    }

                    double playerBalance = -1;

                    if (canUseCurrency) {

                        CurrencyHook currencyHook = core.getHookManager().getCurrencyHook();
                        if (currencyHook != null && currencyHook.isEnabled()) {

                            playerBalance = currencyHook.getBalance(player);

                        }

                    } else if (canUseExp) {

                        playerBalance = player.getLevel();

                    }

                    if (playerBalance > -1) {

                        if (playerBalance < hat.getPrice()) {

                            String currency = SettingsManager.CURRENCY.getString();
                            Utils.cosmeticsOGPlaceholderMessage(player,
                                    Message.INSUFFICIENT_FUNDS.getValue().replace("{1}", currency));

                            if (canClose) {

                                PlayerUtil.closeInventory(player);

                            }

                            return;

                        } else {

                            StaticMenuManager staticManager = core.getMenuManagerFactory()
                                    .getStaticMenuManager(playerState);
                            playerState.setPendingPurchase(hat);

                            MenuInventory purchaseInventory = core.getDatabase().getPurchaseMenu(playerState);
                            AbstractMenu pendingPurchaseMenu;

                            if (purchaseInventory != null) {

                                pendingPurchaseMenu = new StaticMenu(core, staticManager, player, purchaseInventory);

                            } else {

                                pendingPurchaseMenu = new StaticMenu(core, staticManager, player,
                                        PendingPurchaseMenu.defaultPendingPurchaseInventory.clone());

                            }

                            staticManager.addMenu(pendingPurchaseMenu);
                            pendingPurchaseMenu.open();

                        }

                    } else {

                        if (!canUsePermission && !canUseCurrency && !canUseExp) {

                            core.getParticleManager().equipHat(player, hat);

                        }

                        if (canClose) {

                            PlayerUtil.closeInventory(player);

                        }

                    }

                }

                break;

            }

            case TOGGLE: {

                List<Hat> hats = playerState.getActiveHats();

                // Toggles all active hats based on the first hats toggle state
                // Players can toggle individual hats through the hat manager

                if (hats.size() > 0) {

                    boolean initialToggle = hats.get(0).isHidden();
                    for (Hat h : hats) {

                        h.setHidden(!initialToggle);

                    }

                }

                break;

            }

            case CLOSE: {

                PlayerUtil.closeInventory(player);
                break;

            }

            case OVERRIDE: {

                // Remove an already equipped hat
                if (checkAgainstEquippedHats(hat, slot, playerState, inventory)) {

                    return;

                }

                core.getParticleManager().equipHat(player, hat);
                if (canClose) {

                    PlayerUtil.closeInventory(player);

                }

                break;

            }

            case CLEAR: {

                core.getPlayerState(player).clearActiveHats();
                Utils.cosmeticsOGPlaceholderMessage(player, Message.COMMAND_CLEAR_SUCCESS.getValue());
                break;

            }

            case COMMAND: {

                if (!argument.equals("")) {

                    PlayerUtil.runNextTick(() -> {

                        player.closeInventory();
                        player.performCommand(argument);

                    });

                }

                break;

            }

            case OPEN_MENU_PERMISSION:
            case OPEN_MENU: {

                if (this == OPEN_MENU_PERMISSION && !player.hasPermission(hat.getFullPermission())) {

                    break;

                }

                StaticMenuManager staticManager = (StaticMenuManager) playerState.getMenuManager();
                if (staticManager == null) {

                    return;

                }

                AbstractMenu menu = staticManager.getMenuFromCache(argument);
                if (menu != null) {

                    menu.open();

                } else {

                    MenuInventory menuInventory = core.getDatabase().loadInventory(argument, playerState);
                    if (menuInventory == null) {

                        Utils.cosmeticsOGPlaceholderMessage(player,
                                Message.COMMAND_ERROR_UNKNOWN_MENU.getValue().replace("{1}", argument));
                        break;

                    }

                    StaticMenu staticMenu = new StaticMenu(core, staticManager, player, menuInventory);
                    staticManager.addMenu(staticMenu);

                    staticMenu.open();

                }

                break;

            }

            case PURCHASE_CONFIRM: {

                Hat pendingHat = playerState.getPendingPurchase();

                // Go back to the previous menu if the pending hat is null
                if (pendingHat == null) {

                    gotoPreviousMenu(playerState);

                }

                int price = pendingHat.getPrice();
                boolean purchased = false;

                if (SettingsManager.isEconomyEnabled()) {

                    CurrencyHook currencyHook = core.getHookManager().getCurrencyHook();
                    if (currencyHook != null && currencyHook.isEnabled()) {

                        if (currencyHook.withdraw(player, price)) {

                            purchased = true;

                        }

                    }

                } else if (SettingsManager.FLAG_EXPERIENCE.getBoolean()) {

                    double currentBalance = player.getLevel();
                    double newBalance = currentBalance - price;
                    player.setLevel((int) newBalance);

                    purchased = true;

                }

                if (purchased) {

                    playerState.addPurchasedHat(pendingHat);

                    core.getDatabase().savePlayerPurchase(player.getUniqueId(), pendingHat);
                    core.getParticleManager().equipHat(player, pendingHat);

                    if (SettingsManager.CLOSE_MENU_ON_EQUIP.getBoolean()) {

                        PlayerUtil.closeInventory(player);

                    } else {

                        gotoPreviousMenu(playerState);

                    }

                }

                break;

            }

            case PURCHASE_DENY: {

                gotoPreviousMenu(playerState);
                break;

            }

            case DEMO: {

                HatEquipEvent event = new HatEquipEvent(player, hat);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {

                    hat.setPermanent(false);
                    core.getParticleManager().equipHat(player, hat);

                }

                break;

            }

            case ACTIVE_PARTICLES: {

                StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
                EquippedParticlesMenu particlesMenu = new EquippedParticlesMenu(core, staticManager, player, true);

                staticManager.addMenu(particlesMenu);
                particlesMenu.open();
                break;

            }

        }

    }

    /**
     * Returns the ParticleAction associated with this id
     * 
     * @param id
     * @return
     */
    public static ParticleAction fromId(int id) {

        if (actionID.containsKey(id)) {

            return actionID.get(id);

        }

        return EQUIP;

    }

    /**
     * Returns the ParticleAction that matches this name
     * 
     * @param name
     * @return
     */
    public static ParticleAction fromName(String name, ParticleAction fallback) {

        if (name == null) {

            return fallback;

        }

        try {

            return ParticleAction.valueOf(name.toUpperCase());

        } catch (IllegalArgumentException e) {

            return fallback;

        }

    }

    private void gotoPreviousMenu(PlayerState playerState) {

        StaticMenuManager staticManager = (StaticMenuManager) playerState.getMenuManager();
        AbstractMenu menu = staticManager.getPreviousOpenMenu();

        if (menu == null) {

            return;

        }

        menu.open();

    }

    private boolean checkAgainstEquippedHats(Hat hat, int slot, PlayerState playerState, Inventory inventory) {

        if (playerState.isEquipped(hat)) {

            playerState.removeHat(hat);
            ItemStack item = inventory.getItem(slot);

            ItemUtil.stripHighlight(item);

            // Convert List<String> to List<Component>.
            List<Component> components = hat.getCachedDescription().stream().map(Component::text)
                    .collect(Collectors.toList());

            ItemUtil.setItemDescription(item, components);
            return true;

        }

        return false;

    }

}
