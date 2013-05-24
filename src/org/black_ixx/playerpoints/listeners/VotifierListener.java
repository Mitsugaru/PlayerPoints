package org.black_ixx.playerpoints.listeners;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

/**
 * Listener for the votifier event.
 */
public class VotifierListener implements Listener {
   /**
    * Plugin instance.
    */
   private PlayerPoints plugin;
   /**
    * Amount to give.
    */
   private int amount = 100;
   /**
    * Whether the player has to be online or not.
    */
   private boolean online = false;

   /**
    * Constructor.
    * 
    * @param plugin
    *           - Plugin instance.
    */
   public VotifierListener(PlayerPoints plugin) {
      this.plugin = plugin;
      amount = plugin.getRootConfig().voteAmount;
      online = plugin.getRootConfig().voteOnline;
   }

   @EventHandler
   public void vote(VotifierEvent event) {
      if(event.getVote().getUsername() == null) {
         return;
      }
      final String name = event.getVote().getUsername();
      boolean pay = false;
      if(online) {
         final Player player = plugin.getServer().getPlayer(name);
         if(player != null && player.isOnline()) {
            pay = true;
            player.sendMessage("Thanks for voting on "
                  + event.getVote().getServiceName() + "!");
            player.sendMessage(this.amount
                  + " has been added to your Points balance.");
         }
      } else {
         pay = true;
      }
      if(pay) {
         plugin.getAPI().give(name, amount);
      }
   }
}
