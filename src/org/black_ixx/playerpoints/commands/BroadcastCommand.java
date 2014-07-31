package org.black_ixx.playerpoints.commands;

import java.util.EnumMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.models.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.PointsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BroadcastCommand implements PointsCommand {

    @Override
    public boolean execute(PlayerPoints plugin, CommandSender sender,
            Command command, String label, String[] args,
            EnumMap<Flag, String> info) {
        if(!PermissionHandler.has(sender, PermissionNode.BROADCAST)) {
            info.put(Flag.EXTRA, PermissionNode.BROADCAST.getNode());
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.PERMISSION_DENY, info));
            return true;
        }
        if(args.length < 1) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.COMMAND_BROADCAST, info));
            return true;
        }
        String playerName = null;
        if(plugin.getModuleForClass(RootConfig.class).autocompleteOnline) {
            playerName = plugin.expandName(args[0]);
        }
        if(playerName == null) {
            playerName = args[0];
        }
        info.put(Flag.PLAYER, playerName);
        info.put(Flag.AMOUNT, "" + plugin.getAPI().look(plugin.translateNameToUUID(playerName)));
        final String message = LocalizeConfig.parseString(LocalizeNode.BROADCAST, info);
        for(Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
        return true;
    }

}
