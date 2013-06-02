package org.black_ixx.playerpoints.commands;

import java.util.EnumMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.models.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.PointsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Handles the set command.
 * 
 * @author Mitsugaru
 */
public class SetCommand implements PointsCommand {

    @Override
    public boolean execute(PlayerPoints plugin, CommandSender sender,
            Command command, String label, String[] args,
            EnumMap<Flag, String> info) {
        if(!PermissionHandler.has(sender, PermissionNode.SET)) {
            info.put(Flag.EXTRA, PermissionNode.SET.getNode());
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.PERMISSION_DENY, info));
            return true;
        }
        if(args.length < 2) {
            // Falsche Argumente
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.COMMAND_SET, info));
            return true;
        }
        try {
            int intanzahl = Integer.parseInt(args[1]);
            if(plugin.getAPI().set(args[0], intanzahl)) {
                info.put(Flag.PLAYER, args[0]);
                info.put(Flag.AMOUNT, "" + intanzahl);
                sender.sendMessage(LocalizeConfig.parseString(
                        LocalizeNode.POINTS_SUCCESS, info));
            } else {
                sender.sendMessage(LocalizeConfig.parseString(
                        LocalizeNode.POINTS_FAIL, info));
            }

        } catch(NumberFormatException notnumber) {
            info.put(Flag.EXTRA, args[1]);
            sender.sendMessage(LocalizeConfig.parseString(
                    LocalizeNode.NOT_INTEGER, info));
        }
        return true;
    }

}
