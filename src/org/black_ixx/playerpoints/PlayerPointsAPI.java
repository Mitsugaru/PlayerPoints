package org.black_ixx.playerpoints;

import java.util.UUID;

import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.black_ixx.playerpoints.event.PlayerPointsResetEvent;
import org.black_ixx.playerpoints.storage.StorageHandler;

/**
 * API hook.
 */
public class PlayerPointsAPI {
    /**
     * Plugin instance.
     */
    private final PlayerPoints plugin;

    /**
     * Constructor
     * @param p - Player points plugin.
     */
    public PlayerPointsAPI(PlayerPoints p) {
        this.plugin = p;
    }

    /**
     * Give points to specified player.
     * 
     * @param playerId
     *            UUID of player
     * @param Amount
     *            of points to give
     * @return True if we successfully adjusted points, else false
     */
    public boolean give(UUID playerId, int amount) {
        PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId,
                amount);
        plugin.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            final int total = look(playerId)
                    + event.getChange();
            return plugin.getModuleForClass(StorageHandler.class).setPoints(
                    playerId.toString(), total);
        }
        return false;
    }

    /**
     * Take points from specified player. If the amount given is not already
     * negative, we make it negative.
     * 
     * @param playerId
     *            UUID of player
     * @param Amount
     *            of points to give
     * @return True if we successfully adjusted points, else false
     */
    public boolean take(UUID playerId, int amount) {
        final int points = look(playerId);
        int take = amount;
        if(take > 0) {
            take *= -1;
        }
        if((points + take) < 0) {
            return false;
        }
        return give(playerId, take);
    }

    /**
     * Look up the current points of a player, if available. If player does not
     * exist in the config file, then we default to 0.
     * 
     * @param playerId
     *            Player UUID
     * @return Points that the player has
     */
    public int look(UUID playerId) {
        return plugin.getModuleForClass(StorageHandler.class).getPoints(playerId.toString());
    }

    /**
     * Transfer points from one player to another. Attempts to take the points,
     * if available, from the source account and then attempts to give same
     * amount to target account. If it takes, but does not give, then we return
     * the amount back to the source since it failed.
     * 
     * @return True if we successfully adjusted points, else false
     */
    public boolean pay(UUID source, UUID target, int amount) {
        if(take(source, amount)) {
            if(give(target, amount)) {
                return true;
            } else {
                give(source, amount);
            }
        }
        return false;
    }

    /**
     * Sets a player's points to a given value.
     * 
     * @param playerId
     *            UUID of player
     * @param Amount
     *            of points that it should be set to
     * @return True if successful
     */
    public boolean set(UUID playerId, int amount) {
        PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playerId,
                amount - look(playerId));
        plugin.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            return plugin.getModuleForClass(StorageHandler.class).setPoints(
                    playerId.toString(),
                    look(playerId) + event.getChange());
        }
        return false;
    }

    /**
     * Reset a player's points by removing their entry from the config.
     * 
     * @param Name
     *            of player
     * @return True if successful
     */
    public boolean reset(UUID playerId) {
        PlayerPointsResetEvent event = new PlayerPointsResetEvent(playerId);
        plugin.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            return plugin.getModuleForClass(StorageHandler.class).setPoints(
                    playerId.toString(), event.getChange());
        }
        return false;
    }
}
