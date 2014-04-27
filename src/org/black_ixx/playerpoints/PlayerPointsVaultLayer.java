package org.black_ixx.playerpoints;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.black_ixx.playerpoints.services.IModule;
import org.black_ixx.playerpoints.storage.StorageHandler;
import org.bukkit.plugin.ServicePriority;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

/**
 * Vault economy layer for PlayerPoints.
 * 
 * @author Mitsugaru
 */
public class PlayerPointsVaultLayer implements Economy, IModule {

    /**
     * Plugin instance.
     */
    private PlayerPoints plugin;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public PlayerPointsVaultLayer(PlayerPoints plugin) {
        this.plugin = plugin;
    }

    @Override
    public void starting() {
        // Set to low priority. Allow for other, standard economy plugins to
        // supercede ours.
        plugin.getServer().getServicesManager()
                .register(Economy.class, this, plugin, ServicePriority.Low);
    }

    @Override
    public void closing() {
        plugin.getServer().getServicesManager().unregister(Economy.class, this);
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        StringBuilder sb = new StringBuilder();
        int points = (int) amount;
        sb.append(points + " ");
        if(points == 1) {
            sb.append(currencyNameSingular());
        } else {
            sb.append(currencyNamePlural());
        }
        return sb.toString();
    }

    @Override
    public String currencyNamePlural() {
        return "Points";
    }

    @Override
    public String currencyNameSingular() {
        return "Point";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return plugin.getModuleForClass(StorageHandler.class).playerEntryExists(playerName);
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        return plugin.getAPI().look(handleTranslation(playerName));
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        int current = plugin.getAPI().look(handleTranslation(playerName));
        return current >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        int points = (int) amount;
        boolean result = plugin.getAPI().take(handleTranslation(playerName), points);
        int balance = plugin.getAPI().look(handleTranslation(playerName));

        EconomyResponse response = null;
        if(result) {
            response = new EconomyResponse(amount, balance,
                    ResponseType.SUCCESS, null);
        } else {
            response = new EconomyResponse(amount, balance,
                    ResponseType.FAILURE, "Lack funds");
        }
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName,
            double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        int points = (int) amount;
        boolean result = plugin.getAPI().give(handleTranslation(playerName), points);
        int balance = plugin.getAPI().look(handleTranslation(playerName));

        EconomyResponse response = null;
        if(result) {
            response = new EconomyResponse(amount, balance,
                    ResponseType.SUCCESS, null);
        } else {
            response = new EconomyResponse(amount, balance,
                    ResponseType.FAILURE, null);
        }
        return response;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName,
            double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                "Does not handle banks.");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        // Assume true as the storage handler will dynamically add players.
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }
    
    private UUID handleTranslation(String name) {
        UUID id = null;
        try {
            UUID.fromString(name);
        } catch(IllegalArgumentException e) {
            id = plugin.translateNameToUUID(name);
        }
        return id;
    }

}
