package org.black_ixx.playerpoints.commands;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.models.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.PointsCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveAllCommand implements PointsCommand {

    @Override
    public boolean execute(PlayerPoints plugin, CommandSender sender,
            Command command, String label, String[] args,
            EnumMap<Flag, String> info) {
        if(!PermissionHandler.has(sender, PermissionNode.GIVEALL)) {
            info.put(Flag.EXTRA, PermissionNode.GIVEALL.getNode());
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.PERMISSION_DENY, info));
            return true;
        }
        if(args.length < 1) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.COMMAND_GIVEALL, info));
            return true;
        }
        try {
            final int anzahl = Integer.parseInt(args[0]);
            info.put(Flag.AMOUNT, String.valueOf(anzahl));
            List<String> unsuccessful = new ArrayList<String>();
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player != null) {
                    if(plugin.getAPI().give(player.getUniqueId(), anzahl)) {
                        info.put(Flag.PLAYER, sender.getName());
                        player.sendMessage(LocalizeConfig.parseString(
                                LocalizeNode.POINTS_PAY_RECEIVE, info));
                    } else {
                        unsuccessful.add(player.getName());
                    }
                }
            }
            info.put(
                    Flag.PLAYER,
                    String.valueOf(Bukkit.getOnlinePlayers().size()
                            - unsuccessful.size()));
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.POINTS_SUCCESS_ALL, info));
            if(!unsuccessful.isEmpty()) {
                // TODO maybe tell them which players failed...
                info.put(Flag.PLAYER, String.valueOf(unsuccessful.size()));
                sender.sendMessage(LocalizeConfig.parseString(
                        LocalizeNode.POINTS_FAIL_ALL, info));
            }
        } catch(NumberFormatException notnumber) {
            info.put(Flag.EXTRA, args[1]);
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.NOT_INTEGER, info));
        }
        return true;
    }

}
