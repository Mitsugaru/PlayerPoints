package org.black_ixx.playerPoints.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.black_ixx.playerPoints.PlayerPoints;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class YAMLStorage {

    private static PlayerPoints plugin;
    private static File file;
    private static YamlConfiguration config;

    public static void init(PlayerPoints pp) {
	plugin = pp;
	file = new File(plugin.getDataFolder().getAbsolutePath()
		+ "/storage.yml");
	config = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() {
	// Set config
	try {
	    // Save the file
	    config.save(file);
	} catch (IOException e1) {
	    plugin.getLogger().warning(
		    "File I/O Exception on saving storage.yml");
	    e1.printStackTrace();
	}
    }

    public static void reload() {
	try {
	    config.load(file);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (InvalidConfigurationException e) {
	    e.printStackTrace();
	}
    }

    public static void setPoints(String name, int points) {
	config.set("Points." + name, points);
	save();
    }

    public static int getPoints(String name) {
	int points = config.getInt("Points." + name, 0);
	return points;
    }
}
