package org.black_ixx.playerpoints.commands;

import java.util.EnumMap;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.LocalizeConfig;
import org.black_ixx.playerpoints.config.LocalizeNode;
import org.black_ixx.playerpoints.config.LocalizeConfig.Flag;
import org.black_ixx.playerpoints.permissions.PermissionHandler;
import org.black_ixx.playerpoints.permissions.PermissionNode;
import org.black_ixx.playerpoints.services.PointsCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
         if(plugin.getAPI().pay(sender.getName(), args[0], intanzahl)) {
            info.put(Flag.PLAYER, args[0]);
            info.put(Flag.AMOUNT, "" + args[1]);
            sender.sendMessage(LocalizeConfig.parseString(
                  LocalizeNode.POINTS_PAY_SEND, info));
            // Send message to receiver
            final Player target = Bukkit.getServer().getPlayer(args[0]);
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
