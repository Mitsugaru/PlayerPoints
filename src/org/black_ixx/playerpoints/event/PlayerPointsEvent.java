package org.black_ixx.playerpoints.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPointsEvent extends Event implements Cancellable {
   /**
    * Handler list.
    */
   private static final HandlerList handlers = new HandlerList();
   /**
    * Player name whose points is changing.
    */
   private final String playerName;
   /**
    * Amount their points are being changed by. Note, this is NOT the final
    * amount that the player's points balance will be. This is the amount to
    * modify their current balance by.
    */
   private int change;
   /**
    * Cancelled flag.
    */
   private boolean cancelled;

   /**
    * Constructor.
    * 
    * @param name
    *           - Name of player.
    * @param change
    *           - Amount of change that will apply to their current balance.
    */
   public PlayerPointsEvent(String name, int change) {
      this.playerName = name;
      this.change = change;
   }

   /**
    * Get the amount of points that the player's balance will change by.
    * 
    * @return Amount of change.
    */
   public int getChange() {
      return change;
   }

   /**
    * Set the amount of change that will be used to adjust the player's balance.
    * 
    * @param change
    *           - Amount of change.
    */
   public void setChange(int change) {
      this.change = change;
   }

   /**
    * Get the player name.
    * 
    * @return Name of Player.
    */
   public String getPlayerName() {
      return playerName;
   }

   @Override
   public boolean isCancelled() {
      return cancelled;
   }

   @Override
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   /**
    * Static method to get HandlerList.
    * 
    * @return HandlerList.
    */
   public static HandlerList getHandlerList() {
      return handlers;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

}
