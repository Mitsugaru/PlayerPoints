package org.black_ixx.playerpoints.listeners;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.RootConfig;
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
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public VotifierListener(PlayerPoints plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void vote(VotifierEvent event) {
        RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if(event.getVote().getUsername() == null) {
            return;
        }
        final String name = event.getVote().getUsername();
        boolean pay = false;
        if(config.voteOnline) {
            final Player player = plugin.getServer().getPlayer(name);
            if(player != null && player.isOnline()) {
                pay = true;
                player.sendMessage("Thanks for voting on "
                        + event.getVote().getServiceName() + "!");
                player.sendMessage(config.voteAmount
                        + " has been added to your Points balance.");
            }
        } else {
            pay = true;
        }
        if(pay) {
            plugin.getAPI().give(name, config.voteAmount);
        }
    }
}
