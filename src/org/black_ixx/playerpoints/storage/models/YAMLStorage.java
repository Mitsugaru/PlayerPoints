package org.black_ixx.playerpoints.storage.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.IStorage;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Object that handles points storage from a file config source.
 * 
 * @author Mitsugaru
 */
public class YAMLStorage implements IStorage {

    /**
     * Plugin reference.
     */
    private PlayerPoints plugin;

    /**
     * File reference.
     */
    private File file;

    /**
     * Yaml config.
     */
    private YamlConfiguration config;

    /**
     * Points section string.
     */
    private static final String POINTS_SECTION = "Points.";

    /**
     * Constructor.
     * 
     * @param pp
     *            - Player points plugin instance.
     */
    public YAMLStorage(PlayerPoints pp) {
        plugin = pp;
        file = new File(plugin.getDataFolder().getAbsolutePath()
                + "/storage.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Save the config data.
     */
    public void save() {
        // Set config
        try {
            // Save the file
            config.save(file);
        } catch(IOException e1) {
            plugin.getLogger().warning(
                    "File I/O Exception on saving storage.yml");
            e1.printStackTrace();
        }
    }

    /**
     * Reload the config file.
     */
    public void reload() {
        try {
            config.load(file);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setPoints(String name, int points) {
        config.set(POINTS_SECTION + name, points);
        save();
        return true;
    }

    @Override
    public int getPoints(String name) {
        int points = config.getInt(POINTS_SECTION + name, 0);
        return points;
    }

    @Override
    public boolean playerEntryExists(String name) {
        return config.contains(POINTS_SECTION + name);
    }

    @Override
    public Collection<String> getPlayers() {
        return config.getConfigurationSection("Points.").getKeys(false);
    }
}
