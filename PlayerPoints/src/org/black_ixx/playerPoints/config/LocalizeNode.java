package org.black_ixx.playerPoints.config;

public enum LocalizeNode {

    /**
     * General
     */
    PERMISSION_DENY("message.noPermission",
	    "&7%tag &cLack permission: &b%extra"), CONSOLE_DENY(
	    "message.noConsole", "&7%tag &cCannot use command as console"), NOT_INTEGER(
	    "message.notIntenger", "&7%tag &6%extra &cis not an integer"),
    /**
     * Command
     */
    COMMAND_UNKNOWN("message.command.unknown",
	    "&7%tag &cUnknown command '%extra'"), COMMAND_GIVE(
	    "message.command.give", "&7%tag &9/points give <name> <points>"), COMMAND_TAKE(
	    "message.command.take", "&7%tag &9/points take <name> <points>"), COMMAND_LOOK(
	    "message.command.look", "&7%tag &9/points look <name>"), COMMAND_PAY(
	    "message.command.pay", "&7%tag &9/points give <name> <points>"), COMMAND_SET(
	    "message.command.set", "&7%tag &9/points set <name> <points>"), COMMAND_RESET(
	    "message.command.reset", "&7%tag &9/points reset <name>"), COMMAND_ME(
	    "message.command.me", "&7%tag &9/points me"),
    /**
     * Points
     */
    POINTS_SUCCESS("message.points.success",
	    "&7%tag &9Player &a%player &9now has &a%amount &9Points"), POINTS_FAIL(
	    "message.points.fail", "&7%tag &cTransaction failed"), POINTS_LOOK(
	    "message.points.look",
	    "&7%tag &9Player &a%player &9has &a%amount &9Points"), POINTS_PAY_SEND(
	    "message.points.pay.send",
	    "&7%tag &9You have sent &a%amount &9Points to &a%player"), POINTS_PAY_RECEIVE(
	    "message.points.pay.receive",
	    "&7%tag &9You have received &a%amount &9Points from &a%player"), POINTS_PAY_INVALID(
	    "message.points.pay.invalid",
	    "&7%tag &6Cannot pay 0 or negative points."), POINTS_LACK(
	    "message.points.lack", "&7%tag &6You do not have enough Points!"), POINTS_RESET(
	    "message.points.reset",
	    "&7%tag The points of &a%player &9was successfully reset"), POINTS_ME(
	    "message.points.me", "&7%tag &9You have &a%amount &9Points"),
    /**
     * Help
     */
    HELP_HEADER("message.help.header", "&9======= &7%tag &9======="), HELP_ME(
	    "message.help.me", "&7/points me &6: Show current points"), HELP_GIVE(
	    "message.help.give",
	    "&7/points give <name> <points> &6: Generate points for given player"), HELP_TAKE(
	    "message.help.take",
	    "&7/points take <name> <points> &6: Take points from player"), HELP_LOOK(
	    "message.help.look",
	    "&7/points give <name> &6: Lookup player's points"), HELP_SET(
	    "message.help.set",
	    "&7/points set <name> <points> &6: Set player's points to amount"), HELP_RESET(
	    "message.help.reset",
	    "&7/points reset <name> &6: Reset player's points to 0"), HELP_PAY(
	    "message.help.pay",
	    "&7/points pay <name> <points> &6: Send points to given player");

    private String path, def;

    private LocalizeNode(String path, String def) {
	this.path = path;
	this.def = def;
    }

    public String getPath() {
	return path;
    }

    public String getDefaultValue() {
	return def;
    }
}
