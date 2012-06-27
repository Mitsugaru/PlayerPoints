package org.black_ixx.playerPoints;

import java.util.EnumMap;

import org.black_ixx.playerPoints.config.LocalizeConfig;
import org.black_ixx.playerPoints.config.LocalizeNode;
import org.black_ixx.playerPoints.config.LocalizeConfig.Flag;
import org.black_ixx.playerPoints.permissions.PermissionHandler;
import org.black_ixx.playerPoints.permissions.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commander implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command,
	    String label, String[] args) {
	final EnumMap<Flag, String> info = new EnumMap<Flag, String>(Flag.class);
	info.put(Flag.TAG, PlayerPoints.TAG);
	if (args.length == 0) {
	    showHelp(sender, info);
	} else {
	    final String com = args[0];
	    // Argument um Punkte zu adden
	    if (com.equalsIgnoreCase("give")) {
		return giveCommand(sender, args, info);
	    }
	    // Argument um Geld zu removen
	    else if (com.equalsIgnoreCase("take")) {
		return takeCommand(sender, args, info);
	    }
	    // Argument um Geld zu sehen
	    else if (com.equalsIgnoreCase("look")) {
		return lookCommand(sender, args, info);
	    }

	    // Argument um Geld zu schicken
	    else if (com.equalsIgnoreCase("pay")) {
		return payCommand(sender, args, info);
	    }
	    // Argument um Geld zu setten
	    else if (com.equalsIgnoreCase("set")) {
		return setCommand(sender, args, info);
	    }
	    // Argument um Geld zu reseten
	    else if (com.equalsIgnoreCase("reset")) {
		return resetCommand(sender, args, info);
	    }
	    // Argument um eigenes Geld zu sehen
	    else if (com.equalsIgnoreCase("me")) {
		return meCommand(sender, args, info);
	    } else {
		info.put(Flag.EXTRA, com);
		sender.sendMessage(LocalizeConfig.parseString(
			LocalizeNode.COMMAND_UNKNOWN, info));
	    }
	}
	// ENDE DES BEFEHLS /Point
	return true;
    }

    private boolean meCommand(CommandSender sender, String[] args,
	    EnumMap<Flag, String> info) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.CONSOLE_DENY, info));
	    return true;
	}
	if (!PermissionHandler.has(sender, PermissionNode.ME)) {
	    info.put(Flag.EXTRA, PermissionNode.ME.getNode());
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.PERMISSION_DENY, info));
	    return true;
	}
	if (args.length < 1) {
	    // Falsche Argumente
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.COMMAND_ME, info));
	    return true;
	}
	info.put(Flag.AMOUNT, "" + PlayerPointsAPI.look(sender.getName()));
	sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.POINTS_ME,
		info));
	return true;
    }

    private boolean payCommand(CommandSender sender, String[] args,
	    EnumMap<Flag, String> info) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.CONSOLE_DENY, info));
	    return true;
	}
	if (!PermissionHandler.has(sender, PermissionNode.PAY)) {
	    info.put(Flag.EXTRA, PermissionNode.PAY.getNode());
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.PERMISSION_DENY, info));
	    return true;
	}
	if (args.length < 3) {
	    // Falsche Argumente
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.COMMAND_PAY, info));
	    return true;
	}
	try {
	    final int intanzahl = Integer.parseInt(args[2]);
	    if (intanzahl <= 0) {
		sender.sendMessage(LocalizeConfig.parseString(
			LocalizeNode.POINTS_PAY_INVALID, info));
		return true;
	    }
	    if (PlayerPointsAPI.pay(sender.getName(), args[1], intanzahl)) {
		info.put(Flag.PLAYER, args[1]);
		info.put(Flag.AMOUNT, "" + args[2]);
		sender.sendMessage(LocalizeConfig.parseString(
			LocalizeNode.POINTS_PAY_SEND, info));
		// Send message to receiver
		final Player target = Bukkit.getServer().getPlayer(args[1]);
		if (target != null && target.isOnline()) {
		    info.put(Flag.PLAYER, sender.getName());
		    target.sendMessage(LocalizeConfig.parseString(
			    LocalizeNode.POINTS_PAY_RECEIVE, info));
		}
	    } else {
		sender.sendMessage(LocalizeConfig.parseString(
			LocalizeNode.POINTS_LACK, info));
	    }
	} catch (NumberFormatException notnumber) {
	    info.put(Flag.EXTRA, args[2]);
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.NOT_INTEGER, info));
	}
	return true;
    }

    private boolean giveCommand(CommandSender sender, String[] args,
	    EnumMap<Flag, String> info) {
	if (!PermissionHandler.has(sender, PermissionNode.GIVE)) {
	    info.put(Flag.EXTRA, PermissionNode.GIVE.getNode());
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.PERMISSION_DENY, info));
	    return true;
	}
	if (args.length < 3) {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.COMMAND_GIVE, info));
	    return true;
	}
	try {
	    final int anzahl = Integer.parseInt(args[2]);
	    if (PlayerPointsAPI.give(args[1], anzahl)) {
		info.put(Flag.PLAYER, args[1]);
		info.put(Flag.AMOUNT, "" + PlayerPointsAPI.look(args[1]));
		sender.sendMessage(LocalizeConfig.parseString(
			LocalizeNode.POINTS_SUCCESS, info));
		final Player target = Bukkit.getServer().getPlayer(args[1]);
		if (target != null && target.isOnline()) {
		    info.put(Flag.PLAYER, sender.getName());
		    info.put(Flag.AMOUNT, "" + anzahl);
		    target.sendMessage(LocalizeConfig.parseString(
			    LocalizeNode.POINTS_PAY_RECEIVE, info));
		}
	    } else {
		sender.sendMessage(LocalizeConfig.parseString(
			LocalizeNode.POINTS_FAIL, info));
	    }
	} catch (NumberFormatException notnumber) {
	    info.put(Flag.EXTRA, args[2]);
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.NOT_INTEGER, info));
	}
	return true;
    }

    private boolean takeCommand(CommandSender sender, String[] args,
	    EnumMap<Flag, String> info) {
	if (!PermissionHandler.has(sender, PermissionNode.TAKE)) {
	    info.put(Flag.EXTRA, PermissionNode.TAKE.getNode());
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.PERMISSION_DENY, info));
	    return true;
	}
	if (args.length < 3) {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.COMMAND_TAKE, info));
	    return true;
	}
	try {
	    final int intanzahl = Integer.parseInt(args[2]);
	    if (PlayerPointsAPI.take(args[1], intanzahl)) {
		info.put(Flag.PLAYER, args[1]);
		info.put(Flag.AMOUNT, "" + PlayerPointsAPI.look(args[1]));
		sender.sendMessage(LocalizeConfig.parseString(
			LocalizeNode.POINTS_SUCCESS, info));
		final Player target = Bukkit.getServer().getPlayer(args[1]);
		if (target != null && target.isOnline()) {
		    info.put(Flag.PLAYER, sender.getName());
		    if (intanzahl < 0) {
			info.put(Flag.AMOUNT, "" + intanzahl);
		    } else {
			info.put(Flag.AMOUNT, "-" + intanzahl);
		    }
		    target.sendMessage(LocalizeConfig.parseString(
			    LocalizeNode.POINTS_PAY_RECEIVE, info));
		}
	    } else {
		sender.sendMessage(LocalizeConfig.parseString(
			LocalizeNode.POINTS_FAIL, info));
	    }

	} catch (NumberFormatException notnumber) {
	    info.put(Flag.EXTRA, args[2]);
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.NOT_INTEGER, info));
	}
	return true;
    }

    private boolean lookCommand(CommandSender sender, String[] args,
	    EnumMap<Flag, String> info) {
	if (!PermissionHandler.has(sender, PermissionNode.LOOK)) {
	    info.put(Flag.EXTRA, PermissionNode.LOOK.getNode());
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.PERMISSION_DENY, info));
	    return true;
	}
	if (args.length < 2) {
	    // Falsche Argumente
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.COMMAND_LOOK, info));
	    return true;
	}
	info.put(Flag.PLAYER, args[1]);
	info.put(Flag.AMOUNT, "" + PlayerPointsAPI.look(args[1]));
	sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.POINTS_LOOK,
		info));
	return true;
    }

    private boolean setCommand(CommandSender sender, String[] args,
	    EnumMap<Flag, String> info) {
	if (!PermissionHandler.has(sender, PermissionNode.SET)) {
	    info.put(Flag.EXTRA, PermissionNode.SET.getNode());
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.PERMISSION_DENY, info));
	    return true;
	}
	if (args.length < 3) {
	    // Falsche Argumente
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.COMMAND_SET, info));
	    return true;
	}
	try {
	    int intanzahl = Integer.parseInt(args[2]);
	    if (PlayerPointsAPI.set(args[1], intanzahl)) {
		info.put(Flag.PLAYER, args[1]);
		info.put(Flag.AMOUNT, "" + intanzahl);
		sender.sendMessage(LocalizeConfig.parseString(
			LocalizeNode.POINTS_SUCCESS, info));
	    } else {
		sender.sendMessage(LocalizeConfig.parseString(
			LocalizeNode.POINTS_FAIL, info));
	    }

	} catch (NumberFormatException notnumber) {
	    info.put(Flag.EXTRA, args[2]);
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.NOT_INTEGER, info));
	}
	return true;
    }

    private boolean resetCommand(CommandSender sender, String[] args,
	    EnumMap<Flag, String> info) {
	if (!PermissionHandler.has(sender, PermissionNode.RESET)) {
	    info.put(Flag.EXTRA, PermissionNode.RESET.getNode());
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.PERMISSION_DENY, info));
	    return true;
	}
	if (args.length < 2) {
	    // Falsche Argumente
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.COMMAND_RESET, info));
	    return true;
	}
	if (PlayerPointsAPI.reset(args[1])) {
	    info.put(Flag.PLAYER, args[1]);
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.POINTS_RESET, info));
	} else {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.POINTS_FAIL, info));
	}
	return true;
    }

    private void showHelp(CommandSender sender, EnumMap<Flag, String> info) {
	sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_HEADER,
		info));
	if (PermissionHandler.has(sender, PermissionNode.ME)) {
	    sender.sendMessage(LocalizeConfig.parseString(LocalizeNode.HELP_ME,
		    info));
	}
	if (PermissionHandler.has(sender, PermissionNode.GIVE)) {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.HELP_GIVE, info));
	}
	if (PermissionHandler.has(sender, PermissionNode.TAKE)) {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.HELP_TAKE, info));
	}
	if (PermissionHandler.has(sender, PermissionNode.PAY)) {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.HELP_PAY, info));
	}
	if (PermissionHandler.has(sender, PermissionNode.LOOK)) {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.HELP_LOOK, info));
	}
	if (PermissionHandler.has(sender, PermissionNode.SET)) {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.HELP_SET, info));
	}
	if (PermissionHandler.has(sender, PermissionNode.RESET)) {
	    sender.sendMessage(LocalizeConfig.parseString(
		    LocalizeNode.HELP_RESET, info));
	}
    }
}
