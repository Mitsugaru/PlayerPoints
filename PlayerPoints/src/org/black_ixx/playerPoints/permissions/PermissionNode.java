package org.black_ixx.playerPoints.permissions;

/**
 * Enumeration of permission nodes used through the plugin. Allows for a
 * centralized location and combats typos.
 * 
 * @author Mitsugaru
 * 
 */
public enum PermissionNode {

    GIVE(".give"), TAKE(".take"), LOOK(".look"), PAY(".pay"), SET(".set"), RESET(
	    ".reset"), ME(".me");

    private static final String prefix = "PlayerPoints";
    private String node;

    private PermissionNode(String node) {
	this.node = prefix + node;
    }

    public String getNode() {
	return node;
    }
}
