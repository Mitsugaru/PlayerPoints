package org.black_ixx.playerpoints.commands;

import java.util.EnumMap;
import java.util.UUID;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.models.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.PointsCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the pay command.
 * 
 * @author Mitsugaru
 */
public class PayCommand implements PointsCommand {

    @Override
    public boolean execute(PlayerPoints plugin, CommandSender sender,
            Command command, String label, String[] args,
            EnumMap<Flag, String> info) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.CONSOLE_DENY, info));
            return true;
        }
        if(!PermissionHandler.has(sender, PermissionNode.PAY)) {
            info.put(Flag.EXTRA, PermissionNode.PAY.getNode());
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.PERMISSION_DENY, info));
            return true;
        }
        if(args.length < 2) {
            // Falsche Argumente
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.COMMAND_PAY, info));
            return true;
        }
        try {
            final int intanzahl = Integer.parseInt(args[1]);
            if(intanzahl <= 0) {
                sender.sendMessage(LocalizeConfig.parseString(
                        LocalizeNode.POINTS_PAY_INVALID, info));
                return true;
            }
            String playerName = null;
            if(plugin.getModuleForClass(RootConfig.class).autocompleteOnline) {
                playerName = plugin.expandName(args[0]);
            }
            if(playerName == null) {
                playerName = args[0];
            }
            UUID id = plugin.translateNameToUUID(playerName);
            if(plugin.getAPI().pay(((Player)sender).getUniqueId(), id, intanzahl)) {
                info.put(Flag.PLAYER, playerName);
                info.put(Flag.AMOUNT, "" + args[1]);
                sender.sendMessage(LocalizeConfig.parseString(
                        LocalizeNode.POINTS_PAY_SEND, info));
                // Send message to receiver
                final Player target = Bukkit.getServer().getPlayer(id);
                if(target != null && target.isOnline()) {
                    info.put(Flag.PLAYER, sender.getName());
                    target.sendMessage(LocalizeConfig.parseString(
                            LocalizeNode.POINTS_PAY_RECEIVE, info));
                }
            } else {
                sender.sendMessage(LocalizeConfig.parseString(
                        LocalizeNode.POINTS_LACK, info));
            }
        } catch(NumberFormatException notnumber) {
            info.put(Flag.EXTRA, args[1]);
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.NOT_INTEGER, info));
        }
        return true;
    }

}
