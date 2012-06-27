package org.black_ixx.playerPoints.listeners;

import org.black_ixx.playerPoints.PlayerPoints;
import org.black_ixx.playerPoints.PlayerPointsAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

public class VotifierListener implements Listener {
    private PlayerPoints plugin;
    private int amount = 100;
    private boolean online = false;

    public VotifierListener(PlayerPoints plugin) {
	this.plugin = plugin;
	amount = plugin.getRootConfig().voteAmount;
	online = plugin.getRootConfig().voteOnline;
    }

    @EventHandler
    public void vote(VotifierEvent event) {
	if (event.getVote().getUsername() == null) {
	    return;
	}
	final String name = event.getVote().getUsername();
	boolean pay = false;
	if (online) {
	    final Player player = plugin.getServer().getPlayer(name);
	    if (player != null && player.isOnline()) {
		pay = true;
		player.sendMessage("Thanks for voting on "
			+ event.getVote().getServiceName() + "!");
		player.sendMessage(this.amount
			+ " has been added to your Points balance.");
	    }
	} else {
	    pay = true;
	}
	if (pay) {
	    PlayerPointsAPI.give(name, amount);
	}
    }
}
