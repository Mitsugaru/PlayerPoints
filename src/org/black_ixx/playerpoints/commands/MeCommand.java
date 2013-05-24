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
import org.bukkit.entity.Player;

/**
 * Handles the me command.
 * 
 * @author Mitsugaru
 */
public class MeCommand implements PointsCommand {

   @Override
   public boolean execute(PlayerPoints plugin, CommandSender sender,
         Command command, String label, String[] args,
         EnumMap<Flag, String> info) {
      if(!(sender instanceof Player)) {
         sender.sendMessage(LocalizeConfig.parseString(
               LocalizeNode.CONSOLE_DENY, info));
         return true;
      }
      if(!PermissionHandler.has(sender, PermissionNode.ME)) {
         info.put(Flag.EXTRA, PermissionNode.ME.getNode());
         sender.sendMessage(LocalizeConfig.parseString(
               LocalizeNode.PERMISSION_DENY, info));
         return true;
      }
      info.put(Flag.AMOUNT, "" + plugin.getAPI().look(sender.getName()));
      sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.POINTS_ME,
            info));
      return true;
   }

}
