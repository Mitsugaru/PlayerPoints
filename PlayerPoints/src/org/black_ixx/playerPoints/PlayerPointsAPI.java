package org.black_ixx.playerPoints;

import org.black_ixx.playerPoints.event.PlayerPointsChangeEvent;
import org.black_ixx.playerPoints.event.PlayerPointsResetEvent;

/**
 * Static API hook.
 */
public class PlayerPointsAPI {
   /**
    * Plugin instance.
    */
   private final PlayerPoints plugin;

   public PlayerPointsAPI(PlayerPoints p) {
      this.plugin = p;
   }

   /**
    * Give points to specified player.
    * 
    * @param Name
    *           of player
    * @param Amount
    *           of points to give
    * @return True if we successfully adjusted points, else false
    */
   public boolean give(String playername, int amount) {
      PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playername, amount);
      plugin.getServer().getPluginManager().callEvent(event);
      if(!event.isCancelled()) {
         final int total = look(playername.toLowerCase()) + event.getChange();
         return plugin.getStorageHandler().setPoints(playername.toLowerCase(), total);
      }
      return false;
   }

   /**
    * Take points from specified player. If the amount given is not already
    * negative, we make it negative.
    * 
    * @param Name
    *           of player
    * @param Amount
    *           of points to give
    * @return True if we successfully adjusted points, else false
    */
   public boolean take(String playername, int amount) {
      final int points = look(playername);
      int take = amount;
      if(take > 0) {
         take *= -1;
      }
      if((points + take) < 0) {
         return false;
      }
      return give(playername, take);
   }

   /**
    * Look up the current points of a player, if available. If player does not
    * exist in the config file, then we default to 0.
    * 
    * @param Player
    *           name
    * @return Points that the player has
    */
   public int look(String playername) {
      return plugin.getStorageHandler().getPoints(playername.toLowerCase());
   }

   /**
    * Transfer points from one player to another. Attempts to take the points,
    * if available, from the source account and then attempts to give same
    * amount to target account. If it takes, but does not give, then we return
    * the amount back to the source since it failed.
    * 
    * @param Name
    *           of player
    * @param Amount
    *           of points to give
    * @return True if we successfully adjusted points, else false
    */
   public boolean pay(String source, String target, int amount) {
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
    * @param Name
    *           of player
    * @param Amount
    *           of points that it should be set to
    * @return True if successful
    */
   public boolean set(String playername, int amount) {
      PlayerPointsChangeEvent event = new PlayerPointsChangeEvent(playername, amount - look(playername));
      plugin.getServer().getPluginManager().callEvent(event);
      if(!event.isCancelled()) {
         return plugin.getStorageHandler().setPoints(playername.toLowerCase(), event.getChange());
      }
      return false;
   }

   /**
    * Reset a player's points by removing their entry from the config.
    * 
    * @param Name
    *           of player
    * @return True if successful
    */
   public boolean reset(String playername) {
      PlayerPointsResetEvent event = new PlayerPointsResetEvent(playername);
      plugin.getServer().getPluginManager().callEvent(event);
      if(!event.isCancelled()) {
         return plugin.getStorageHandler().setPoints(playername.toLowerCase(), event.getChange());
      }
      return false;
   }
}
