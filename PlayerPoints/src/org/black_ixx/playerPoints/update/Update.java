package org.black_ixx.playerPoints.update;

import java.io.File;

import org.black_ixx.playerPoints.PlayerPoints;
import org.black_ixx.playerPoints.config.LocalizeConfig;
import org.black_ixx.playerPoints.config.LocalizeNode;
import org.bukkit.configuration.ConfigurationSection;

public class Update {

    private static PlayerPoints plugin;

    public static void init(PlayerPoints pp) {
	plugin = pp;
    }

    /**
     * Check if updates are necessary
     */
    public static void checkUpdate() {
	// Check if need to update
	final ConfigurationSection config = plugin.getConfig();
	if (Double.parseDouble(plugin.getDescription().getVersion()) > Double
		.parseDouble(config.getString("version"))) {
	    // Update to latest version
	    plugin.getLogger().info(
		    "Updating to v" + plugin.getDescription().getVersion());
	    update();
	}
    }

    private static void update() {
	// Grab current version
	final double ver = Double.parseDouble(plugin.getConfig().getString(
		"version"));
	if (ver < 1.5) {
	    // rename old config to storage
	    final File storage = new File(plugin.getDataFolder()
		    + File.separator + "config.yml");
	    boolean success = false;
	    try {
		success = storage.renameTo(new File(plugin.getDataFolder()
			+ File.separator + "storage.yml"));
	    } catch (SecurityException sec) {
		plugin.getLogger().severe(
			"SecurityExcpetion on renaming config.yml to storage.yml");
		sec.printStackTrace();
	    } catch (NullPointerException npe) {
		plugin.getLogger().severe(
			"NullPointerException on renaming config.yml to storage.yml");
		npe.printStackTrace();
	    }
	    if (success) {
		// Set new config with defaults
		plugin.reloadConfig();
		final ConfigurationSection config = plugin.getConfig();
		config.set("storage", "YAML");
		config.set("mysql.host", "localhost");
		config.set("mysql.port", 3306);
		config.set("mysql.database", "minecraft");
		config.set("mysql.user", "username");
		config.set("mysql.password", "pass");
		config.set("mysql.import.use", false);
		config.set("mysql.import.source", "YAML");
		config.set("vote.enabled", false);
		config.set("vote.amount", 100);
		config.set("vote.online", false);
		config.set("debug.database", false);
		plugin.saveConfig();
	    } else {
		plugin.getLogger().severe(
			"Failed to rename file config.yml to storage.yml");
	    }
	}
	if(ver < 1.52)
	{
	    final String unknownCommand = LocalizeConfig.getString("message.unknownCommand", LocalizeNode.COMMAND_UNKNOWN.getDefaultValue());
	    LocalizeConfig.set("message.unknownCommand", null);
	    LocalizeConfig.set(LocalizeNode.COMMAND_UNKNOWN.getPath(), unknownCommand);
	    LocalizeConfig.reload();
	}
	// Update version number in config.yml
	plugin.getConfig().set("version", plugin.getDescription().getVersion());
	plugin.saveConfig();
	plugin.getLogger().info("Upgrade complete");
    }
}
