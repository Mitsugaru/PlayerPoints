package org.black_ixx.playerpoints.commands;

import java.util.EnumMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.config.LocalizeConfig.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.PointsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ResetCommand implements PointsCommand {

   @Override
   public boolean execute(PlayerPoints plugin, CommandSender sender,
         Command command, String label, String[] args,
         EnumMap<Flag, String> info) {
      if(!PermissionHandler.has(sender, PermissionNode.RESET)) {
         info.put(Flag.EXTRA, PermissionNode.RESET.getNode());
         sender.sendMessage(LocalizeConfig.parseString(
               LocalizeNode.PERMISSION_DENY, info));
         return true;
      }
      if(args.length < 1) {
         // Falsche Argumente
         sender.sendMessage(LocalizeConfig.parseString(
               LocalizeNode.COMMAND_RESET, info));
         return true;
      }
      if(plugin.getAPI().reset(args[0])) {
         info.put(Flag.PLAYER, args[0]);
         sender.sendMessage(LocalizeConfig.parseString(
               LocalizeNode.POINTS_RESET, info));
      } else {
         sender.sendMessage(LocalizeConfig.parseString(
               LocalizeNode.POINTS_FAIL, info));
      }
      return true;
   }

}
