package org.black_ixx.playerpoints.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.StorageType;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Handler for the root plugin config.yml file.
 * 
 * @author Mitsugaru
 */
public class RootConfig {

    /**
     * Plugin instance.
     */
    private PlayerPoints plugin;
    /**
     * Database info.
     */
    public String host, port, database, user, password;
    /**
     * Vote amount.
     */
    public int voteAmount;
    /**
     * Import sql, vault and vote options.
     */
    public boolean importSQL, voteOnline, voteEnabled, vault;
    /**
     * Debug database option.
     */
    public static boolean debugDatabase;
    /**
     * Storage info.
     */
    public StorageType backend, importSource;

    /**
     * Constructor.
     * 
     * @param plugin
     *            - Plugin instance.
     */
    public RootConfig(PlayerPoints plugin) {
        this.plugin = plugin;
        // Grab config
        final ConfigurationSection config = plugin.getConfig();
        // LinkedHashmap of defaults
        final Map<String, Object> defaults = new LinkedHashMap<String, Object>();
        // TODO defaults
        defaults.put("storage", "YAML");
        defaults.put("mysql.host", "localhost");
        defaults.put("mysql.port", 3306);
        defaults.put("mysql.database", "minecraft");
        defaults.put("mysql.user", "username");
        defaults.put("mysql.password", "pass");
        defaults.put("mysql.import.use", false);
        defaults.put("mysql.import.source", "YAML");
        defaults.put("vote.enabled", false);
        defaults.put("vote.amount", 100);
        defaults.put("vote.online", false);
        defaults.put("debug.database", false);
        defaults.put("vault", false);
        defaults.put("version", plugin.getDescription().getVersion());
        // Insert defaults into config file if they're not present
        for(final Entry<String, Object> e : defaults.entrySet()) {
            if(!config.contains(e.getKey())) {
                config.set(e.getKey(), e.getValue());
            }
        }
        // Save config
        plugin.saveConfig();
        // Load settings
        loadSettings(config);
        /**
         * Storage
         */
        final String back = config.getString("storage");
        if(back.equalsIgnoreCase("sqlite")) {
            backend = StorageType.SQLITE;
        } else if(back.equalsIgnoreCase("mysql")) {
            backend = StorageType.MYSQL;
        } else {
            backend = StorageType.YAML;
        }
        /**
         * SQL info
         */
        host = config.getString("mysql.host", "localhost");
        port = config.getString("mysql.port", "3306");
        database = config.getString("mysql.database", "minecraft");
        user = config.getString("mysql.user", "user");
        password = config.getString("mysql.password", "password");
        importSQL = config.getBoolean("mysql.import.use", false);
        vault = config.getBoolean("vault", false);
        final String source = config.getString("mysql.import.source", "YAML");
        if(source.equalsIgnoreCase("SQLITE")) {
            importSource = StorageType.SQLITE;
        } else {
            importSource = StorageType.YAML;
        }
    }

    /**
     * Reload the config and the settings in memory.
     */
    public void reloadConfig() {
        // Initial relaod
        plugin.reloadConfig();
        // Grab config
        final ConfigurationSection config = plugin.getConfig();
        // Load settings
        loadSettings(config);
    }

    /**
     * Load the settings from config.
     * 
     * @param config
     *            - Config to read from.
     */
    private void loadSettings(ConfigurationSection config) {
        debugDatabase = config.getBoolean("debug.database", false);
        voteEnabled = config.getBoolean("vote.enabled", false);
        voteAmount = config.getInt("vote.amount", 100);
        voteOnline = config.getBoolean("vote.online", false);
    }

    /**
     * Get the storage type to use.
     * 
     * @return Storage type.
     */
    public StorageType getStorageType() {
        return backend;
    }
}
