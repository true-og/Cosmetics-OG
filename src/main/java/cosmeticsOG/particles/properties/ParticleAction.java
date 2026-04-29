package cosmeticsOG.particles.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cosmeticsOG.CosmeticsOG;
import cosmeticsOG.events.HatEquipEvent;
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
import net.kyori.adventure.text.Component;
import net.trueog.diamondbankog.DiamondBankException;
import net.trueog.diamondbankog.balance.shard.PlayerShards;
import net.trueog.utilitiesog.UtilitiesOG;

public enum ParticleAction {

    EQUIP(0), TOGGLE(1), CLOSE(2), DUMMY(3), OVERRIDE(4), CLEAR(5), COMMAND(6, true), OPEN_MENU(7, true),
    OPEN_MENU_PERMISSION(8, true), PURCHASE_CONFIRM(9, false, true), PURCHASE_DENY(10, false, true),
    PURCHASE_ITEM(11, false, true), MIMIC(12), DEMO(13, true), ACTIVE_PARTICLES(14);

    private final CosmeticsOG core = CosmeticsOG.instance;

    private final int id;
    private final boolean hasData;
    private final boolean isHidden;

    private static final Map<Integer, ParticleAction> actionID = new HashMap<>();

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

        return StringUtils.lowerCase(this.toString());

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

        return UtilitiesOG.stripFormatting(getDisplayName());

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

        final PlayerState playerState = core.getPlayerState(player);
        final boolean canClose = SettingsManager.CLOSE_MENU_ON_EQUIP.getBoolean();

        switch (this) {

            case EQUIP -> {

                final HatEquipEvent event = new HatEquipEvent(player, hat);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {

                    // Remove an already equipped hat.
                    if (checkAgainstEquippedHats(hat, slot, playerState, inventory)) {

                        return;

                    }

                    // Check to see if we have already purchased this hat.
                    if (playerState.hasPurchased(hat)) {

                        core.getParticleManager().equipHat(player, hat);

                        if (canClose) {

                            PlayerUtil.closeInventory(player);

                        }

                        return;

                    }

                    final boolean canUsePermission = SettingsManager.FLAG_PERMISSION.getBoolean();
                    final boolean canUseCurrency = SettingsManager.isEconomyEnabled();
                    final boolean canUseExp = SettingsManager.FLAG_EXPERIENCE.getBoolean();

                    if (canUsePermission) {

                        if (hat.isLocked()) {

                            // Only show the permission denied message if currency and exp are also
                            // disabled.
                            if (!canUseCurrency && !canUseExp) {

                                final String deniedMessage = hat.getPermissionDeniedDisplayMessage();

                                if (!"".equals(deniedMessage)) {

                                    CosmeticsOG.chatMessage(player, deniedMessage);

                                } else {

                                    CosmeticsOG.chatMessage(player, Message.HAT_NO_PERMISSION.getValue());

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

                    if (canUseCurrency) {

                        openPurchaseMenuIfAffordable(player, playerState, hat, canClose);
                        return;

                    }

                    long playerBalance = -1;
                    if (canUseExp) {

                        playerBalance = player.getLevel();

                    }

                    if (playerBalance > -1) {

                        if (playerBalance < hat.getPrice()) {

                            sendInsufficientFunds(player);

                            if (canClose) {

                                PlayerUtil.closeInventory(player);

                            }

                            return;

                        } else {

                            openPendingPurchaseMenu(player, playerState, hat);

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

            }
            case TOGGLE -> {

                final List<Hat> hats = playerState.getActiveHats();
                if (hats.size() > 0) {

                    final boolean initialToggle = hats.get(0).isHidden();
                    hats.forEach(h -> h.setHidden(!initialToggle));

                }

            }
            case CLOSE -> PlayerUtil.closeInventory(player);
            case OVERRIDE -> {

                // Remove an already equipped hat.
                if (checkAgainstEquippedHats(hat, slot, playerState, inventory)) {

                    return;

                }

                core.getParticleManager().equipHat(player, hat);
                if (canClose) {

                    PlayerUtil.closeInventory(player);

                }

            }
            case CLEAR -> {

                core.getPlayerState(player).clearActiveHats();
                CosmeticsOG.chatMessage(player, Message.COMMAND_CLEAR_SUCCESS.getValue());

            }
            case COMMAND -> {

                if (!"".equals(argument)) {

                    PlayerUtil.runNextTick(() -> {

                        player.closeInventory();
                        player.performCommand(argument);

                    });

                }

            }
            case OPEN_MENU_PERMISSION, OPEN_MENU -> {

                if (this == OPEN_MENU_PERMISSION && !player.hasPermission(hat.getFullPermission())) {

                    break;

                }

                final StaticMenuManager staticManager = (StaticMenuManager) playerState.getMenuManager();
                if (staticManager == null) {

                    return;

                }

                final AbstractMenu menu = staticManager.getMenuFromCache(argument);
                if (menu != null) {

                    menu.open();

                } else {

                    final MenuInventory menuInventory = core.getDatabase().loadInventory(argument, playerState);
                    if (menuInventory == null) {

                        CosmeticsOG.chatMessage(player,
                                Message.COMMAND_ERROR_UNKNOWN_MENU.getValue().replace("{1}", argument));
                        break;

                    }

                    final StaticMenu staticMenu = new StaticMenu(core, staticManager, player, menuInventory);
                    staticManager.addMenu(staticMenu);

                    staticMenu.open();

                }

            }
            case PURCHASE_CONFIRM -> {

                final Hat pendingHat = playerState.getPendingPurchase();
                // Go back to the previous menu if the pending hat is null
                if (pendingHat == null) {

                    gotoPreviousMenu(playerState);
                    return;

                }

                final int price = pendingHat.getPrice();
                boolean purchased = false;
                if (SettingsManager.isEconomyEnabled()) {

                    playerState.setPendingPurchase(null);
                    purchaseWithDiamondBank(player, playerState, pendingHat, price);
                    return;

                } else if (SettingsManager.FLAG_EXPERIENCE.getBoolean()) {

                    final double currentBalance = player.getLevel();
                    final double newBalance = currentBalance - price;
                    player.setLevel((int) newBalance);

                    purchased = true;

                }

                if (purchased) {

                    finishPurchase(player, playerState, pendingHat);

                }

            }
            case PURCHASE_DENY -> {

                playerState.setPendingPurchase(null);
                gotoPreviousMenu(playerState);

            }
            case DEMO -> {

                final HatEquipEvent event = new HatEquipEvent(player, hat);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {

                    hat.setPermanent(false);
                    core.getParticleManager().equipHat(player, hat);

                }

            }
            case ACTIVE_PARTICLES -> {

                final StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
                final EquippedParticlesMenu particlesMenu = new EquippedParticlesMenu(core, staticManager, player,
                        true);
                staticManager.addMenu(particlesMenu);
                particlesMenu.open();

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

            return ParticleAction.valueOf(StringUtils.upperCase(name));

        } catch (IllegalArgumentException e) {

            return fallback;

        }

    }

    private void gotoPreviousMenu(PlayerState playerState) {

        final StaticMenuManager staticManager = (StaticMenuManager) playerState.getMenuManager();
        final AbstractMenu menu = staticManager.getPreviousOpenMenu();
        if (menu == null) {

            return;

        }

        menu.open();

    }

    private boolean checkAgainstEquippedHats(Hat hat, int slot, PlayerState playerState, Inventory inventory) {

        if (!playerState.isEquipped(hat)) {

            return false;

        }

        playerState.removeHat(hat);

        final ItemStack item = inventory.getItem(slot);
        ItemUtil.stripHighlight(item);

        // Convert List<String> to List<Component>.
        final List<Component> components = hat.getCachedDescription().stream().map(Component::text)
                .collect(Collectors.toList());
        ItemUtil.setItemDescription(item, components);

        return true;

    }

    private void openPurchaseMenuIfAffordable(Player player, PlayerState playerState, Hat hat, boolean canClose) {

        if (!isDiamondBankAvailable()) {

            return;

        }

        final UUID uuid = player.getUniqueId();
        final long priceShards = hat.getPrice();
        getSpendableShardBalance(uuid).whenComplete((playerBalance, throwable) -> {

            if (throwable != null) {

                handleDiamondBankFailure(player, throwable);
                return;

            }

            runOnMainThread(() -> {

                if (!player.isOnline()) {

                    return;

                }

                if (playerBalance < priceShards) {

                    sendInsufficientFunds(player);
                    if (canClose) {

                        PlayerUtil.closeInventory(player);

                    }

                    return;

                }

                openPendingPurchaseMenu(player, playerState, hat);

            });

        });

    }

    private void openPendingPurchaseMenu(Player player, PlayerState playerState, Hat hat) {

        final StaticMenuManager staticManager = core.getMenuManagerFactory().getStaticMenuManager(playerState);
        playerState.setPendingPurchase(hat);

        final MenuInventory purchaseInventory = core.getDatabase().getPurchaseMenu(playerState);
        final AbstractMenu pendingPurchaseMenu;
        if (purchaseInventory != null) {

            pendingPurchaseMenu = new StaticMenu(core, staticManager, player, purchaseInventory);

        } else {

            pendingPurchaseMenu = new StaticMenu(core, staticManager, player,
                    PendingPurchaseMenu.defaultPendingPurchaseInventory.clone());

        }

        staticManager.addMenu(pendingPurchaseMenu);
        pendingPurchaseMenu.open();

    }

    private void purchaseWithDiamondBank(Player player, PlayerState playerState, Hat pendingHat, long priceShards) {

        if (!isDiamondBankAvailable()) {

            return;

        }

        final UUID uuid = player.getUniqueId();
        final String notes = "Purchased " + UtilitiesOG.stripFormatting(pendingHat.getDisplayName());
        consumePlayerShards(uuid, priceShards, notes).whenComplete((ignored, throwable) -> {

            if (throwable != null) {

                handleDiamondBankFailure(player, throwable);
                return;

            }

            runOnMainThread(() -> finishPurchase(player, playerState, pendingHat));

        });

    }

    private void finishPurchase(Player player, PlayerState playerState, Hat pendingHat) {

        if (!player.isOnline()) {

            return;

        }

        playerState.setPendingPurchase(null);
        playerState.addPurchasedHat(pendingHat);

        core.getDatabase().savePlayerPurchase(player.getUniqueId(), pendingHat);
        core.getParticleManager().equipHat(player, pendingHat);

        if (SettingsManager.CLOSE_MENU_ON_EQUIP.getBoolean()) {

            PlayerUtil.closeInventory(player);

        } else {

            gotoPreviousMenu(playerState);

        }

    }

    private CompletableFuture<Long> getSpendableShardBalance(UUID uuid) {

        return callDiamondBankAsync(() -> {

            final PlayerShards shards = CosmeticsOG.getDiamondBankAPI().getAllShards(uuid);
            return shards.getBank() + shards.getInventory();

        });

    }

    private CompletableFuture<Void> consumePlayerShards(UUID uuid, long priceShards, String notes) {

        return callDiamondBankAsync(() -> {

            CosmeticsOG.getDiamondBankAPI().consumeFromPlayer(uuid, priceShards, "Cosmetics-OG purchase", notes);
            return null;

        });

    }

    private <T> CompletableFuture<T> callDiamondBankAsync(DiamondBankCall<T> call) {

        final CompletableFuture<T> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(CosmeticsOG.getInstance(), () -> {

            try {

                future.complete(call.execute());

            } catch (Throwable t) {

                future.completeExceptionally(t);

            }

        });

        return future;

    }

    private boolean isDiamondBankAvailable() {

        if (CosmeticsOG.getDiamondBankAPI() != null) {

            return true;

        }

        CosmeticsOG.disableSelf("Could not find DiamondBank-OG! Disabling Cosmetics-OG...");
        return false;

    }

    private void handleDiamondBankFailure(Player player, Throwable throwable) {

        final Throwable cause = unwrapCompletionException(throwable);
        if (cause instanceof DiamondBankException.EconomyDisabledException) {

            CosmeticsOG.disableSelf("The DiamondBank-OG economy is disabled - disabling Cosmetics-OG!");
            return;

        }

        if (cause instanceof DiamondBankException.InsufficientFundsException
                || cause instanceof DiamondBankException.InsufficientBalanceException)
        {

            runOnMainThread(() -> {

                if (player.isOnline()) {

                    sendInsufficientFunds(player);

                }

            });
            return;

        }

        if (cause instanceof DiamondBankException.PlayerNotOnlineException) {

            return;

        }

        UtilitiesOG.logToConsole(CosmeticsOG.getPrefix(),
                "DiamondBank-OG transaction failed: " + cause.getClass().getSimpleName());
        if (CosmeticsOG.debugging) {

            cause.printStackTrace();

        }

    }

    private Throwable unwrapCompletionException(Throwable throwable) {

        if (throwable instanceof CompletionException && throwable.getCause() != null) {

            return throwable.getCause();

        }

        return throwable;

    }

    private void sendInsufficientFunds(Player player) {

        final String currency = SettingsManager.CURRENCY.getString();
        CosmeticsOG.chatMessage(player, Message.INSUFFICIENT_FUNDS.getValue().replace("{1}", currency));

    }

    private void runOnMainThread(Runnable runnable) {

        Bukkit.getScheduler().runTask(CosmeticsOG.getInstance(), runnable);

    }

    @FunctionalInterface
    private interface DiamondBankCall<T> {

        T execute() throws Exception;

    }

}
